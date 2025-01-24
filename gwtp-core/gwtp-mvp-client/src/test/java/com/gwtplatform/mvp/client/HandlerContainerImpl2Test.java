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

package com.gwtplatform.mvp.client;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.inject.Inject;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link HandlerContainerImpl}.
 */
@RunWith(JukitoRunner.class)
public class HandlerContainerImpl2Test {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(AutobindDisable.class).toInstance(new AutobindDisable(true));
        }
    }

    // SUT
    @Inject
    HandlerContainerImpl handlerContainer;

    @Test
    public void shouldNotBindDefaultHandlerContainerOnInjection() {
        // Given
        // HandlerContainerImpl is injected

        // When, Then
        // the bind method should be injected at creation time
        assertFalse(handlerContainer.isBound());
    }
}
