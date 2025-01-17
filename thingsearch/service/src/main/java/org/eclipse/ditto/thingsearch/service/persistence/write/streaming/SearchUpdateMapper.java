/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.thingsearch.service.persistence.write.streaming;

import java.util.List;

import org.eclipse.ditto.internal.utils.akka.AkkaClassLoader;
import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.metrics.instruments.timer.StartedTimer;
import org.eclipse.ditto.internal.utils.persistence.mongo.DittoMongoClient;
import org.eclipse.ditto.thingsearch.service.common.config.DittoSearchConfig;
import org.eclipse.ditto.thingsearch.service.common.config.SearchConfig;
import org.eclipse.ditto.thingsearch.service.persistence.write.model.AbstractWriteModel;
import org.eclipse.ditto.thingsearch.service.starter.actors.MongoClientExtension;
import org.eclipse.ditto.thingsearch.service.updater.actors.MongoWriteModel;
import org.slf4j.Logger;

import akka.NotUsed;
import akka.actor.AbstractExtensionId;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.stream.javadsl.Source;

/**
 * Search Update Mapper to be loaded by reflection.
 * Can be used as an extension point to use custom map search updates.
 * Implementations MUST have a public constructor taking an actorSystem as argument.
 *
 * @since 2.1.0
 */
public abstract class SearchUpdateMapper implements Extension {

    private static final ExtensionId EXTENSION_ID = new ExtensionId();

    protected final ActorSystem actorSystem;
    protected final int maxWireVersion;

    protected SearchUpdateMapper(final ActorSystem actorSystem, final Integer maxWireVersion) {
        this.actorSystem = actorSystem;
        this.maxWireVersion = maxWireVersion;
    }

    /**
     * Gets a write model of the search update and processes it.
     *
     * @param writeModel the write model.
     * @param lastWriteModel the last write model to compute incremental update from.
     * @return Ditto write model together with its processed MongoDB write model.
     */
    public abstract Source<MongoWriteModel, NotUsed> processWriteModel(AbstractWriteModel writeModel,
            final AbstractWriteModel lastWriteModel);

    /**
     * Load a {@code SearchUpdateListener} dynamically according to the search configuration.
     *
     * @param actorSystem The actor system in which to load the listener.
     * @return The listener.
     */
    public static SearchUpdateMapper get(final ActorSystem actorSystem) {
        return EXTENSION_ID.get(actorSystem);
    }

    /**
     * Convert a write model to an incremental update model.
     *
     * @param model the write model.
     * @param logger the logger.
     * @return a singleton list of write model together with its update document, or an empty list if there is no
     * change.
     */
    protected Source<MongoWriteModel, NotUsed>
    toIncrementalMongo(final AbstractWriteModel model, final AbstractWriteModel lastWriteModel, final Logger logger) {
        try {
            final var mongoWriteModelOpt = model.toIncrementalMongo(lastWriteModel, maxWireVersion);
            if (mongoWriteModelOpt.isEmpty()) {
                logger.debug("Write model is unchanged, skipping update: <{}>", model);
                model.getMetadata().sendWeakAck(null);
                return Source.empty();
            } else {
                ConsistencyLag.startS5MongoBulkWrite(model.getMetadata());
                final var result = mongoWriteModelOpt.orElseThrow();
                logger.debug("MongoWriteModel={}", result);
                return Source.single(result);
            }
        } catch (final Exception error) {
            logger.error("Failed to compute write model " + model, error);
            try {
                model.getMetadata().getTimers().forEach(StartedTimer::stop);
            } catch (final Exception e) {
                // tolerate stopping stopped timers
            }
            return Source.empty();
        }
    }

    /**
     * ID of the actor system extension to validate the {@code SearchUpdateListener}.
     */
    private static final class ExtensionId extends AbstractExtensionId<SearchUpdateMapper> {

        @Override
        public SearchUpdateMapper createExtension(final ExtendedActorSystem system) {
            final SearchConfig searchConfig =
                    DittoSearchConfig.of(DefaultScopedConfig.dittoScoped(
                            system.settings().config()));

            final DittoMongoClient client = MongoClientExtension.get(system).getUpdaterClient();
            final int maxWireVersion = client.getMaxWireVersion();

            return AkkaClassLoader.instantiate(system, SearchUpdateMapper.class,
                    searchConfig.getSearchUpdateMapperImplementation(),
                    List.of(ActorSystem.class, Integer.class),
                    List.of(system, maxWireVersion));
        }
    }

}
