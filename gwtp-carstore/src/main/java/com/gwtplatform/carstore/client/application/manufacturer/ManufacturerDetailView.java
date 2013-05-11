/*
 * Copyright 2013 ArcBees Inc.
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

import javax.inject.Inject;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ManufacturerDetailView extends ViewWithUiHandlers<ManufacturerDetailUiHandlers>
        implements ManufacturerDetailPresenter.MyView, Editor<ManufacturerDto> {
    interface Binder extends UiBinder<Widget, ManufacturerDetailView> {
    }

    interface Driver extends SimpleBeanEditorDriver<ManufacturerDto, ManufacturerDetailView> {
    }

    @UiField
    TextBox name;

    private final Driver driver;

    @Inject
    ManufacturerDetailView(Binder uiBinder,
                           Driver driver) {
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));

        driver.initialize(this);

        name.getElement().setAttribute("placeholder", "Manufacturer name");
    }

    @Override
    public void edit(ManufacturerDto manufacturerDto) {
        name.setFocus(true);
        driver.edit(manufacturerDto);
    }

    @Override
    public void getManufacturer() {
        getUiHandlers().onSave(driver.flush());
    }
}

