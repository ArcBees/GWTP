package com.gwtplatform.carstore.client.application.rating.renderer;

import javax.inject.Inject;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public class RatingCell extends AbstractCell<RatingDto> {
    interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name, String rating);

        void onBrowserEvent(RatingCell o, NativeEvent e, Element p, RatingDto n);
    }

    private final Renderer uiRenderer;
    private final Delegate<RatingDto> delegate;

    @Inject
    RatingCell(Renderer uiRenderer,
               @Assisted Delegate<RatingDto> delegate) {
        super(BrowserEvents.CLICK);

        this.uiRenderer = uiRenderer;
        this.delegate = delegate;
    }

    @Override
    public void render(Context context, RatingDto value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.toString(), value.getRating().toString());
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, RatingDto value,
                               NativeEvent event, ValueUpdater<RatingDto> valueUpdater) {
        uiRenderer.onBrowserEvent(this, event, parent, value);
    }

    @UiHandler({"remove"})
    void onRemoveRatingClicked(ClickEvent event, Element parent, RatingDto value) {
        delegate.execute(value);
    }
}
