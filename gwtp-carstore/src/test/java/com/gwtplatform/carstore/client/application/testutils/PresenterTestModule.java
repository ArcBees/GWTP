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

import org.jukito.JukitoModule;
import org.jukito.TestSingleton;

import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.AutobindDisable;

/**
 * Base module to use while testing presenters. {@link AutomockingModule} is
 * used. Your configuration module must extends this class.
 * 
 * @author Christian Goudreau
 */
public abstract class PresenterTestModule extends JukitoModule {
    @Override
    protected void configureTest() {
        bindNamedMock(DispatchAsync.class, "mock").in(TestSingleton.class);
        bind(DispatchAsync.class).to(RelayingDispatcher.class);
        bind(RelayingDispatcher.class).in(TestSingleton.class);

        configurePresenterTest();

        bind(AutobindDisable.class).toInstance(new AutobindDisable(true));
    }

    protected abstract void configurePresenterTest();
}
