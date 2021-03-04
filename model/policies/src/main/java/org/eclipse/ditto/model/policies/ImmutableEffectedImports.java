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

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;
import static org.eclipse.ditto.model.base.exceptions.DittoJsonException.wrapJsonRuntimeException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;

/**
 * An immutable implementation of {@link EffectedImports}.
 */
@Immutable
final class ImmutableEffectedImports implements EffectedImports {

    private final ImportedLabels includedImportedLabels;
    private final ImportedLabels excludedImportedLabels;

    private ImmutableEffectedImports(final ImportedLabels included, final ImportedLabels excluded) {
        includedImportedLabels = included;
        excludedImportedLabels = excluded;
    }

    /**
     * Returns a new {@code EffectedImports} object of the given {@code includedImportedLabels} and {@code
     * excludedImportedLabels}.
     *
     * @param includedImportedLabels the ImportedLabels which should be added as "included".
     * @param excludedImportedLabels the ImportedLabels which should be added as "excluded".
     * @return a new {@code EffectedImports} object.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static EffectedImports of(final Iterable<String> includedImportedLabels,
            final Iterable<String> excludedImportedLabels) {

        final ImportedLabels included =
                toImportedEntries(toSet(checkNotNull(includedImportedLabels, "included imported labels")));
        final ImportedLabels excluded =
                toImportedEntries(toSet(checkNotNull(excludedImportedLabels, "excluded imported labels")));

        return new ImmutableEffectedImports(included, excluded);
    }

    private static Collection<String> toSet(final Iterable<String> iterable) {
        final Set<String> result;
        if (iterable instanceof Set) {
            result = (Set<String>) iterable;
        } else {
            result = new HashSet<>();
            iterable.forEach(result::add);
        }
        return result;
    }

    private static ImportedLabels toImportedEntries(final Collection<String> stringCollection) {
        return stringCollection.isEmpty()
                ? PoliciesModelFactory.noImportedEntries()
                : PoliciesModelFactory.newImportedEntries(stringCollection);
    }

    /**
     * Creates a new {@code EffectedImports} object from the specified JSON object.
     *
     * @param jsonObject a JSON object which provides the data for the EffectedImports to be created.
     * @return a new EffectedImports which is initialised with the extracted data from {@code jsonObject}.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.model.base.exceptions.DittoJsonException if the passed in {@code jsonObject} was not in
     * the expected 'EffectedImports' format.
     */
    public static EffectedImports fromJson(final JsonObject jsonObject) {
        checkNotNull(jsonObject, "JSON object");

        final Set<String> included =
                wrapJsonRuntimeException(() -> getImportedEntriesFor(jsonObject, JsonFields.INCLUDED));
        final Set<String> excluded =
                wrapJsonRuntimeException(() -> getImportedEntriesFor(jsonObject, JsonFields.EXCLUDED));
        return of(included, excluded);
    }

    private static Set<String> getImportedEntriesFor(final JsonObject jsonObject,
            final JsonFieldDefinition<JsonArray> effect) {

        return jsonObject.getValueOrThrow(effect)
                .stream()
                .filter(JsonValue::isString)
                .map(JsonValue::asString)
                .collect(Collectors.toSet());
    }

    @Override
    public ImportedLabels getImportedEntries(final ImportedEffect effect) {
        switch (checkNotNull(effect, "imported entry effect")) {
            case INCLUDED:
                return PoliciesModelFactory.newImportedEntries(includedImportedLabels);
            case EXCLUDED:
                return PoliciesModelFactory.newImportedEntries(excludedImportedLabels);
            default:
                throw new IllegalArgumentException("Imported effect <" + effect + "> is unknown!");
        }
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        return JsonFactory.newObjectBuilder()
                .set(JsonFields.SCHEMA_VERSION, schemaVersion.toInt(), predicate)
                .set(JsonFields.INCLUDED, includedImportedLabels.toJson(), predicate)
                .set(JsonFields.EXCLUDED, excludedImportedLabels.toJson(), predicate)
                .build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableEffectedImports that = (ImmutableEffectedImports) o;
        return Objects.equals(includedImportedLabels, that.includedImportedLabels) && Objects
                .equals(excludedImportedLabels, that.excludedImportedLabels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includedImportedLabels, excludedImportedLabels);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "includedImportedLabels=" + includedImportedLabels +
                ", excludedImportedLabels=" + excludedImportedLabels +
                "]";
    }

}