package com.gwtplatform.testing;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This class implements the mockito runner but allows Guice dependency injection.
 * To setup the guice environment, the test class can have an inner static class 
 * deriving from {@link AbstractModule} or, more commonly, from {@link TestModule}. 
 * This last class will let you bind {@link TestSingletonMockProvider} and the runner will make sure
 * these singletons are reset at every invocation of a test case.
 * <p />
 * This code not very clean as it is cut & paste from {@link org.mockito.internal.runners.JUnit45AndHigherRunnerImpl},
 * but it's unclear how we could make otherwise.
 * <p />
 * Most of the code here is inspired from:
 * <a href="http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.html">
 * http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.html</a>
 * <p />
 * 
 * @author Philippe Beaudoin
 */
public class GuiceMockitoJUnitRunner extends BlockJUnit4ClassRunner  {

  private final Injector injector;

  public GuiceMockitoJUnitRunner(Class<?> klass) throws InitializationError, InvocationTargetException, InstantiationException, IllegalAccessException {
    super(klass);
    Injector injector = null;
    for( Class<?> subclass : klass.getClasses() ) {
      if( AbstractModule.class.isAssignableFrom(subclass) ) {
        assert injector == null : "More than one AbstractModule inner class found within test class \"" + klass.getName() + "\".";
        injector = Guice.createInjector( (Module)subclass.newInstance() );
      }
    }
    if( injector == null )
      injector = Guice.createInjector(); 

    this.injector = injector;
  }

  @Override
  public void run(final RunNotifier notifier) {
    // add listener that validates framework usage at the end of each test
    notifier.addListener(new FrameworkUsageValidator(notifier));
    super.run(notifier);
  }
  
  @Override
  protected Statement withBefores(FrameworkMethod method,
      Object target,
      Statement statement) {
    TestScope.clear();
    return super.withBefores(method, target, statement);
  }
  @Override
  protected Object createTest() throws Exception {
    return injector.getInstance(getTestClass().getJavaClass()); 
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
