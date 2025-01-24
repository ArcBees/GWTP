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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ViewImpl}.
 * Created by Boris on 22/05/2016.
 */
@RunWith(JukitoRunner.class)
public class ViewImplTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            GWTMockUtilities.disarm();
            forceMock(Widget.class);
        }
    }

    IsWidget widget;
    MockInsertPanel container;
    OrderedSlot<?> slot;
    TestViewImpl view;

    @Before
    public void init() {
        widget = mock(Widget.class);
        container = mock(MockInsertPanel.class);
        slot = mock(OrderedSlot.class);

        view = new TestViewImpl();

        final Widget c = mock(Widget.class);
        when(widget.asWidget()).thenReturn(c);

        view.initWidget(widget);
        view.bindSlot(slot, container);
    }

    @Test
    public void testInsertSingle() {
        final Widget c = mock(Widget.class);
        final ComparableContent content = new ComparableContent("TEST", c);
        view.addToSlot(slot, content);

        verify(container).insert(c, 0);
    }

    @Test
    public void testInsertBack() {
        final Widget a = mock(Widget.class);
        final Widget b = mock(Widget.class);
        final Widget c = mock(Widget.class);
        final ComparableContent aaa = new ComparableContent("AAA", a);
        final ComparableContent bbb = new ComparableContent("BBB", b);
        final ComparableContent ccc = new ComparableContent("CCC", c);

        view.addToSlot(slot, aaa);
        view.addToSlot(slot, bbb);
        view.addToSlot(slot, ccc);

        final InOrder inOrder = inOrder(container);
        inOrder.verify(container).insert(a, 0);
        inOrder.verify(container).insert(b, 1);
        inOrder.verify(container).insert(c, 2);
    }

    @Test
    public void testInsertFront() {
        final Widget a = mock(Widget.class);
        final Widget b = mock(Widget.class);
        final Widget c = mock(Widget.class);
        final ComparableContent aaa = new ComparableContent("AAA", a);
        final ComparableContent bbb = new ComparableContent("BBB", b);
        final ComparableContent ccc = new ComparableContent("CCC", c);

        view.addToSlot(slot, ccc);
        view.addToSlot(slot, bbb);
        view.addToSlot(slot, aaa);

        final InOrder inOrder = inOrder(container);
        inOrder.verify(container).insert(c, 0);
        inOrder.verify(container).insert(b, 0);
        inOrder.verify(container).insert(a, 0);
    }

    @Test
    public void testInsertRandomCheckOrder() {
        final List<String> names = new ArrayList<>();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Widget w = (Widget) invocationOnMock.getArguments()[0];
                final int idx = (int) invocationOnMock.getArguments()[1];
                names.add(idx, w.getTitle());
                return null;
            }
        }).when(container).insert(any(Widget.class), anyInt());

        for (int i = 0; i < 1000; ++i) {
            final String name = Double.toString(Math.random());
            final Widget w = mock(Widget.class);
            when(w.getTitle()).thenReturn(name);
            final ComparableContent cc = new ComparableContent(name, w);
            view.addToSlot(slot, cc);
        }

        assertTrue("Container should have 1000 members.", names.size() == 1000);
        final List<String> namesCopy = new ArrayList<>(names);
        Collections.sort(names);
        assertEquals("Container elements should be sorted.", namesCopy, names);
    }

    static class ComparableContent implements IsWidget, Comparable<ComparableContent> {

        private final String name;
        private final Widget widget;

        ComparableContent(final String name, Widget widget) {
            this.name = name;
            this.widget = widget;
        }

        @Override
        public int compareTo(ComparableContent o) {
            return name.compareTo(o.name);
        }

        @Override
        public Widget asWidget() {
            return widget;
        }
    }

    interface MockInsertPanel extends InsertPanel, HasWidgets {
    }

    static class TestViewImpl extends ViewImpl {
        @Override
        public void initWidget(IsWidget widget) {
            super.initWidget(widget);
        }

        @Override
        public <T extends HasWidgets & InsertPanel> void bindSlot(OrderedSlot<?> slot, T container) {
            super.bindSlot(slot, container);
        }
    }
}
