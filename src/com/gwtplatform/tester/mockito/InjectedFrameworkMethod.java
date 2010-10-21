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

import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This class is an extension of {@link FrameworkMethod} that makes it possible to specify
 * which {@link Binding} to use for parameters marked with {@literal @}{@link All}. 
 * 
 * @author Philippe Beaudoin
 */
public class InjectedFrameworkMethod extends FrameworkMethod {

  private final List<Binding<?>> bindingsToUseForParameters;
  
  public InjectedFrameworkMethod(Method method) {
    super(method);
    bindingsToUseForParameters = null;
  }

  public InjectedFrameworkMethod(Method method, List<Binding<?>> bindingsToUseForParameters) {
    super(method);
    this.bindingsToUseForParameters = bindingsToUseForParameters;
  }

  public List<Binding<?>> getBindingsToUseForParameters() {
    return bindingsToUseForParameters;
  }
}
