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

package com.gwtplatform.dispatch.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gwt.event.shared.HasHandlers;

/**
 * This test is being run by ant, but is not run in eclipse.
 * 
 * TODO: Make a test suite with a couple of permutations. (With/without Order, Optional, both....)
 * 
 * @author Brendan Doherty
 * @author Florian Sauter
 */
public class AnnotationProcessingTest {

  @org.junit.Test
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
  
  @org.junit.Test
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
  
  @org.junit.Test
  public void shouldGenerateEventWithOptionalFieldsAndBuilder() throws SecurityException, NoSuchMethodException {
    Foo foo = new Foo("bar");
    FooChangedEvent event = new FooChangedEvent.Builder(foo, true).additionalMessage("message").priority(1.0).build();
    assertEquals("message", event.getAdditionalMessage());
    assertTrue(1.0 == event.getPriority());
    
    Class<?> eventClass = FooChangedEvent.class;
    eventClass.getMethod("fire", HasHandlers.class);
    eventClass.getMethod("fire", HasHandlers.class, FooChangedEvent.class);
  }

  @org.junit.Test
  public void shouldGenerateDispatch() {
    RetrieveFooAction action = new RetrieveFooAction(16);
    assertEquals(16, action.getFooId());
    assertTrue(action.isSecured());
    assertEquals("dispatch/RetrieveFoo",action.getServiceName());

    RetrieveFooAction action2 = new RetrieveFooAction(16);
    assertEquals(action, action2);
    assertEquals(action.hashCode(), action2.hashCode());

    RetrieveFooAction action3 = new RetrieveFooAction(17);
    assertFalse(action.equals(action3));
    assertFalse(action.hashCode() == action3.hashCode());

    Foo foo = new Foo("bar");
    RetrieveFooResult result = new RetrieveFooResult(foo, 42);
    assertEquals("bar", result.getFoo().getName());
    assertEquals(42, result.getMeaningOfLife());

    RetrieveFooResult result2 = new RetrieveFooResult(foo, 42);
    assertEquals(result, result2);
    assertEquals(result.hashCode(), result2.hashCode());

    RetrieveFooResult result3 = new RetrieveFooResult(foo, 43);
    assertFalse(result.equals(result3));
    assertFalse(result.hashCode() == result3.hashCode());
    
    RetrieveBarAction action4 = new RetrieveBarAction("blah");
    assertFalse(action4.isSecured());
    assertEquals("dispatch/Blah", action4.getServiceName());
    
    RetrieveBarResult result4 = new RetrieveBarResult(foo, 42);
    assertEquals(foo, result4.getThing());
  }
  
  @org.junit.Test
  public void shouldGenerateDispatchWithOptionalFields() {
    RetrieveFooAction action = new RetrieveFooAction.Builder(42).additionalQuestion("meaning of life").build();
    assertEquals(42, action.getFooId());
    assertTrue(action.isSecured());
    assertEquals("dispatch/RetrieveFoo",action.getServiceName());
    
    Foo foo = new Foo("bar");
    RetrieveFooResult result = new RetrieveFooResult.Builder(foo, 42).answer42(true).build();
    assertEquals(true, result.isAnswer42());
    assertEquals(42, result.getMeaningOfLife());
  }

  @org.junit.Test
  public void shouldGenerateDto() {
    PersonNameDto dto = new PersonNameDto("bob", "smith");
    assertEquals("bob", dto.getFirstName());
    assertEquals("smith", dto.getLastName());

    PersonNameDto dto2 = new PersonNameDto("bob", "smith");
    assertEquals(dto, dto2);

    PersonNameDto dto3 = new PersonNameDto("bobby", "smith");
    assertFalse(dto.equals(dto3));
  }
  
  @org.junit.Test
  public void shouldGenerateDtoWithOptionalFieldsAndBuilder() {
    PersonNameDto dto = new PersonNameDto.Builder("bob", "andrews").secondName("peter").build();
    assertEquals("bob", dto.getFirstName());
    assertEquals("andrews", dto.getLastName());
    assertEquals("peter", dto.getSecondName());
  }

}
