/*
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

package com.gwtplatform.dispatch.rpc.server;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.dispatch.rpc.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.rpc.server.guice.actionvalidator.DefaultActionValidator;
import com.gwtplatform.dispatch.rpc.shared.ServiceException;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(JukitoRunner.class)
public class ActionThrownByHandlerTest {
    public static class MyModule extends JukitoModule {
        @Override
        protected void configureTest() {
            install(new ServiceModule(DefaultActionValidator.class));
        }
    }

    @Inject
    DispatchServiceImpl service;

    @Test
    public void exceptionThrownByHandlerIsNotWrappedInActionException() throws ServiceException {
        try {
            service.execute("", new SomeAction());
            fail();
        } catch (ActionException e) {
            assertThat(e, instanceOf(ActionExceptionThrownByHandler.class));
            assertEquals(0, e.getStackTrace().length);
            assertEquals(0, e.getCause().getStackTrace().length);
        }
    }
}
