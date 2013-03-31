package com.gwtplatform.carstore.client.application.manufacturer;

import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerUiHandlers;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ManufacturerMobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(ManufacturerPresenter.class, ManufacturerPresenter.MyView.class, ManufacturerMobileView.class,
                ManufacturerPresenter.MyProxy.class);

        bindPresenter(ManufacturerDetailPresenter.class, ManufacturerDetailPresenter.MyView.class,
                ManufacturerDetailView.class, ManufacturerDetailPresenter.MyProxy.class);
        
        bindSingletonPresenterWidget(EditManufacturerPresenter.class, EditManufacturerPresenter.MyView.class,
                EditManufacturerView.class);
       
        bind(ManufacturerUiHandlers.class).to(ManufacturerPresenter.class);
        bind(ManufacturerDetailUiHandlers.class).to(ManufacturerDetailPresenter.class);
        bind(EditManufacturerUiHandlers.class).to(EditManufacturerPresenter.class);
    }
}
