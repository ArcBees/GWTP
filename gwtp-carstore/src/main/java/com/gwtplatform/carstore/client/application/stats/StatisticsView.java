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

package com.gwtplatform.carstore.client.application.stats;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.gwtplatform.carstore.client.application.stats.StatisticsPresenter.MyView;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class StatisticsView extends ViewWithUiHandlers<StatisticsUiHandlers> implements MyView {
    interface Binder extends UiBinder<Widget, StatisticsView> {
    }

    @UiField
    DatePicker datePicker;
    @UiField
    InlineLabel result;

    @Inject
    StatisticsView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setResult(String result) {
        this.result.setText(result);
    }

    @UiHandler("extractYear")
    void onExtractYear(ClickEvent event) {
        getUiHandlers().extractYear(datePicker.getValue());
    }
}
