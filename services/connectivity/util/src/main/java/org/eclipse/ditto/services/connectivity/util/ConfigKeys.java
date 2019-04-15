/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.services.connectivity.util;

import javax.annotation.concurrent.Immutable;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This class encloses everything regarding configuration keys.
 */
public final class ConfigKeys {

    static final String CONNECTIVITY_PREFIX = "ditto.connectivity.";
    private static final String ENABLED_SUFFIX = "enabled";

    /**
     * Key of the uri for mongodb.
     */
    public static final String MONGO_URI = "akka.contrib.persistence.mongodb.mongo.mongouri";

    /**
     * Configuration Keys for HTTP.
     */
    @Immutable
    public static final class Http {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "http.";

        /**
         * Key of the hostname value of a HTTP service.
         */
        public static final String HOSTNAME = PREFIX + "hostname";

        /**
         * Key of the port number value of a HTTP service.
         */
        public static final String PORT = PREFIX + "port";

        private Http() {
            throw new AssertionError();
        }

    }

    /**
     * Configuration Keys for Cluster.
     */
    @Immutable
    public static final class Cluster {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "cluster.";

        /**
         * Key of the how many shards should be used in the cluster.
         */
        public static final String NUMBER_OF_SHARDS = PREFIX + "number-of-shards";

        private Cluster() {
            throw new AssertionError();
        }

    }

    /**
     * Configuration keys for the health check.
     */
    @Immutable
    public static final class HealthCheck {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "health-check.";

        private static final String PERSISTENCE_PREFIX = PREFIX + "persistence.";

        /**
         * The timeout of the health check for persistence. If the persistence takes longer than that to respond, it is
         * considered "DOWN".
         */
        public static final String PERSISTENCE_TIMEOUT = PERSISTENCE_PREFIX + "timeout";

        /**
         * Whether the health check for persistence should be enabled or not.
         */
        public static final String PERSISTENCE_ENABLED = PERSISTENCE_PREFIX + ENABLED_SUFFIX;

        /**
         * The interval of the health check.
         */
        public static final String INTERVAL = PREFIX + "interval";

        /**
         * Whether the health check should be enabled (globally) or not.
         */
        public static final String ENABLED = PREFIX + ENABLED_SUFFIX;

        private HealthCheck() {
            throw new AssertionError();
        }

    }

    /**
     * Configuration keys for Connection.
     */
    @Immutable
    public static final class Connection {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "connection.";

        private static final String SUPERVISOR_PREFIX = PREFIX + "supervisor.";

        private static final String SUPERVISOR_EXPONENTIAL_BACKOFF = SUPERVISOR_PREFIX + "exponential-backoff.";

        /**
         * The random factor of the exponential back-off strategy.
         */
        public static final String
                SUPERVISOR_EXPONENTIAL_BACKOFF_RANDOM_FACTOR = SUPERVISOR_EXPONENTIAL_BACKOFF + "random-factor";

        /**
         * The maximal exponential back-off duration.
         */
        public static final String SUPERVISOR_EXPONENTIAL_BACKOFF_MAX = SUPERVISOR_EXPONENTIAL_BACKOFF + "max";

        /**
         * The minimal exponential back-off duration.
         */
        public static final String SUPERVISOR_EXPONENTIAL_BACKOFF_MIN = SUPERVISOR_EXPONENTIAL_BACKOFF + "min";

        private Connection() {
            throw new AssertionError();
        }

    }

    /**
     * Configuration keys for Reconnect.
     */
    @Immutable
    public static final class Reconnect {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "reconnect.";

        /**
         * Initial delay for reconnecting the connections after the ReconnectActor has been started.
         */
        public static final String RECONNECT_INITIAL_DELAY = PREFIX + "initial-delay";

        /**
         * Interval for trying to reconnect all started connections.
         */
        public static final String RECONNECT_INTERVAL = PREFIX + "interval";

        /**
         * Frequency (duration) of recovery. Used to limit the recovery rate.
         */
        public static final String RECONNECT_RATE_FREQUENCY = PREFIX + "rate.frequency";

        /**
         * Number of entities recovered per batch. Used to limit the recovery rate.
         */
        public static final String RECONNECT_RATE_ENTITIES = PREFIX + "rate.entities";

        private Reconnect() {
            throw new AssertionError();
        }

    }

    /**
     * Configuration keys for Client.
     */
    @Immutable
    public static final class Client {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "client.";

        /**
         * Duration after the init process is triggered (in case no connect command was received by the client actor).
         */
        public static final String INIT_TIMEOUT = PREFIX + "init-timeout";

        private Client() {
            throw new AssertionError();
        }

    }

    /**
     * TODO: this is just a workaround until issue #350 if finished.
     *  Afterwards it should be possible to use a configreader (with AbstractConfigReader#getChildOrEmpty).
     *
     * Configuration keys for Monitoring.
     */
    @Immutable
    public static final class Monitoring {

        private static final String PREFIX = CONNECTIVITY_PREFIX + "monitoring";

        public static MonitoringConfigReader fromRawConfig(final Config rawConfig) {
            if (rawConfig.hasPath(PREFIX)) {
                return new MonitoringConfigReader(rawConfig.getConfig(PREFIX));
            }
            return new MonitoringConfigReader(ConfigFactory.empty());
        }

        private Monitoring() {
            throw new AssertionError();
        }

    }

    /*
     * This class is not designed for instantiation.
     */
    private ConfigKeys() {
        throw new AssertionError();
    }

}
