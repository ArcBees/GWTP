package com.arcbees.carsample.client.application.cars.renderer;

import com.arcbees.carsample.shared.domain.Car;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.Inject;

public class CarCell extends AbstractCell<Car> {
    public interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name);
    }

    private final Renderer uiRenderer;

    @Inject
    public CarCell(final Renderer uiRenderer) {
        this.uiRenderer = uiRenderer;
    }

    @Override
    public void render(Context context, Car value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.getModel());
    }
}
