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
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingMessages;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.GetCarsAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dispatch.SaveRatingAction;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest.Builder;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class RatingDetailPresenter extends Presenter<MyView, MyProxy>
        implements RatingDetailUiHandlers, ActionBarEvent.ActionBarHandler, GoBackEvent.GoBackHandler {

    public interface MyView extends View, HasUiHandlers<RatingDetailUiHandlers> {
        void edit(RatingDto ratingDto);

        void setAllowedCars(List<CarDto> carDtos);

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
    RatingDetailPresenter(EventBus eventBus,
                          MyView view,
                          MyProxy proxy,
                          DispatchAsync dispatcher,
                          EditRatingMessages messages,
                          PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
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
        dispatcher.execute(new SaveRatingAction(ratingDto), new ErrorHandlerAsyncCallback<GetResult<RatingDto>>(this) {
            @Override
            public void onSuccess(GetResult<RatingDto> result) {
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

        dispatcher.execute(new GetCarsAction(), new SafeAsyncCallback<GetResults<CarDto>>() {
            @Override
            public void onSuccess(GetResults<CarDto> result) {
                onGetCarsSuccess(result.getResults());
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.SLOT_MAIN_CONTENT, this);
    }

    private void onGetCarsSuccess(List<CarDto> carDtos) {
        getView().setAllowedCars(carDtos);
        getView().edit(new RatingDto());
    }
}
