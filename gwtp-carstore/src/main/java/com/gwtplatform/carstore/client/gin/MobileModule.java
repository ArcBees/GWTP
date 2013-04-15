package com.gwtplatform.carstore.client.gin;

import com.gwtplatform.carstore.client.application.ApplicationMobileModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationMobileModule());
    }
}
