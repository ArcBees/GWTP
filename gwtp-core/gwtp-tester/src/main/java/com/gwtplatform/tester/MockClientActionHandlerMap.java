/*
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.tester;

import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;

/**
 * This is the interface that define the map of {@code com.gwtplatform.dispatch.shared.Action} to
 * {@link ClientActionHandler} for unit testing.
 */
public interface MockClientActionHandlerMap {

    /**
     * @return the {@code com.gwtplatform.dispatch.shared.Action} class associated
     */
    Class<?> getActionClass();

    /**
     * @return the {@link ClientActionHandler} class associated
     */
    ClientActionHandler<?, ?> getClientActionHandler();

}
