/**
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

package com.gwtplatform.carstore.client.application.manufacturer.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent.ManufacturerAddedHandler;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class ManufacturerAddedEvent extends GwtEvent<ManufacturerAddedHandler> {
    public interface ManufacturerAddedHandler extends EventHandler {
        void onManufacturerAdded(ManufacturerAddedEvent event);
    }

    public static Type<ManufacturerAddedHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, ManufacturerDto manufacturerDto) {
        source.fireEvent(new ManufacturerAddedEvent(manufacturerDto));
    }

    private static final Type<ManufacturerAddedHandler> TYPE = new Type<ManufacturerAddedHandler>();

    private ManufacturerDto manufacturerDto;

    public ManufacturerAddedEvent(ManufacturerDto manufacturerDto) {
        this.manufacturerDto = manufacturerDto;
    }

    @Override
    public Type<ManufacturerAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturerDto;
    }

    @Override
    protected void dispatch(ManufacturerAddedHandler handler) {
        handler.onManufacturerAdded(this);
    }
}
