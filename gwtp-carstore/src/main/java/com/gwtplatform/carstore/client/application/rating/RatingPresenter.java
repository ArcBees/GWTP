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

package com.gwtplatform.carstore.client.application.rating;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.rating.event.RatingAddedEvent;
import com.gwtplatform.carstore.client.application.rating.event.RatingAddedEvent.RatingAddedHandler;
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingPresenter;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.RatingService;
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder;

public class RatingPresenter extends Presenter<RatingPresenter.MyView, RatingPresenter.MyProxy>
        implements RatingUiHandlers, RatingAddedHandler, ActionBarEvent.ActionBarHandler {

    public interface MyView extends View, HasUiHandlers<RatingUiHandlers> {
        void displayRatings(List<RatingDto> results);

        void removeRating(RatingDto ratingDto);

        void addRating(RatingDto ratingDto);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.rating)
    public interface MyProxy extends ProxyPlace<RatingPresenter> {
    }

    private final RestDispatch dispatcher;
    private final EditRatingPresenter editRatingPresenter;
    private final RatingService ratingService;
    private final PlaceManager placeManager;

    @Inject
    RatingPresenter(EventBus eventBus,
                    MyView view,
                    MyProxy proxy,
                    RestDispatch dispatcher,
                    RatingService ratingService,
                    EditRatingPresenter editRatingPresenter,
                    PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.ratingService = ratingService;
        this.placeManager = placeManager;
        this.editRatingPresenter = editRatingPresenter;

        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getRating())) {
            placeManager.revealPlace(new Builder().nameToken(NameTokens.getDetailRating()).build());
        }
    }

    @Override
    public void onCreate() {
        editRatingPresenter.createNew();
    }

    @Override
    public void onDelete(final RatingDto ratingDto) {
        dispatcher.execute(ratingService.delete(ratingDto.getId()), new AbstractAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                getView().removeRating(ratingDto);
            }
        });
    }

    @ProxyEvent
    @Override
    public void onRatingAdded(RatingAddedEvent event) {
        getView().addRating(event.getRating());
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(ActionType.ADD), true);

        dispatcher.execute(ratingService.getRatings(), new AbstractAsyncCallback<List<RatingDto>>() {
            @Override
            public void onSuccess(List<RatingDto> ratings) {
                getView().displayRatings(ratings);
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.SLOT_MAIN_CONTENT, this);
    }
}
