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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.Errors;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  
  @Override
  public void evaluate() throws Throwable {
    Method javaMethod = method.getMethod();
    
    Errors errors = new Errors(javaMethod);
    List<Key<?>> keys = GuiceUtils.getMethodKeys(javaMethod, errors);
    errors.throwConfigurationExceptionIfErrorsExist();
    
    Iterator<Binding<?>> bindingIter;
    if (InjectedFrameworkMethod.class.isAssignableFrom(method.getClass())) {
      bindingIter = ((InjectedFrameworkMethod) method).getBindingsToUseForParameters().iterator();
    } else {
      bindingIter = new ArrayList<Binding<?>>().iterator();
    }
    
    List<Object> injectedParameters = new ArrayList<Object>();
    for (Key<?> key : keys) {
      if (!All.class.equals(key.getAnnotationType())) {
        injectedParameters.add(injector.getInstance(key));
      } else {
        if (!bindingIter.hasNext()) {
          throw new AssertionError("Expected more bindings to fill @All parameters.");
        }
        injectedParameters.add(injector.getInstance(bindingIter.next().getKey()));
      }
    }
    
    method.invokeExplosively(test, injectedParameters.toArray());
  }
}