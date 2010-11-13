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

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * MockingBinder makes testing view even easier by mocking every {@link UiField}
 * and returning a mocked object upon creation.
 * <p />
 * To use it, you should build a small class that extends MockingBinder and bind
 * that class inside your Guice test module. You will have to provide a
 * {@link MockFactory} to let MockingBinder mock everything.
 * 
 * Ex:
 * 
 * <pre>public static class Module extends JukitoModule {
 *   static class MyTestBinder extends MockingBinder<Widget, BlogView> implements Binder {
 *     public MyTestBinder(final AnyMockFactory anyMockFactory) {
 *       super(Widget.class, anyMockFactory);
 *     }
 *   }
 *
 *   protected void configureTest() {
 *     GWTMockUtilities.disarm();
 *
 *     bind(Binder.class).to(MyTestBinder.class);
 *   }
 * }</pre>
 * 
 * Disarming GWT is important to unit test views.
 * 
 * @param <U> Mock type returned by {@link UiBinder#createAndBindUi()}.
 * @param <O> Owner type.
 * 
 * @author Christian Goudreau
 */
public abstract class MockingBinder<U, O> implements UiBinder<U, O> {
  private final Class<U> returnTypeClass;
  private final MockFactory mockFactory;

  /**
   * @param returnTypeClass Type to return when creating the mocked ui.
   * @param mockFactory A {@link MockFactory} to provide mock object.
   */
  public MockingBinder(final Class<U> returnTypeClass,
      final MockFactory mockFactory) {
    this.returnTypeClass = returnTypeClass;
    this.mockFactory = mockFactory;
  }

  @Override
  public U createAndBindUi(O owner) throws IllegalArgumentException {
    Field[] fields = owner.getClass().getDeclaredFields();

    for (Field field : fields) {
      Annotation[] annotations = field.getAnnotations();
      field.setAccessible(true);

      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(UiField.class)) {
            Object mockObject = mockFactory.mock(field.getType());

            try {
              field.set(owner, mockObject);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
        }
      }
    }

    return mockFactory.mock(returnTypeClass);
  }
}
