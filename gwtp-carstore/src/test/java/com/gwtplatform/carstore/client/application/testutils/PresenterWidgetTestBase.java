/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.testutils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;

/**
 * Basic configuration for PresenterWidgetTest, you test must extends this
 * class, or don't forget to inject everything inside your presenter's test.
 * 
 * @author Christian Goudreau
 */
public abstract class PresenterWidgetTestBase {
    @Inject
    public EventBus eventBus;
    @Inject
    public RelayingDispatcher dispatcher;
    @Inject
    @Named("mock")
    public DispatchAsync mockDispatcher;
}
