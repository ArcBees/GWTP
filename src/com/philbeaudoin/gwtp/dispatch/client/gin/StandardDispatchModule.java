package com.philbeaudoin.gwtp.dispatch.client.gin;

import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.dispatch.client.DefaultExceptionHandler;
import com.philbeaudoin.gwtp.dispatch.client.DispatchAsync;
import com.philbeaudoin.gwtp.dispatch.client.ExceptionHandler;
import com.philbeaudoin.gwtp.dispatch.client.standard.StandardDispatchAsync;

/**
 * This module binds the {@link DispatchAsync} to {@link StandardDispatchAsync}.
 * For simple cases, just add this as a \@GinModule in your {@link Ginjector} instance.
 * <p/>
 * If you want to provide a custom {@link ExceptionHandler} just call
 * <code>install( new StandardDispatchModule( MyExceptionHandler.class )</code>
 * in another module.
 *
 * @author David Peterson
 */
public class StandardDispatchModule extends AbstractDispatchModule {

    /**
     * Constructs a new GIN configuration module that sets up a {@link com.philbeaudoin.gwtp.dispatch.client.DispatchAsync}
     * implementation, using the {@link com.philbeaudoin.gwtp.dispatch.client.DefaultExceptionHandler}.
     */
    public StandardDispatchModule() {
        this( DefaultExceptionHandler.class );
    }


    public StandardDispatchModule( Class<? extends ExceptionHandler> exceptionHandlerType ) {
        super( exceptionHandlerType );
    }

    @Provides
    @Singleton
    protected DispatchAsync provideDispatchAsync( ExceptionHandler exceptionHandler ) {
        return new StandardDispatchAsync( exceptionHandler );
    }

}
