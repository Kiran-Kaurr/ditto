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
package org.eclipse.ditto.signals.commands.policies.modify;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.policies.PoliciesModelFactory;
import org.eclipse.ditto.model.policies.PolicyIdValidator;
import org.eclipse.ditto.model.policies.PolicyImport;
import org.eclipse.ditto.signals.commands.base.AbstractCommand;
import org.eclipse.ditto.signals.commands.base.CommandJsonDeserializer;
import org.eclipse.ditto.signals.commands.policies.PolicyCommandSizeValidator;

/**
 * This command modifies a {@link PolicyImport}.
 */
@Immutable
public final class ModifyPolicyImport extends AbstractCommand<ModifyPolicyImport> implements
        PolicyModifyCommand<ModifyPolicyImport> {

    /**
     * NAME of this command.
     */
    public static final String NAME = "modifyPolicyImport";

    /**
     * Type of this command.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<String> JSON_IMPORTED_POLICY_ID =
            JsonFactory.newStringFieldDefinition("importedPolicyId", FieldType.REGULAR, JsonSchemaVersion.V_2);

    static final JsonFieldDefinition<JsonObject> JSON_POLICY_IMPORT =
            JsonFactory.newJsonObjectFieldDefinition("policyImport", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final String policyId;
    private final PolicyImport policyImport;

    private ModifyPolicyImport(final String policyId, final PolicyImport policyImport, final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        PolicyIdValidator.getInstance().accept(policyId, dittoHeaders);
        this.policyId = policyId;
        this.policyImport = policyImport;

        PolicyCommandSizeValidator.getInstance().ensureValidSize(() -> policyImport.toJsonString().length(), () ->
                dittoHeaders);
    }

    /**
     * Creates a command for modifying a {@code PolicyImport}.
     *
     * @param policyId the identifier of the Policy.
     * @param policyImport the PolicyImport to modify.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ModifyPolicyImport of(final String policyId, final PolicyImport policyImport,
            final DittoHeaders dittoHeaders) {

        Objects.requireNonNull(policyId, "The Policy identifier must not be null!");
        Objects.requireNonNull(policyImport, "The PolicyImport must not be null!");
        return new ModifyPolicyImport(policyId, policyImport, dittoHeaders);
    }

    /**
     * Creates a command for modifying a {@code PolicyImport} from a JSON string.
     *
     * @param jsonString the JSON string of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static ModifyPolicyImport fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        return fromJson(JsonFactory.newObject(jsonString), dittoHeaders);
    }

    /**
     * Creates a command for modifying a {@code PolicyImport} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static ModifyPolicyImport fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new CommandJsonDeserializer<ModifyPolicyImport>(TYPE, jsonObject).deserialize(() -> {
            final String policyId = jsonObject.getValueOrThrow(PolicyModifyCommand.JsonFields.JSON_POLICY_ID);
            final String policyImportLabel = jsonObject.getValueOrThrow(JSON_IMPORTED_POLICY_ID);
            final JsonObject policyImportJsonObject = jsonObject.getValueOrThrow(JSON_POLICY_IMPORT);
            final PolicyImport policyImport =
                    PoliciesModelFactory.newPolicyImport(policyImportLabel, policyImportJsonObject);

            return of(policyId, policyImport, dittoHeaders);
        });
    }

    /**
     * Returns the {@code PolicyImport} to modify.
     *
     * @return the PolicyImport to modify.
     */
    public PolicyImport getPolicyImport() {
        return policyImport;
    }

    /**
     * Returns the identifier of the {@code Policy} whose {@code PolicyImport} to modify.
     *
     * @return the identifier of the Policy whose PolicyImport to modify.
     */
    @Override
    public String getId() {
        return policyId;
    }

    @Override
    public Optional<JsonValue> getEntity(final JsonSchemaVersion schemaVersion) {
        return Optional.of(policyImport.toJson(schemaVersion, FieldType.regularOrSpecial()));
    }

    @Override
    public JsonPointer getResourcePath() {
        final String path = "/imports/" + policyImport.getImportedPolicyId();
        return JsonPointer.of(path);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {

        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(PolicyModifyCommand.JsonFields.JSON_POLICY_ID, policyId, predicate);
        jsonObjectBuilder.set(JSON_IMPORTED_POLICY_ID, policyImport.getImportedPolicyId(), predicate);
        jsonObjectBuilder.set(JSON_POLICY_IMPORT, policyImport.toJson(schemaVersion, thePredicate), predicate);
    }

    @Override
    public Category getCategory() {
        return Category.MODIFY;
    }

    @Override
    public ModifyPolicyImport setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(policyId, policyImport, dittoHeaders);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ModifyPolicyImport;
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || getClass() != obj.getClass()) {
            return false;
        }
        final ModifyPolicyImport that = (ModifyPolicyImport) obj;
        return that.canEqual(this) && Objects.equals(policyId, that.policyId)
                && Objects.equals(policyImport, that.policyImport) && super.equals(obj);
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyId, policyImport);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + ", policyId=" + policyId + ", policyImport="
                + policyImport + "]";
    }

}