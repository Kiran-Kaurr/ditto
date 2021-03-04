/*
 * Copyright (c) 2017-2018 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.model.policies;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.base.json.Jsonifiable;

/**
 * Holds {@link ImportedLabels} for {@link ImportedEffect}s (included/excluded).
 */
public interface EffectedImports extends Jsonifiable.WithFieldSelectorAndPredicate<JsonField> {

    /**
     * Returns a new {@code EffectedImports} containing the given {@code includedEntries} and {@code
     * excludedEntries}.
     *
     * @param includedEntries the ImportedLabels which should be included, may be {@code null}.
     * @param excludedEntries the ImportedLabels which should be excluded, may be {@code null}.
     * @return the new {@code EffectedImports}.
     */
    static EffectedImports newInstance(@Nullable final Iterable<String> includedEntries,
            @Nullable final Iterable<String> excludedEntries) {

        return PoliciesModelFactory.newEffectedImportedEntries(includedEntries, excludedEntries);
    }

    /**
     * EffectedImports is only available in JsonSchemaVersion V_2.
     *
     * @return the supported JsonSchemaVersions of EffectedImports.
     */
    @Override
    default JsonSchemaVersion[] getSupportedSchemaVersions() {
        return new JsonSchemaVersion[]{JsonSchemaVersion.V_2};
    }

    /**
     * Returns the {@link ImportedLabels} which are valid for the passed {@code effect}.
     *
     * @param effect the ImportedEffect for which to return the ImportedLabels.
     * @return the ImportedLabels which are valid for the passed effect.
     * @throws NullPointerException if {@code effect} is {@code null}.
     * @throws IllegalArgumentException if {@code effect} is unknown.
     */
    ImportedLabels getImportedEntries(ImportedEffect effect);

    /**
     * Returns the included {@link ImportedLabels}.
     *
     * @return the included ImportedLabels.
     */
    default ImportedLabels getIncludedImportedEntries() {
        return getImportedEntries(ImportedEffect.INCLUDED);
    }

    /**
     * Returns the excluded {@link ImportedLabels}.
     *
     * @return the excluded ImportedLabels.
     */
    default ImportedLabels getExcludedImportedEntries() {
        return getImportedEntries(ImportedEffect.EXCLUDED);
    }

    /**
     * Returns all non hidden marked fields of this EffectedImports.
     *
     * @return a JSON object representation of this EffectedImports including only non hidden marked fields.
     */
    @Override
    default JsonObject toJson() {
        return toJson(FieldType.notHidden());
    }

    @Override
    default JsonObject toJson(final JsonSchemaVersion schemaVersion, final JsonFieldSelector fieldSelector) {
        return toJson(schemaVersion, FieldType.regularOrSpecial()).get(fieldSelector);
    }

    /**
     * An enumeration of the known {@link JsonField}s of a EffectedImports.
     */
    @Immutable
    final class JsonFields {

        /**
         * JSON field containing the {@link JsonSchemaVersion}.
         */
        public static final JsonFieldDefinition<Integer> SCHEMA_VERSION =
                JsonFactory.newIntFieldDefinition(JsonSchemaVersion.getJsonKey(), FieldType.SPECIAL, FieldType.HIDDEN,
                        JsonSchemaVersion.V_2);

        /**
         * JSON field containing the EffectedImports's {@code included} ImportedLabels.
         */
        public static final JsonFieldDefinition<JsonArray> INCLUDED =
                JsonFactory.newJsonArrayFieldDefinition("included", FieldType.REGULAR, JsonSchemaVersion.V_2);

        /**
         * JSON field containing the EffectedImports's {@code excluded} ImportedLabels.
         */
        public static final JsonFieldDefinition<JsonArray> EXCLUDED =
                JsonFactory.newJsonArrayFieldDefinition("excluded", FieldType.REGULAR, JsonSchemaVersion.V_2);

        private JsonFields() {
            throw new AssertionError();
        }

    }

}