/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client;

import org.jboss.errai.marshalling.client.api.MarshallerFramework;

/**
 * Abstract implementation extended by the generated implementation of {@link RestDispatcherController}. This is used to
 * execute additional code that do not need to be generated.
 */
public abstract class AbstractRestDispatcherController implements RestDispatcherController {
    @Override
    public void onModuleLoad() {
        MarshallerFramework.initializeDefaultSessionProvider();
    }
}
