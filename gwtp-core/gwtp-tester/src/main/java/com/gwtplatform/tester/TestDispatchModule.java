/**
 * Copyright 2010 ArcBees Inc.
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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.gwtplatform.dispatch.client.DefaultExceptionHandler;
import com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
import com.gwtplatform.dispatch.client.DispatchAsync;
import com.gwtplatform.dispatch.client.DispatchService;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.SecurityCookieAccessor;

/**
 * This class configures guice for use in test cases with a
 * {@link MockHandlerModule}.
 * 
 * @author Brendan Doherty
 * 
 */
public class TestDispatchModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(DispatchService.class).to(TestDispatchService.class).in(
        Singleton.class);
    bind(DispatchAsync.class).to(TestDispatchAsync.class);

    bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
    bind(SecurityCookieAccessor.class).to(DefaultSecurityCookieAccessor.class);
  }
}
