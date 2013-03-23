package com.arcbees.carsample.client.gin;

import com.arcbees.carsample.client.application.ApplicationTabletModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class TabletModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationTabletModule());
    }
}
