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
