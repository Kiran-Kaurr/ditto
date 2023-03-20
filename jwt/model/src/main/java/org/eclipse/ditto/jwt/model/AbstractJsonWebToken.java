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
package org.eclipse.ditto.jwt.model;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import com.google.common.base.Splitter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonParseException;
import org.eclipse.ditto.json.JsonValue;

/**
 * Abstract implementation of {@link JsonWebToken}.
 */
public abstract class AbstractJsonWebToken implements JsonWebToken {

    /**
     * Delimiter of the JSON Web Token.
     */
    private static final String JWT_DELIMITER = "\\.";

    private final String token;
    private final JsonObject header;
    private final JsonObject body;
    private final String signature;

    protected AbstractJsonWebToken(final JsonWebToken jsonWebToken) {
        checkNotNull(jsonWebToken, "JSON Web Token");

        token = jsonWebToken.getToken();
        header = jsonWebToken.getHeader();
        body = jsonWebToken.getBody();
        signature = jsonWebToken.getSignature();
    }

    protected AbstractJsonWebToken(final String token) {
        this.token = token;

        final List<String> tokenParts = Splitter.onPattern(JWT_DELIMITER).splitToList(this.token);
        final int expectedTokenPartAmount = 3;
        if (tokenParts.length != tokenParts.size()) {
            throw JwtInvalidException.newBuilder()
                    .description("The token is expected to have three parts: header, payload and signature.")
                    .build();
        }
        header = tryToDecodeJwtPart(tokenParts.get(0));
        body = tryToDecodeJwtPart(tokenParts.get(1));
        signature = tokenParts.get(2);
    }

    private static JsonObject tryToDecodeJwtPart(final String jwtPart) {
        try {
            return decodeJwtPart(jwtPart);
        } catch (final IllegalArgumentException | JsonParseException e) {
            throw JwtInvalidException.newBuilder()
                    .description("Check if your JSON Web Token has the correct format and is Base64 URL encoded.")
                    .cause(e)
                    .build();
        }
    }

    private static JsonObject decodeJwtPart(final String jwtPart) {
        final Base64.Decoder decoder = Base64.getDecoder();
        return JsonFactory.newObject(new String(decoder.decode(jwtPart), StandardCharsets.UTF_8));
    }

    @Override
    public JsonObject getHeader() {
        return header;
    }

    @Override
    public JsonObject getBody() {
        return body;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getKeyId() {
        return header.getValueOrThrow(JsonFields.KID);
    }

    @Override
    public String getIssuer() {
        return body.getValueOrThrow(JsonFields.ISS);
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public Audience getAudience() {
        final Optional<JsonValue> audience = body.getValue(JsonFields.AUD);
        return audience.map(Audience::fromJson).orElseGet(Audience::empty);
    }

    @Override
    public String getAuthorizedParty() {
        return body.getValue(JsonFields.AZP).orElseGet(String::new);
    }

    @Override
    public List<String> getScopes() {
        final String[] strings = body.getValue(JsonFields.SCOPE).map(s -> s.split(" ")).orElseGet(() -> new String[]{});
        return Arrays.stream(strings).collect(Collectors.toList());
    }

    @Override
    public Instant getExpirationTime() {
        return Instant.ofEpochSecond(body.getValueOrThrow(JsonFields.EXP));
    }

    @Override
    public boolean isExpired() {
        return Instant.now().isAfter(getExpirationTime());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractJsonWebToken)) {
            return false;
        }
        final AbstractJsonWebToken that = (AbstractJsonWebToken) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(header, that.header) &&
                Objects.equals(body, that.body) &&
                Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, header, body, signature);
    }

    @Override
    public String toString() {
        return "token=" + token + ", header=" + header + ", body=" + body + ", signature=" + signature;
    }

}
