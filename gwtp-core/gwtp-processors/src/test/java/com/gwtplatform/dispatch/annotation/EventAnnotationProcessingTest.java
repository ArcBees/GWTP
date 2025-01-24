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

package com.gwtplatform.dispatch.annotation;

import org.junit.Test;

import com.google.gwt.event.shared.HasHandlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This test is being run by ant, but is not run in eclipse.
 * <p>
 * TODO: Make a test suite with a couple of permutations (with/without Order, Optional, both...).
 */
public class EventAnnotationProcessingTest {

    @Test
    public void shouldGenerateEvent() {
        Foo foo = new Foo("bar");
        FooChangedEvent event = new FooChangedEvent(foo, true);
        assertEquals("bar", event.getFoo().getName());
        assertTrue(event.isOriginator());

        FooChangedEvent event2 = new FooChangedEvent(foo, true);
        assertEquals(event, event2);
        assertEquals(event.hashCode(), event2.hashCode());

        FooChangedEvent event3 = new FooChangedEvent(foo, false);
        assertFalse(event3.equals(event));
    }

    @Test
    public void shouldGenerateEventWithBuilder() {
        Foo foo = new Foo("bar");
        FooChangedEvent event = new FooChangedEvent.Builder(foo, true).build();
        assertEquals("bar", event.getFoo().getName());
        assertTrue(event.isOriginator());

        FooChangedEvent event2 = new FooChangedEvent.Builder(foo, true).build();
        assertEquals(event, event2);
        assertEquals(event.hashCode(), event2.hashCode());

        FooChangedEvent event3 = new FooChangedEvent.Builder(foo, false).build();
        assertFalse(event3.equals(event));
    }

    @Test
    public void shouldGenerateEventWithOptionalFieldsAndBuilder() throws SecurityException, NoSuchMethodException {
        Foo foo = new Foo("bar");
        FooChangedEvent event = new FooChangedEvent.Builder(foo, true).additionalMessage("message").priority(1.0)
                .build();
        assertEquals("message", event.getAdditionalMessage());
        assertTrue(1.0 == event.getPriority());

        Class<?> eventClass = FooChangedEvent.class;
        eventClass.getMethod("fire", HasHandlers.class);
        eventClass.getMethod("fire", HasHandlers.class, FooChangedEvent.class);
    }
}
