package com.gwtplatform.carstore.client.application.rating.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.carstore.client.application.rating.event.RatingAddedEvent.RatingAddedHandler;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public class RatingAddedEvent extends GwtEvent<RatingAddedHandler> {
    public interface RatingAddedHandler extends EventHandler {
        void onRatingAdded(RatingAddedEvent event);
    }

    public static Type<RatingAddedHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, RatingDto ratingDto) {
        source.fireEvent(new RatingAddedEvent(ratingDto));
    }

    private static final Type<RatingAddedHandler> TYPE = new Type<RatingAddedHandler>();

    private RatingDto ratingDto;

    public RatingAddedEvent(RatingDto ratingDto) {
        this.ratingDto = ratingDto;
    }

    @Override
    public Type<RatingAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public RatingDto getRating() {
        return ratingDto;
    }

    @Override
    protected void dispatch(RatingAddedHandler handler) {
        handler.onRatingAdded(this);
    }
}
