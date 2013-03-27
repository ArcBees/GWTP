package com.arcbees.carsample.client.gin;

import com.arcbees.carsample.client.application.ApplicationDesktopModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class DesktopModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationDesktopModule());
    }
}
