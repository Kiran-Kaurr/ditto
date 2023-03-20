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
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.json.Jsonifiable;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.ThingId;

/**
 * Represents connection log entry.
 */
@Immutable
public interface LogEntry extends Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    /**
     *Returns correlation ID that is associated with the log entry.
 
     */
    String getCorrelationId();

    /**
     *Returns timestamp of the log entry.
 
     */
    Instant getTimestamp();

    /**
     *Returns category of the log entry.
 
     */
    LogCategory getLogCategory();

    /**
     *Returns type of the log entry.
 
     */
    LogType getLogType();

    /**
     *Returns the log level.
 
     */
    LogLevel getLogLevel();

    /**
     *Returns the log message.
 
     */
    String getMessage();

    /**
     *Returns address if the log can be correlated to a known source or target, empty otherwise.
 
     */
    Optional<String> getAddress();

    /**
     *Returns entity ID if the log can be correlated to a known entity (e.g. a Thing), empty otherwise.
 
     */
    Optional<EntityId> getEntityId();

    /**
     * @return entity ID if the log can be correlated to a Thing, empty otherwise.
     * @deprecated replaced by {@link #getEntityId()}
     */
    @Deprecated
    default Optional<ThingId> getThingId() {
        return getEntityId().map(ThingId::of);
    }


    /**
     * Returns all non-hidden marked fields of this {@code LogEntry}.
     *
     * @return a JSON object representation of this LogEntry including only non-hidden marked fields.
     */
    @Override
    default JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    default JsonObject toJson(final JsonSchemaVersion schemaVersion, final JsonFieldSelector fieldSelector) {
        return toJson(schemaVersion, FieldType.notHidden()).get(fieldSelector);
    }

    /**
     * An enumeration of the known {@code JsonField}s of a {@code ConnectionMetrics}.
     */
    @Immutable
    final class JsonFields {

        /**
         * JSON field containing the correlation id.
         */
        public static final JsonFieldDefinition<String> CORRELATION_ID =
                JsonFactory.newStringFieldDefinition("correlationId", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the timestamp.
         */
        public static final JsonFieldDefinition<String> TIMESTAMP =
                JsonFactory.newStringFieldDefinition("timestamp", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the category.
         */
        public static final JsonFieldDefinition<String> CATEGORY =
                JsonFactory.newStringFieldDefinition("category", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the type.
         */
        public static final JsonFieldDefinition<String> TYPE =
                JsonFactory.newStringFieldDefinition("type", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the log level.
         */
        public static final JsonFieldDefinition<String> LEVEL =
                JsonFactory.newStringFieldDefinition("level", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the message.
         */
        public static final JsonFieldDefinition<String> MESSAGE =
                JsonFactory.newStringFieldDefinition("message", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the address.
         */
        public static final JsonFieldDefinition<String> ADDRESS =
                JsonFactory.newStringFieldDefinition("address", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the Thing ID.
         * @deprecated replaced by {@link #ENTITY_ID}
         */
        @Deprecated
        public static final JsonFieldDefinition<String> THING_ID =
                JsonFactory.newStringFieldDefinition("thingId", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the entity ID.
         * @since 2.1.0
         */
        public static final JsonFieldDefinition<String> ENTITY_ID =
                JsonFactory.newStringFieldDefinition("entityId", FieldType.REGULAR,
                        JsonSchemaVersion.V_2);

        private JsonFields() {
            throw new AssertionError();
        }

    }

}
