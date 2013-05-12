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

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerUiHandlers extends UiHandlers {
    void onDetail(ManufacturerDto manufacturerDto);

    void onEdit(ManufacturerDto manufacturerDto);

    void onDelete(ManufacturerDto manufacturerDto);

    void onCreate();
}
