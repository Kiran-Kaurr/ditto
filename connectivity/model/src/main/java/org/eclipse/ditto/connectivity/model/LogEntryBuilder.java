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
package org.eclipse.ditto.connectivity.model;

import java.time.Instant;

import javax.annotation.Nullable;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.things.model.ThingId;

/**
 * A mutable builder for a {@link LogEntry} with a fluent API.
 */
public interface LogEntryBuilder {

    /**
     *Returns this builder to allow method chaining.
 @param correlationId  correlation ID that is associated with the log entry.
     * 
     */
    LogEntryBuilder correlationId(String correlationId);

    /**
     *Returns this builder to allow method chaining.
 @param timestamp  timestamp of the log entry.
     * 
     */
    LogEntryBuilder timestamp(Instant timestamp);

    /**
     *Returns this builder to allow method chaining.
 @param logCategory  category of the log entry.
     * 
     */
    LogEntryBuilder logCategory(LogCategory logCategory);

    /**
     *Returns this builder to allow method chaining.
 @param logType  type of the log entry.
     * 
     */
    LogEntryBuilder logType(LogType logType);

    /**
     *Returns this builder to allow method chaining.
 @param logLevel  the log level.
     * 
     */
    LogEntryBuilder logLevel(LogLevel logLevel);

    /**
     *Returns this builder to allow method chaining.
 @param message  the log message.
     * 
     */
    LogEntryBuilder message(String message);

    /**
     *Returns this builder to allow method chaining.
 @param address if the log can be correlated to a known source or target, empty otherwise.
     * 
     */
    LogEntryBuilder address(@Nullable String address);

    /**
     *Returns this builder to allow method chaining.
 @param entityId entity ID if the log can be correlated to a known entity (e.g. a Thing), empty otherwise.
     * 
     */
    LogEntryBuilder entityId(@Nullable EntityId entityId);

    /**
     * @param thingId Thing ID if the log can be correlated to a known Thing, empty otherwise.
     * @return this builder to allow method chaining.
     * @deprecated replaced by {@link #entityId(org.eclipse.ditto.base.model.entity.id.EntityId)}
     */
    @Deprecated
    default LogEntryBuilder thingId(@Nullable ThingId thingId) {
        return entityId(thingId);
    }

    /**
     * Builds a new {@link LogEntry}.
     * @return the new {@link LogEntry}.
     */
    LogEntry build();

}
