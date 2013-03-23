package com.arcbees.carsample.client.application.rating;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.ApplicationPresenter;
import com.arcbees.carsample.client.application.event.ActionBarEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent.ActionType;
import com.arcbees.carsample.client.application.event.DisplayMessageEvent;
import com.arcbees.carsample.client.application.event.GoBackEvent;
import com.arcbees.carsample.client.application.rating.RatingDetailPresenter.MyProxy;
import com.arcbees.carsample.client.application.rating.RatingDetailPresenter.MyView;
import com.arcbees.carsample.client.application.rating.ui.EditRatingMessages;
import com.arcbees.carsample.client.application.widget.message.Message;
import com.arcbees.carsample.client.application.widget.message.MessageStyle;
import com.arcbees.carsample.client.place.NameTokens;
import com.arcbees.carsample.client.security.LoggedInGatekeeper;
import com.arcbees.carsample.client.util.ErrorHandlerAsyncCallback;
import com.arcbees.carsample.client.util.SafeAsyncCallback;
import com.arcbees.carsample.shared.dispatch.GetCarsAction;
import com.arcbees.carsample.shared.dispatch.GetResult;
import com.arcbees.carsample.shared.dispatch.GetResults;
import com.arcbees.carsample.shared.dispatch.SaveRatingAction;
import com.arcbees.carsample.shared.domain.Car;
import com.arcbees.carsample.shared.domain.Rating;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class RatingDetailPresenter extends Presenter<MyView, MyProxy> implements RatingDetailUiHandlers,
        ActionBarEvent.ActionBarHandler, GoBackEvent.GoBackHandler {
    public interface MyView extends View, HasUiHandlers<RatingDetailUiHandlers> {
        void edit(Rating rating);

        void setAllowedCars(List<Car> cars);

        void getRating();
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.detailRating)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<RatingDetailPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final EditRatingMessages messages;
    private final PlaceManager placeManager;

    @Inject
    public RatingDetailPresenter(EventBus eventBus, MyView view, MyProxy proxy, DispatchAsync dispatcher,
            EditRatingMessages messages, PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.messages = messages;
        this.placeManager = placeManager;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void onGoBack(GoBackEvent event) {
        placeManager.revealPlace(new PlaceRequest(NameTokens.getRating()));
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.DONE && event.isTheSameToken(NameTokens.getDetailRating())) {
            getView().getRating();
        }
    }

    @Override
    public void onSave(Rating rating) {
        dispatcher.execute(new SaveRatingAction(rating), new ErrorHandlerAsyncCallback<GetResult<Rating>>(this) {
            @Override
            public void onSuccess(GetResult<Rating> result) {
                DisplayMessageEvent.fire(RatingDetailPresenter.this, new Message(messages.ratingSaved(),
                        MessageStyle.SUCCESS));
                placeManager.revealPlace(new PlaceRequest(NameTokens.getRating()));

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
        List<ActionType> actions = Arrays.asList(new ActionType[] { ActionType.DONE });
        ChangeActionBarEvent.fire(this, actions, false);

        dispatcher.execute(new GetCarsAction(), new SafeAsyncCallback<GetResults<Car>>() {
            @Override
            public void onSuccess(GetResults<Car> result) {
                onGetCarsSuccess(result.getResults());
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    private void onGetCarsSuccess(List<Car> cars) {
        getView().setAllowedCars(cars);
        getView().edit(new Rating());
    }
}
