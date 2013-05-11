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

package com.gwtplatform.carstore.client.application.report.renderer;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.Inject;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;

public class ReportCell extends AbstractCell<ManufacturerRatingDto> {
    interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name, String average);
    }

    private final Renderer uiRenderer;
    private final NumberFormat numberFormat;

    @Inject
    ReportCell(Renderer uiRenderer) {
        this.uiRenderer = uiRenderer;
        this.numberFormat = NumberFormat.getFormat("#,##0.0#");
    }

    @Override
    public void render(Context context, ManufacturerRatingDto value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.getManufacturer(), numberFormat.format(value.getRating()));
    }
}
