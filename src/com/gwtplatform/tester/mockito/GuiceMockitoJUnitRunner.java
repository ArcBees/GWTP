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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scope;
import com.google.inject.spi.DefaultBindingScopingVisitor;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the mockito runner but allows Guice dependency
 * injection. To setup the guice environment, the test class can have an inner
 * static class deriving from {@link TestModule}. This last class will let you bind
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
    this(klass, true);
  }

  public GuiceMockitoJUnitRunner(Class<?> klass, boolean useAutomockingIfNoEnvironmentFound) throws InitializationError,
      InvocationTargetException, InstantiationException, IllegalAccessException {
    super(klass);

    TestModule testModule = null;
    for (Class<?> subclass : klass.getClasses()) {
      if (TestModule.class.isAssignableFrom(subclass)) {
        assert testModule == null : "More than one TestModule inner class found within test class \""
            + klass.getName() + "\".";
        testModule = (TestModule) subclass.newInstance();
      }
    }
    if (testModule == null) {
      if (useAutomockingIfNoEnvironmentFound) {
        testModule = new AutomockingModule() {          
          @Override protected void configureTest() { } };
      } else {
        testModule = new TestModule() {          
          @Override protected void configureTest() { } };        
      }
    }
    testModule.setTestClass(klass);
    injector = Guice.createInjector(testModule);
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

  @Override
  protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
    return new InjectedStatement(method, test, injector);    
  }

  @Override
  protected Statement withBefores(FrameworkMethod method, Object target,
      Statement statement) {
    Statement newStatement = super.withBefores(method, target, statement); 
    
    List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
        InjectBefore.class);
    return befores.isEmpty() ? newStatement : new InjectedBeforeStatements(newStatement,
        befores, target, injector);
  }

  @Override
  protected Statement withAfters(FrameworkMethod method, Object target,
      Statement statement) {
    Statement newStatement = super.withAfters(method, target, statement); 
    
    List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
        InjectAfter.class);
    return afters.isEmpty() ? newStatement : new InjectedBeforeStatements(newStatement,
        afters, target, injector);
  }
  
  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    // Use a set otherwise methods are listed multiple times
    Set<FrameworkMethod> guiceTestMethods = new HashSet<FrameworkMethod>();
    guiceTestMethods.addAll(super.computeTestMethods());
    guiceTestMethods.addAll(getTestClass().getAnnotatedMethods(InjectTest.class));
    List<FrameworkMethod> result = new ArrayList<FrameworkMethod>(guiceTestMethods.size());
    result.addAll(guiceTestMethods);    
    return result;
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
   * Adds to {@code errors} for each method annotated with {@code @Test}that
   * is not a public, void instance method with no arguments.
   */
  protected void validateTestMethods(List<Throwable> errors) {
    super.validateTestMethods(errors);
    validatePublicVoidMethods(InjectTest.class, false, errors);
  }

  /**
   * Adds to {@code errors} if any method in this class is annotated with
   * {@code annotation}, but:
   * <ul>
   * <li>is not public, or
   * <li>takes parameters, or
   * <li>returns something other than void, or
   * <li>is static (given {@code isStatic is false}), or
   * <li>is not static (given {@code isStatic is true}).
   */
  protected void validatePublicVoidMethods(Class<? extends Annotation> annotation,
      boolean isStatic, List<Throwable> errors) {
    List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);

    for (FrameworkMethod eachTestMethod : methods) {
      eachTestMethod.validatePublicVoid(isStatic, errors);
      // TODO We should validate that all parameters have a binding
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
