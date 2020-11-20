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
package org.eclipse.ditto.services.utils.pubsub.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.services.utils.config.ConfigWithFallback;

import com.typesafe.config.Config;

/**
 * This class is the default implementation of {@link org.eclipse.ditto.services.utils.pubsub.config.PubSubConfig}.
 */
@Immutable
final class DefaultPubSubConfig implements PubSubConfig {

    /**
     * Path of the Suite Auth configuration settings.
     */
    private static final String CONFIG_PATH = "pubsub";

    private final String seed;
    private final Duration restartDelay;
    private final Duration updateInterval;

    private DefaultPubSubConfig(final ConfigWithFallback config) {
        seed = config.getString(ConfigValue.SEED.getConfigPath());
        restartDelay = config.getDuration(ConfigValue.RESTART_DELAY.getConfigPath());
        updateInterval = config.getDuration(ConfigValue.UPDATE_INTERVAL.getConfigPath());
    }

    static PubSubConfig of(final Config config) {
        return new DefaultPubSubConfig(ConfigWithFallback.newInstance(config, CONFIG_PATH, ConfigValue.values()));
    }

    @Override
    public String getSeed() {
        return seed;
    }

    @Override
    public Duration getRestartDelay() {
        return restartDelay;
    }

    @Override
    public Duration getUpdateInterval() {
        return updateInterval;
    }

    private String[] getFieldNames() {
        return new String[]{"seed", "restartDelay", "updateInterval"};
    }

    private Object[] getFieldValues() {
        return new Object[]{seed, restartDelay, updateInterval};
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (other instanceof DefaultPubSubConfig) {
            final DefaultPubSubConfig that = (DefaultPubSubConfig) other;
            return Arrays.asList(getFieldValues()).equals(Arrays.asList(that.getFieldValues()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFieldValues());
    }

    @Override
    public String toString() {
        final String[] fields = getFieldNames();
        final Object[] values = getFieldValues();
        return getClass().getSimpleName() +
                "[" +
                IntStream.range(0, fields.length)
                        .mapToObj(i -> fields[i] + "=" + values[i])
                        .collect(Collectors.joining(",")) +
                "]";
    }
}
