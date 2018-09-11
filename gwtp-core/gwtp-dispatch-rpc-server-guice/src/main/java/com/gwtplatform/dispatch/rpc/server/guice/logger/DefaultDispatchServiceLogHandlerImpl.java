/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.server.guice.logger;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.rpc.server.logger.AbstractDispatchServiceLogHandlerImpl;

/**
 * Guice implementation of the {@link com.gwtplatform.dispatch.rpc.server.logger.DispatchServiceLogHandler}.
 *
 * @author Filip Hrisafov
 */
@Singleton
public class DefaultDispatchServiceLogHandlerImpl extends AbstractDispatchServiceLogHandlerImpl {

    @Inject
    public DefaultDispatchServiceLogHandlerImpl(Logger logger) {
        super(logger);
    }
}
