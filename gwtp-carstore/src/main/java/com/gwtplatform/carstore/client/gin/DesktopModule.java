package com.gwtplatform.carstore.client.gin;

import com.gwtplatform.carstore.client.application.ApplicationDesktopModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class DesktopModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationDesktopModule());
    }
}
