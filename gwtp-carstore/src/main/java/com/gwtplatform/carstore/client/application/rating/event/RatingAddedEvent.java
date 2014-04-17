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

    private static final Type<RatingAddedHandler> TYPE = new Type<>();

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
