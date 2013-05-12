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

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.manufacturer.renderer.ManufacturerCell;
import com.gwtplatform.carstore.client.resources.MobileDataListStyle;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ManufacturerMobileView extends ViewWithUiHandlers<ManufacturerUiHandlers> implements MyView {
    interface Binder extends UiBinder<Widget, ManufacturerMobileView> {
    }

    @UiField(provided = true)
    CellList<ManufacturerDto> manufacturerList;

    private final ListDataProvider<ManufacturerDto> manufacturerDataProvider;
    private final SingleSelectionModel<ManufacturerDto> selectionModel;

    @Inject
    ManufacturerMobileView(Binder uiBinder,
                           ManufacturerCell manufacturerCell,
                           MobileDataListStyle mobileDataListStyle) {
        manufacturerList = new CellList<ManufacturerDto>(manufacturerCell, mobileDataListStyle);

        initWidget(uiBinder.createAndBindUi(this));

        manufacturerDataProvider = new ListDataProvider<ManufacturerDto>();
        manufacturerDataProvider.addDataDisplay(manufacturerList);
        selectionModel = new SingleSelectionModel<ManufacturerDto>();
        manufacturerList.setSelectionModel(selectionModel);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                getUiHandlers().onDetail(selectionModel.getSelectedObject());
            }
        });
    }

    @Override
    public void displayManufacturers(List<ManufacturerDto> manufacturerDtos) {
        manufacturerDataProvider.getList().clear();
        manufacturerDataProvider.getList().addAll(manufacturerDtos);
    }

    @Override
    public void addManufacturer(ManufacturerDto manufacturerDto) {
        manufacturerDataProvider.getList().add(manufacturerDto);
    }

    @Override
    public void removeManufacturer(ManufacturerDto manufacturerDto) {
        manufacturerDataProvider.getList().remove(manufacturerDto);
    }

    @Override
    public void replaceManufacturer(ManufacturerDto oldManufacturer, ManufacturerDto newManufacturer) {
        List<ManufacturerDto> manufacturerDtos = manufacturerDataProvider.getList();
        int index = manufacturerDtos.indexOf(oldManufacturer);

        manufacturerDtos.add(index, newManufacturer);
        manufacturerDtos.remove(index + 1);
    }
}
