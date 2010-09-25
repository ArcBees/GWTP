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

package com.gwtplatform.test.mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scope;
import com.google.inject.spi.DefaultBindingScopingVisitor;


import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the mockito runner but allows Guice dependency
 * injection. To setup the guice environment, the test class can have an inner
 * static class deriving from {@link AbstractModule} or, more commonly, from
 * {@link TestModule}. This last class will let you bind
 * {@link TestSingletonMockProvider} and the runner will make sure these
 * singletons are reset at every invocation of a test case.
 * <p />
 * This code not very clean as it is cut & paste from
 * {@link org.mockito.internal.runners.JUnit45AndHigherRunnerImpl}, but it's
 * unclear how we could make otherwise.
 * <p />
 * Most of the code here is inspired from: <a href=
 * "http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.html"
 * > http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.
 * html</a>
 * <p />
 * Depends on Mockito.
 * 
 * @author Philippe Beaudoin
 */
public class GuiceMockitoJUnitRunner extends BlockJUnit4ClassRunner {

  private final Injector injector;
  
  public GuiceMockitoJUnitRunner(Class<?> klass) throws InitializationError,
      InvocationTargetException, InstantiationException, IllegalAccessException {
    super(klass);
    Injector inj = null;
    for (Class<?> subclass : klass.getClasses()) {
      if (AbstractModule.class.isAssignableFrom(subclass)) {
        assert inj == null : "More than one AbstractModule inner class found within test class \""
            + klass.getName() + "\".";
        inj = Guice.createInjector((Module) subclass.newInstance());
      }
    }
    if (inj == null) {
      inj = Guice.createInjector();
    }
    injector = inj;
  }
  
  @Override
  public void run(final RunNotifier notifier) {
    // add listener that validates framework usage at the end of each test
    notifier.addListener(new FrameworkUsageValidator(notifier));
    super.run(notifier);
  }

  @Override
  protected Object createTest() throws Exception {
    TestScope.clear();
    instantiateEagerTestSingletons();
    return injector.getInstance(getTestClass().getJavaClass());
  }

  private void instantiateEagerTestSingletons() {
    DefaultBindingScopingVisitor<Boolean> isEagerTestScopeSingleton = new DefaultBindingScopingVisitor<Boolean>() {
      public Boolean visitScope(Scope scope) {
        return scope == TestScope.EAGER_SINGLETON;          
      }
    };
    for (Binding<?> binding : injector.getBindings().values()) {
      boolean instantiate = false;
      if (binding != null) {
        Boolean result = binding.acceptScopingVisitor(isEagerTestScopeSingleton);
        if (result != null && result) {
          instantiate = true;
        }
      }
      if (instantiate) {
        binding.getProvider().get();
      }
    }
  }

  /**
   * Access the Guice injector.
   * 
   * @return The Guice {@link Injector}.
   */
  protected Injector getInjector() {
    return injector;
  }

}
