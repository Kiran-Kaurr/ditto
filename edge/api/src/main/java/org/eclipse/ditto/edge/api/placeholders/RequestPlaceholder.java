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
package org.eclipse.ditto.edge.api.placeholders;

import org.eclipse.ditto.base.model.auth.AuthorizationContext;
import org.eclipse.ditto.placeholders.Placeholder;

/**
 * A {@link Placeholder} implementation that replaces {@code request} related things based on an
 * {@link AuthorizationContext}.
 */
public interface RequestPlaceholder extends Placeholder<AuthorizationContext> {

    static RequestPlaceholder getInstance() {
        return ImmutableRequestPlaceholder.INSTANCE;
    }

}
