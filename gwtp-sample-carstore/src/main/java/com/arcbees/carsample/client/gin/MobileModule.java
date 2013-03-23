package com.arcbees.carsample.client.gin;

import com.arcbees.carsample.client.application.ApplicationMobileModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationMobileModule());
    }
}
