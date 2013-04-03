package com.gwtplatform.carstore.client.application.manufacturer.renderer;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.Inject;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class ManufacturerCell extends AbstractCell<ManufacturerDto> {
    public interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name);
    }

    private final Renderer uiRenderer;

    @Inject
    public ManufacturerCell(final Renderer uiRenderer) {
        this.uiRenderer = uiRenderer;
    }

    @Override
    public void render(Context context, ManufacturerDto value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.getName());
    }
}
