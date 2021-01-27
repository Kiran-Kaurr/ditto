/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.thingsearch.persistence.write.streaming;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.eclipse.ditto.model.things.ThingId;
import org.eclipse.ditto.services.base.config.supervision.ExponentialBackOffConfig;
import org.eclipse.ditto.services.thingsearch.common.config.PersistenceStreamConfig;
import org.eclipse.ditto.services.thingsearch.common.config.SearchConfig;
import org.eclipse.ditto.services.thingsearch.common.config.StreamCacheConfig;
import org.eclipse.ditto.services.thingsearch.common.config.StreamConfig;
import org.eclipse.ditto.services.thingsearch.common.config.StreamStageConfig;
import org.eclipse.ditto.services.thingsearch.persistence.write.model.AbstractWriteModel;
import org.eclipse.ditto.services.utils.namespaces.BlockedNamespaces;

import com.mongodb.reactivestreams.client.MongoDatabase;

import akka.NotUsed;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.MessageDispatcher;
import akka.stream.Attributes;
import akka.stream.KillSwitch;
import akka.stream.KillSwitches;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RestartSink;
import akka.stream.javadsl.RestartSource;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

/**
 * Stream from the cache of Thing changes to the persistence of the search index.
 */
public final class SearchUpdaterStream {

    private final SearchConfig searchConfig;
    private final EnforcementFlow enforcementFlow;
    private final MongoSearchUpdaterFlow mongoSearchUpdaterFlow;
    private final BulkWriteResultAckFlow bulkWriteResultAckFlow;
    private final ActorRef changeQueueActor;
    private final BlockedNamespaces blockedNamespaces;

    private SearchUpdaterStream(final SearchConfig searchConfig,
            final EnforcementFlow enforcementFlow,
            final MongoSearchUpdaterFlow mongoSearchUpdaterFlow,
            final BulkWriteResultAckFlow bulkWriteResultAckFlow,
            final ActorRef changeQueueActor,
            final BlockedNamespaces blockedNamespaces) {

        this.searchConfig = searchConfig;
        this.enforcementFlow = enforcementFlow;
        this.mongoSearchUpdaterFlow = mongoSearchUpdaterFlow;
        this.bulkWriteResultAckFlow = bulkWriteResultAckFlow;
        this.changeQueueActor = changeQueueActor;
        this.blockedNamespaces = blockedNamespaces;
    }

    /**
     * Create a restart-able SearchUpdaterStream object.
     *
     * @param searchConfig the configuration settings of the Things-Search service.
     * @param actorSystem actor system to run the stream in.
     * @param thingsShard shard region proxy of things.
     * @param policiesShard shard region proxy of policies.
     * @param updaterShard shard region of search updaters.
     * @param changeQueueActor reference of the change queue actor.
     * @param database MongoDB database.
     * @return a SearchUpdaterStream object.
     */
    public static SearchUpdaterStream of(final SearchConfig searchConfig,
            final ActorSystem actorSystem,
            final ActorRef thingsShard,
            final ActorRef policiesShard,
            final ActorRef updaterShard,
            final ActorRef changeQueueActor,
            final MongoDatabase database,
            final BlockedNamespaces blockedNamespaces) {

        final StreamConfig streamConfig = searchConfig.getUpdaterConfig().getStreamConfig();

        final StreamCacheConfig cacheConfig = streamConfig.getCacheConfig();
        final String dispatcherName = cacheConfig.getDispatcherName();
        final MessageDispatcher messageDispatcher = actorSystem.dispatchers().lookup(dispatcherName);

        final EnforcementFlow enforcementFlow =
                EnforcementFlow.of(streamConfig, thingsShard, policiesShard, messageDispatcher);

        final MongoSearchUpdaterFlow mongoSearchUpdaterFlow = MongoSearchUpdaterFlow.of(database);

        final BulkWriteResultAckFlow bulkWriteResultAckFlow = BulkWriteResultAckFlow.of(updaterShard);

        return new SearchUpdaterStream(searchConfig, enforcementFlow, mongoSearchUpdaterFlow, bulkWriteResultAckFlow,
                changeQueueActor, blockedNamespaces);
    }

    /**
     * Start a perpetual search updater stream killed only by the kill-switch.
     *
     * @param actorContext where to create actors for this stream.
     * @return kill-switch to terminate the stream.
     */
    public KillSwitch start(final ActorContext actorContext) {
        final Source<Source<AbstractWriteModel, NotUsed>, NotUsed> restartSource = createRestartSource();
        final Sink<Source<AbstractWriteModel, NotUsed>, NotUsed> restartSink = createRestartSink();
        return restartSource.viaMat(KillSwitches.single(), Keep.right())
                .toMat(restartSink, Keep.left())
                .run(actorContext.system());
    }

    private Source<Source<AbstractWriteModel, NotUsed>, NotUsed> createRestartSource() {
        final StreamConfig streamConfig = searchConfig.getUpdaterConfig().getStreamConfig();
        final StreamStageConfig retrievalConfig = streamConfig.getRetrievalConfig();

        final Source<Source<AbstractWriteModel, NotUsed>, NotUsed> source =
                ChangeQueueActor.createSource(changeQueueActor, streamConfig.getWriteInterval())
                        .via(filterMapKeysByBlockedNamespaces())
                        .via(enforcementFlow.create(retrievalConfig.getParallelism())
                                .map(writeModelSource -> writeModelSource.via(
                                        blockNamespaceFlow(SearchUpdaterStream::namespaceOfWriteModel))));

        final ExponentialBackOffConfig backOffConfig = retrievalConfig.getExponentialBackOffConfig();

        return RestartSource.withBackoff(backOffConfig.getMin(), backOffConfig.getMax(),
                backOffConfig.getRandomFactor(), () -> source);
    }

    private Sink<Source<AbstractWriteModel, NotUsed>, NotUsed> createRestartSink() {
        final StreamConfig streamConfig = searchConfig.getUpdaterConfig().getStreamConfig();
        final PersistenceStreamConfig persistenceConfig = streamConfig.getPersistenceConfig();

        final int parallelism = persistenceConfig.getParallelism();
        final int maxBulkSize = persistenceConfig.getMaxBulkSize();
        final Sink<Source<AbstractWriteModel, NotUsed>, NotUsed> sink =
                mongoSearchUpdaterFlow.start(parallelism, maxBulkSize)
                        .via(bulkWriteResultAckFlow.start(persistenceConfig.getAckDelay()))
                        .log("SearchUpdaterStream/BulkWriteResult")
                        .withAttributes(Attributes.logLevels(
                                Attributes.logLevelInfo(),
                                Attributes.logLevelWarning(),
                                Attributes.logLevelError()))
                        .to(Sink.ignore());

        final ExponentialBackOffConfig backOffConfig = persistenceConfig.getExponentialBackOffConfig();

        return RestartSink.withBackoff(backOffConfig.getMax(), backOffConfig.getMax(), backOffConfig.getRandomFactor(),
                () -> sink);
    }

    private <T> Flow<Map<ThingId, T>, Map<ThingId, T>, NotUsed> filterMapKeysByBlockedNamespaces() {
        return Flow.<Map<ThingId, T>>create()
                .flatMapConcat(map ->
                        Source.fromIterator(map.entrySet()::iterator)
                                .via(blockNamespaceFlow(entry -> entry.getKey().getNamespace()))
                                .fold(new HashMap<>(), (accumulator, entry) -> {
                                    accumulator.put(entry.getKey(), entry.getValue());
                                    return accumulator;
                                })
                );
    }

    private <T> Flow<T, T, NotUsed> blockNamespaceFlow(final Function<T, String> namespaceExtractor) {
        return Flow.<T>create()
                .flatMapConcat(element -> {
                    final String namespace = namespaceExtractor.apply(element);
                    final CompletionStage<Boolean> shouldUpdate = blockedNamespaces.contains(namespace)
                            .handle((result, error) -> result == null || !result);
                    return Source.fromCompletionStage(shouldUpdate)
                            .filter(Boolean::valueOf)
                            .map(bool -> element);
                });
    }

    private static String namespaceOfWriteModel(final AbstractWriteModel writeModel) {
        return writeModel.getMetadata().getThingId().getNamespace();
    }


}
