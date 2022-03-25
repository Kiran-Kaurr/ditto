/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.connectivity.service.messaging.amqp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import org.eclipse.ditto.connectivity.model.Connection;
import org.eclipse.ditto.connectivity.model.UserPasswordCredentials;

import akka.http.javadsl.model.Uri;

/**
 * Supplier of optional username-password credentials.
 */
@FunctionalInterface
public interface PlainCredentialsSupplier {

    /**
     * Get the username-password credentials of a connection.
     *
     * @param connection the connection.
     * @param doubleDecodingEnabled whether double decoding of usernames and passwords is enabled.
     * @return the optional credentials.
     */
    Optional<UserPasswordCredentials> get(Connection connection, boolean doubleDecodingEnabled);

    /**
     * Remove userinfo from a connection URI.
     *
     * @param uri the URI.
     * @return the URI without userinfo.
     */
    default String getUriWithoutUserinfo(final String uri) {
        return Uri.create(uri).userInfo("").toString();
    }

    /**
     * Create a {@code PlainCredentialsSupplier} from the URI of the connection.
     *
     * @return the URI.
     */
    static PlainCredentialsSupplier fromUri() {
        return (connection, doubleDecodingEnabled) ->
                connection.getUsername().flatMap(username ->
                        connection.getPassword().map(password -> {
                            final String u =
                                    doubleDecodingEnabled ? tryDecodeUriComponent(username) : username;
                            final String p =
                                    doubleDecodingEnabled ? tryDecodeUriComponent(password) : password;
                                return UserPasswordCredentials.newInstance(u, p);}));
    }

    private static String tryDecodeUriComponent(final String string) {
        try {
            final String withoutPlus = string.replace("+", "%2B");
            return URLDecoder.decode(withoutPlus, "UTF-8");
        } catch (final IllegalArgumentException | UnsupportedEncodingException e) {
            return string;
        }
    }

}
