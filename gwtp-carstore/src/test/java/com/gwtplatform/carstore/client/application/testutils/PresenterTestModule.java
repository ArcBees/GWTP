package com.gwtplatform.carstore.client.application.testutils;

import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.AutobindDisable;

import org.jukito.JukitoModule;
import org.jukito.TestSingleton;

/**
 * Base module to use while testing presenters. {@link AutomockingModule} is
 * used. Your configuration module must extends this class.
 * 
 * @author Christian Goudreau
 */
public abstract class PresenterTestModule extends JukitoModule {
    @Override
    protected void configureTest() {
        bindNamedMock(DispatchAsync.class, "mock").in(TestSingleton.class);
        bind(DispatchAsync.class).to(RelayingDispatcher.class);
        bind(RelayingDispatcher.class).in(TestSingleton.class);

        configurePresenterTest();

        bind(AutobindDisable.class).toInstance(new AutobindDisable(true));
    }

    protected abstract void configurePresenterTest();
}
