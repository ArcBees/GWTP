/**
 * Copyright 2014 ArcBees Inc.
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

import org.turbogwt.mvp.databind.client.Strategy;
import org.turbogwt.ext.gwtp.databind.client.DatabindViewImpl;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

import javax.inject.Inject;

public class ManufacturerDetailView extends DatabindViewImpl<ManufacturerDetailUiHandlers>
        implements ManufacturerDetailPresenter.MyView, Editor<ManufacturerDto> {

    interface Binder extends UiBinder<Widget, ManufacturerDetailView> {
    }

    @UiField
    TextBox name;

    @Inject
    ManufacturerDetailView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        name.getElement().setAttribute("placeholder", "Manufacturer name");

        bind("name", name, Strategy.ON_CHANGE);
    }
}
