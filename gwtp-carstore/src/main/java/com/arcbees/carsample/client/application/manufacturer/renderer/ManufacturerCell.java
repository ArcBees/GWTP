package com.arcbees.carsample.client.application.manufacturer.renderer;

import com.arcbees.carsample.shared.domain.Manufacturer;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.Inject;

public class ManufacturerCell extends AbstractCell<Manufacturer> {
    public interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name);
    }

    private final Renderer uiRenderer;

    @Inject
    public ManufacturerCell(final Renderer uiRenderer) {
        this.uiRenderer = uiRenderer;
    }

    @Override
    public void render(Context context, Manufacturer value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.getName());
    }
}
