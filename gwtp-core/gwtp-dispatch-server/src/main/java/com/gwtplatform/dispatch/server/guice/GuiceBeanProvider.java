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

package com.gwtplatform.dispatch.server.guice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper.BeanProvider;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper.CommonBindingDescriptor;

/**
 * @author Peter Simun (simun@seges.sk)
 */
public class GuiceBeanProvider implements BeanProvider {

  /**
   * Adapter for tranforming Guice Binding into BeanProvider implementation.
   * 
   * @author Peter Simun (simun@seges.sk)
   */
  public static class GuiceBindingDescriptorAdapter<B> extends CommonBindingDescriptor<B> {

    public GuiceBindingDescriptorAdapter(Binding<B> binding) {
      super(binding.getProvider().get(), binding.getKey().toString());
    }
  }

  private Injector injector;

  public GuiceBeanProvider(Injector injector) {
    this.injector = injector;
  }

  @Override
  public <B> B getInstance(Class<B> clazz) {
    return injector.getInstance(clazz);
  }

  @Override
  public <B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz) {

    List<BindingDescriptor<B>> result = new ArrayList<BindingDescriptor<B>>();

    for (Binding<B> binding : injector.findBindingsByType(TypeLiteral.get(clazz))) {
      result.add(new GuiceBindingDescriptorAdapter<B>(binding));
    }

    return result.iterator();
  }
}