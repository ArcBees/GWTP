package com.gwtplatform.carstore.client.gin;

import com.gwtplatform.carstore.client.application.ApplicationTabletModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class TabletModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new ApplicationTabletModule());
    }
}
