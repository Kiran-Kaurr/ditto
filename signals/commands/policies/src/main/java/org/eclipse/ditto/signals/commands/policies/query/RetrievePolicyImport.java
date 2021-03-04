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
package org.eclipse.ditto.signals.commands.policies.query;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.policies.PolicyIdValidator;
import org.eclipse.ditto.signals.commands.base.AbstractCommand;
import org.eclipse.ditto.signals.commands.base.CommandJsonDeserializer;

/**
 * Command which retrieves the Policy import of a {@code Policy} based on the passed in Policy ID and imported Policy
 * ID.
 */
@Immutable
public final class RetrievePolicyImport extends AbstractCommand<RetrievePolicyImport> implements
        PolicyQueryCommand<RetrievePolicyImport> {

    /**
     * Name of the retrieve "Retrieve Policy Entry" command.
     */
    public static final String NAME = "retrievePolicyImport";

    /**
     * Type of this command.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<String> JSON_IMPORTED_POLICY_ID =
            JsonFactory.newStringFieldDefinition("importedPolicyId", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final String policyId;
    private final String importedPolicyId;

    private RetrievePolicyImport(final String importedPolicyId, final String policyId,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        PolicyIdValidator.getInstance().accept(policyId, dittoHeaders);
        this.policyId = checkNotNull(policyId, "Policy identifier");
        this.importedPolicyId = checkNotNull(importedPolicyId, "Imported Policy ID");
    }

    /**
     * Returns a command for retrieving a specific Policy import with the given ID and imported Policy ID.
     *
     * @param policyId the ID of a single Policy whose Policy import will be retrieved by this command.
     * @param importedPolicyId the specified importedPolicyId for which to retrieve the Policy import for.
     * @param dittoHeaders the optional command headers of the request.
     * @return a Command for retrieving one Policy import with the {@code policyId} and {@code importedPolicyId} which is
     * readable from the passed authorization context.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrievePolicyImport of(final String policyId, final String importedPolicyId,
            final DittoHeaders dittoHeaders) {
        return new RetrievePolicyImport(importedPolicyId, policyId, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrievePolicyImport} from a JSON string.
     *
     * @param jsonString the JSON string of which a new RetrievePolicyImport instance is to be created.
     * @param dittoHeaders the optional command headers of the request.
     * @return the {@code RetrievePolicyImport} which was created from the given JSON string.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static RetrievePolicyImport fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        final JsonObject jsonObject = JsonFactory.newObject(jsonString);

        return fromJson(jsonObject, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrievePolicyImport} from a JSON object.
     *
     * @param jsonObject the JSON object of which a new RetrievePolicyImport instance is to be created.
     * @param dittoHeaders the optional command headers of the request.
     * @return the {@code RetrievePolicyImport} which was created from the given JSON object.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static RetrievePolicyImport fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new CommandJsonDeserializer<RetrievePolicyImport>(TYPE, jsonObject).deserialize(() -> {
            final String policyId = jsonObject.getValueOrThrow(PolicyQueryCommand.JsonFields.JSON_POLICY_ID);
            final String importedPolicyId = jsonObject.getValueOrThrow(JSON_IMPORTED_POLICY_ID);

            return of(policyId, importedPolicyId, dittoHeaders);
        });
    }

    /**
     * Returns the identifier of the imported Policy of the {@code PolicyImport} to retrieve.
     *
     * @return the identifier of the imported Policy of the PolicyImport to retrieve.
     */
    public String getImportedPolicyId() {
        return importedPolicyId;
    }

    /**
     * Returns the identifier of the {@code Policy} to retrieve the {@code PolicyImport} from.
     *
     * @return the identifier of the Policy to retrieve the PolicyImport from.
     */
    @Override
    public String getId() {
        return policyId;
    }

    @Override
    public JsonPointer getResourcePath() {
        final String path = "/imports/" + importedPolicyId;
        return JsonPointer.of(path);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {

        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(PolicyQueryCommand.JsonFields.JSON_POLICY_ID, policyId, predicate);
        jsonObjectBuilder.set(JSON_IMPORTED_POLICY_ID, importedPolicyId, predicate);
    }

    @Override
    public RetrievePolicyImport setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(policyId, importedPolicyId, dittoHeaders);
    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067", "OverlyComplexMethod"})
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RetrievePolicyImport that = (RetrievePolicyImport) obj;
        return that.canEqual(this) && Objects.equals(policyId, that.policyId) && Objects.equals(
                importedPolicyId, that.importedPolicyId)
                && super.equals(that);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof RetrievePolicyImport;
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyId, importedPolicyId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + ", policyId=" + policyId + ", importedPolicyId=" +
                importedPolicyId +
                "]";
    }

}