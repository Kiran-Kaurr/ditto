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

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.policies.PolicyIdValidator;
import org.eclipse.ditto.signals.commands.base.AbstractCommand;
import org.eclipse.ditto.signals.commands.base.CommandJsonDeserializer;

/**
 * Command which retrieves the Policy imports of a {@code Policy} based on the passed in Policy ID.
 */
@Immutable
public final class RetrievePolicyImports extends AbstractCommand<RetrievePolicyImports> implements
        PolicyQueryCommand<RetrievePolicyImports> {

    /**
     * Name of the retrieve "Retrieve Policy Imports" command.
     */
    public static final String NAME = "retrievePolicyImports";

    /**
     * Type of this command.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    private final String policyId;

    private RetrievePolicyImports(final String policyId, final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        PolicyIdValidator.getInstance().accept(policyId, dittoHeaders);
        this.policyId = policyId;
    }

    /**
     * Returns a command for retrieving all Policy imports for the given Policy ID.
     *
     * @param policyId the ID of a single Policy whose Policy imports will be retrieved by this command.
     * @param dittoHeaders the optional command headers of the request.
     * @return a Command for retrieving one Policy imports with the {@code policyId} which is readable from the passed
     * authorization context.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrievePolicyImports of(final String policyId, final DittoHeaders dittoHeaders) {
        return new RetrievePolicyImports(policyId, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrievePolicyImports} from a JSON string.
     *
     * @param jsonString the JSON string of which a new RetrievePolicyImports instance is to be created.
     * @param dittoHeaders the optional command headers of the request.
     * @return the {@code RetrievePolicyImports} which was created from the given JSON string.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static RetrievePolicyImports fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        final JsonObject jsonObject = JsonFactory.newObject(jsonString);

        return fromJson(jsonObject, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrievePolicyImports} from a JSON object.
     *
     * @param jsonObject the JSON object of which a new RetrievePolicyImports instance is to be created.
     * @param dittoHeaders the optional command headers of the request.
     * @return the {@code RetrievePolicyImports} which was created from the given JSON object.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static RetrievePolicyImports fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new CommandJsonDeserializer<RetrievePolicyImports>(TYPE, jsonObject).deserialize(() -> {
            final String policyId = jsonObject.getValueOrThrow(PolicyQueryCommand.JsonFields.JSON_POLICY_ID);

            return of(policyId, dittoHeaders);
        });
    }

    /**
     * Returns the identifier of the {@code Policy} to retrieve the {@code PolicyImports} from.
     *
     * @return the identifier of the Policy to retrieve the PolicyImports from.
     */
    @Override
    public String getId() {
        return policyId;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonPointer.of("/imports");
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {

        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(PolicyQueryCommand.JsonFields.JSON_POLICY_ID, policyId, predicate);
    }

    @Override
    public RetrievePolicyImports setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(policyId, dittoHeaders);
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
        final RetrievePolicyImports that = (RetrievePolicyImports) obj;
        return that.canEqual(this) && Objects.equals(policyId, that.policyId) && super.equals(that);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof RetrievePolicyImports;
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + ", policyId=" + policyId + "]";
    }

}