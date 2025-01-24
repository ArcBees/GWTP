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

import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.Proxy;

import jakarta.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Presenter}.
 */
@RunWith(JukitoRunner.class)
public class PresenterTest {

    @TestSingleton
    static class TestPresenter
            extends Presenter<View, Proxy<TestPresenter>> {
        public int revealInParentCalled;

        @Inject
        TestPresenter(EventBus eventBus, View view, Proxy<TestPresenter> proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public void revealInParent() {
            super.onReveal();
            revealInParentCalled++;
        }
    }

    // SUT
    @Inject
    TestPresenter presenter;

    @Test
    public void forceRevealWhenPresenterIsNotVisible() {
        // Given
        assertFalse(presenter.isVisible());

        // When
        presenter.forceReveal();

        // Then
        assertEquals(1, presenter.revealInParentCalled);
    }

    @Test
    public void forceRevealWhenPresenterIsVisible() {
        // Given
        presenter.internalReveal();
        assertTrue(presenter.isVisible());

        // When
        presenter.forceReveal();

        // Then
        assertEquals(0, presenter.revealInParentCalled);
    }
}
