package com.philbeaudoin.gwtp.testing;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This class implements the mockito runner but allows Guice dependency injection.
 * It's not very clean as it is cut & paste from {@link org.mockito.internal.runners.JUnit45AndHigherRunnerImpl}.
 * Taken from:
 * <a href="http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.html">
 * http://cowwoc.blogspot.com/2008/10/integrating-google-guice-into-junit4.html</a>
 * <p />
 * That the test class can have an inner static class deriving from
 * {@link AbstractModule} and configuring its bindings.
 * 
 * @author Philippe Beaudoin
 */
public class InjectingMockitoJUnitRunner extends BlockJUnit4ClassRunner  {

  private final Injector injector;

  public InjectingMockitoJUnitRunner(Class<?> klass) throws InitializationError, InvocationTargetException, InstantiationException, IllegalAccessException {
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
