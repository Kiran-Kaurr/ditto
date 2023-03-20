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
package org.eclipse.ditto.connectivity.api;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

import org.eclipse.ditto.base.model.auth.AuthorizationContext;
import org.eclipse.ditto.base.model.headers.DittoHeaderDefinition;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.connectivity.model.EnforcementFilter;
import org.eclipse.ditto.connectivity.model.HeaderMapping;
import org.eclipse.ditto.connectivity.model.PayloadMapping;
import org.eclipse.ditto.connectivity.model.Source;
import org.eclipse.ditto.protocol.TopicPath;
import org.eclipse.ditto.base.model.signals.Signal;

/**
 * Simple wrapper around the headers and the payload received from or sent to external AMQP (0.9 or 1.0)
 * sources/targets.
 */
public interface ExternalMessage {

    /**
     * Message header for the Content-Type.
     */
    String CONTENT_TYPE_HEADER = DittoHeaderDefinition.CONTENT_TYPE.getKey();

    /**
     * Message header for the reply to address. MUST be lower-case.
     * "reply-to" is a standard internet message header (RFC-5322).
     */
    String REPLY_TO_HEADER = DittoHeaderDefinition.REPLY_TO.getKey();

    /**
     *Returns the headers of the ExternalMessage.
 
     */
    Map<String, String> getHeaders();

    /**
     *Returns new instance of {@link ExternalMessage} including the provided header.
 @param key the header key
     * @param value the header value
     * 
     */
    ExternalMessage withHeader(String key, String value);

    /**
     *Returns new instance of {@link ExternalMessage} including the provided headers.
 @param additionalHeaders headers added to message headers
     * 
     */
    ExternalMessage withHeaders(Map<String, String> additionalHeaders);

    /**
     *Returns new instance of {@link ExternalMessage} including the provided TopicPath.
 @param topicPath the {@link TopicPath} to set in the new built ExternalMessage
     * 
     */
    ExternalMessage withTopicPath(TopicPath topicPath);

    /**
     *Returns the optional value of the Content-Type header.
 
     */
    default Optional<String> findContentType() {
        return findHeaderIgnoreCase(CONTENT_TYPE_HEADER);
    }

    /**
     *Returns the optional value of the specified header {@code key}.
 @param key the key to look up in the headers
     * 
     */
    Optional<String> findHeader(String key);

    /**
     *Returns the optional value of the specified header {@code key}.
 @param key the key to look up in the headers case insensitively
     * 
     */
    Optional<String> findHeaderIgnoreCase(String key);

    /**
     *Returns whether this ExternalMessage is a text message.
 
     */
    boolean isTextMessage();

    /**
     *Returns whether this ExternalMessage is a bytes message.
 
     */
    boolean isBytesMessage();

    /**
     *Returns the text payload.
 
     */
    Optional<String> getTextPayload();

    /**
     *Returns the bytes payload.
 
     */
    Optional<ByteBuffer> getBytePayload();

    /**
     *Returns the PayloadType of this ExternalMessage.
 
     */
    PayloadType getPayloadType();

    /**
     *Returns {@code true} if this message is a response.
 
     */
    boolean isResponse();

    /**
     *Returns {@code true} if this message is an error.
 
     */
    boolean isError();

    /**
     *Returns the {@link AuthorizationContext} assigned to this message.
 
     */
    Optional<AuthorizationContext> getAuthorizationContext();

    /**
     *Returns the {@link TopicPath} assigned to this message.
 
     */
    Optional<TopicPath> getTopicPath();

    /**
     *Returns the required data to apply the enforcement (if enforcement is enabled), empty otherwise.
 
     */
    Optional<EnforcementFilter<Signal<?>>> getEnforcementFilter();

    /**
     *Returns the optional header mapping.
 
     */
    Optional<HeaderMapping> getHeaderMapping();

    /**
     *Returns the payload mapping that is applied for this message.
 
     */
    Optional<PayloadMapping> getPayloadMapping();

    /**
     *Returns optional source address, where this message was received.
 
     */
    Optional<String> getSourceAddress();

    /**
     * @return optional source, where this message was received
     * @since 1.2.0
     */
    Optional<Source> getSource();

    /**
     *Returns ditto headers of the signal that created this external message if any.
 Use those headers when sending error back into the Ditto cluster.
 
     */
    DittoHeaders getInternalHeaders();

    /**
     * The known payload types of ExternalMessages.
     */
    enum PayloadType {
        TEXT,
        BYTES,
        TEXT_AND_BYTES,
        UNKNOWN
    }
}
