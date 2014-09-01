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
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.rating.RatingDetailPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.rating.RatingDetailPresenter.MyView;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.resources.EditRatingMessages;
import com.gwtplatform.carstore.client.rest.CarsService;
import com.gwtplatform.carstore.client.rest.RatingService;
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder;

public class RatingDetailPresenter extends Presenter<MyView, MyProxy>
        implements RatingDetailUiHandlers, ActionBarEvent.ActionBarHandler, GoBackEvent.GoBackHandler {

    interface MyView extends View, HasUiHandlers<RatingDetailUiHandlers> {
        void edit(RatingDto ratingDto);

        void setAllowedCars(List<CarDto> carDtos);

        void getRating();
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.DETAIL_RATING)
    interface MyProxy extends ProxyPlace<RatingDetailPresenter> {
    }

    private final RestDispatch dispatcher;
    private final CarsService carsService;
    private final RatingService ratingService;
    private final EditRatingMessages messages;
    private final PlaceManager placeManager;

    @Inject
    RatingDetailPresenter(EventBus eventBus,
                          MyView view,
                          MyProxy proxy,
                          RestDispatch dispatcher,
                          CarsService carsService,
                          RatingService ratingService,
                          EditRatingMessages messages,
                          PlaceManager placeManager) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN_CONTENT);

        this.dispatcher = dispatcher;
        this.carsService = carsService;
        this.ratingService = ratingService;
        this.messages = messages;
        this.placeManager = placeManager;

        getView().setUiHandlers(this);
    }

    @Override
    public void onGoBack(GoBackEvent event) {
        placeManager.revealPlace(new Builder().nameToken(NameTokens.getRating()).build());
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.DONE && event.isTheSameToken(NameTokens.getDetailRating())) {
            getView().getRating();
        }
    }

    @Override
    public void onSave(RatingDto ratingDto) {
        dispatcher.execute(ratingService.saveOrCreate(ratingDto), new ErrorHandlerAsyncCallback<RatingDto>(this) {
            @Override
            public void onSuccess(RatingDto savedRating) {
                DisplayMessageEvent.fire(RatingDetailPresenter.this, new Message(messages.ratingSaved(),
                        MessageStyle.SUCCESS));
                placeManager.revealPlace(new Builder().nameToken(NameTokens.getRating()).build());
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(GoBackEvent.getType(), this);
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void onReveal() {
        List<ActionType> actions = Arrays.asList(ActionType.DONE);
        ChangeActionBarEvent.fire(this, actions, false);

        dispatcher.execute(carsService.getCars(), new AbstractAsyncCallback<List<CarDto>>() {
            @Override
            public void onSuccess(List<CarDto> cars) {
                onGetCarsSuccess(cars);
            }
        });
    }

    private void onGetCarsSuccess(List<CarDto> carDtos) {
        getView().setAllowedCars(carDtos);
        getView().edit(new RatingDto());
    }
}
