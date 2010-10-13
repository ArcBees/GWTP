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

package com.gwtplatform.tester.mockito;

import com.google.inject.Injector;
import com.google.inject.Key;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A {@link Statement} invoking a method with parameters by filling-in these
 * parameters with injected instances.
 * 
 * @author Philippe Beaudoin
 */
class InjectedStatement extends Statement {

  private final FrameworkMethod method;
  private final Object test;
  private final Injector injector;

  public InjectedStatement(FrameworkMethod method, Object test, Injector injector) {
    this.method = method;
    this.test = test;
    this.injector = injector;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void evaluate() throws Throwable {
    // TODO Handle Provider<>
    Method javaMethod = method.getMethod();
    Class<?> parameters[] = javaMethod.getParameterTypes();
    Annotation annotations[][] = javaMethod.getParameterAnnotations();
    Object injectedParameters[] = new Object[parameters.length];
    for (int i = 0; i < parameters.length; ++i) {
      Key<Object> key = null;
      if (annotations[i].length == 0) {
        key = (Key<Object>) Key.get(parameters[i]);
      } else {
        key = (Key<Object>) Key.get(parameters[i], annotations[i][0]);
      }
      injectedParameters[i] = injector.getInstance(key);  
    }
    method.invokeExplosively(test, injectedParameters);
  }    
}