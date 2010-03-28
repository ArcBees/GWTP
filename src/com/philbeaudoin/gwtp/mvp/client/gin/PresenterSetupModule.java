package com.philbeaudoin.gwtp.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.philbeaudoin.gwtp.mvp.client.proxy.ParameterTokenFormatter;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceManager;
import com.philbeaudoin.gwtp.mvp.client.proxy.TokenFormatter;

/**
 * Configures the basic classes for presenter.
 */
public class PresenterSetupModule extends AbstractGinModule {

  private final Class<? extends PlaceManager> placeManagerClass;

  private final Class<? extends TokenFormatter> tokenFormatterClass;

  public PresenterSetupModule( Class<? extends PlaceManager> placeManagerClass ) {
    this( placeManagerClass, ParameterTokenFormatter.class );
  }

  public PresenterSetupModule( Class<? extends PlaceManager> placeManagerClass, Class<? extends TokenFormatter> tokenFormatterClass ) {
    this.placeManagerClass = placeManagerClass;
    this.tokenFormatterClass = tokenFormatterClass;
  }

  @Override
  protected void configure() {
    bind( TokenFormatter.class).to( tokenFormatterClass );

    bind( PlaceManager.class ).to( placeManagerClass );
    bind( placeManagerClass ).asEagerSingleton();
  }

  @Override
  public boolean equals( Object object ) {
    return object instanceof PresenterSetupModule;
  }

  public int hashCode() {
    return 19;
  }
}
