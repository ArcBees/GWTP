/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.client.application.manufacturer;

import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerUiHandlers;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ManufacturerModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(ManufacturerPresenter.class, ManufacturerPresenter.MyView.class, ManufacturerView.class,
                ManufacturerPresenter.MyProxy.class);
        
        bindPresenter(ManufacturerDetailPresenter.class, ManufacturerDetailPresenter.MyView.class,
                ManufacturerDetailView.class, ManufacturerDetailPresenter.MyProxy.class);

        bindSingletonPresenterWidget(EditManufacturerPresenter.class, EditManufacturerPresenter.MyView.class,
                EditManufacturerView.class);
        
        bind(ManufacturerDetailUiHandlers.class).to(ManufacturerDetailPresenter.class);
        bind(ManufacturerUiHandlers.class).to(ManufacturerPresenter.class);
        bind(EditManufacturerUiHandlers.class).to(EditManufacturerPresenter.class);
    }
}
