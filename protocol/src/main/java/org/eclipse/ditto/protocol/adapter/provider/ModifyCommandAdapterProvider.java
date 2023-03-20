/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.protocol.adapter.provider;

import org.eclipse.ditto.protocol.adapter.Adapter;
import org.eclipse.ditto.base.model.signals.Signal;
import org.eclipse.ditto.base.model.signals.commands.CommandResponse;

/**
 * Interface providing the modify command adapter and the modify command response adapter.
 *
 * @param <M> the type of modify commands
 * @param <R> the type of modify command responses
 */
interface ModifyCommandAdapterProvider<M extends Signal<?>, R extends CommandResponse<?>> {

    /**
     *Returns the modify command adapter.
 
     */
    Adapter<M> getModifyCommandAdapter();

    /**
     *Returns the modify command response adapter.
 
     */
    Adapter<R> getModifyCommandResponseAdapter();

}
