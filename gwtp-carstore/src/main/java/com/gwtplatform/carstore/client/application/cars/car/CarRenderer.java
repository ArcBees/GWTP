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

package com.gwtplatform.carstore.client.application.cars.car;

import com.google.gwt.text.shared.AbstractRenderer;
import com.gwtplatform.carstore.client.application.manufacturer.ui.ManufacturerRenderer;
import com.gwtplatform.carstore.shared.dto.CarDto;

public class CarRenderer extends AbstractRenderer<CarDto> {
    @Override
    public String render(CarDto carDto) {
        if (carDto == null) {
            return "";
        }

        ManufacturerRenderer manufacturerRenderer = new ManufacturerRenderer();
        String manufacturer = manufacturerRenderer.render(carDto.getManufacturer());

        return manufacturer + " " + carDto.getModel();
    }
}
