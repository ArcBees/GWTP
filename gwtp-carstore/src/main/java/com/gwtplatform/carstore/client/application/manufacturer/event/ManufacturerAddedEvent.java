package com.gwtplatform.carstore.client.application.manufacturer.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent.ManufacturerAddedHandler;
import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class ManufacturerAddedEvent extends GwtEvent<ManufacturerAddedHandler> {
    public interface ManufacturerAddedHandler extends EventHandler {
        void onManufacturerAdded(ManufacturerAddedEvent event);
    }

    public static Type<ManufacturerAddedHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, Manufacturer manufacturer) {
        source.fireEvent(new ManufacturerAddedEvent(manufacturer));
    }

    private static final Type<ManufacturerAddedHandler> TYPE = new Type<ManufacturerAddedHandler>();

    private Manufacturer manufacturer;

    public ManufacturerAddedEvent(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public Type<ManufacturerAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    @Override
    protected void dispatch(ManufacturerAddedHandler handler) {
        handler.onManufacturerAdded(this);
    }
}
