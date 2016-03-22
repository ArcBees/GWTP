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

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.tester.TestView.Binder;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(JukitoRunner.class)
public class MockingBinderTest {
    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        /**
         * Test {@link Binder} delegating createAndBindUi to {@link MockingBinder}.
         */
        static class MyTestBinder extends MockingBinder<Widget, TestView> implements Binder {
            @Inject
            MyTestBinder(MockitoMockFactory mockitoMockFactory) {
                super(Widget.class, mockitoMockFactory);
            }
        }

        @Override
        protected void configureTest() {
            GWTMockUtilities.disarm();
            bind(Binder.class).to(MyTestBinder.class);
        }
    }

    // SUT
    @Inject
    TestView view;

    @Inject
    Binder binder;

    @AfterClass
    public static void cleanup() {
        GWTMockUtilities.restore();
    }

    @Test
    public void mockNullTest() {
        // THEN
        assertNotNull(view);
        assertNotNull(view.mainPanel);
        assertNotNull(view.someField);
    }

    @Test
    public void mockVerification1Test() {
        // WHEN
        view.mainPanel.add(view.someField);

        // THEN
        verify(view.mainPanel).add(view.someField);
    }

    @Test
    public void mockVerification2Test() {
        // GIVEN
        String someText = "some text";

        // WHEN
        view.someField.setText(someText);

        // THEN
        verify(view.someField).setText(someText);
    }

    @Test
    public void mockVerification3Test() {
        // GIVEN
        Widget widget = binder.createAndBindUi(view);

        // WHEN
        widget.asWidget();

        // THEN
        verify(widget).asWidget();
    }
}
