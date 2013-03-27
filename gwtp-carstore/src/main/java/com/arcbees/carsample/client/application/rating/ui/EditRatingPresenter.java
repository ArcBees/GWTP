package com.arcbees.carsample.client.application.rating.ui;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.event.DisplayMessageEvent;
import com.arcbees.carsample.client.application.rating.event.RatingAddedEvent;
import com.arcbees.carsample.client.application.rating.ui.EditRatingPresenter.MyView;
import com.arcbees.carsample.client.application.widget.message.Message;
import com.arcbees.carsample.client.application.widget.message.MessageStyle;
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
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;

public class EditRatingPresenter extends PresenterWidget<MyView> implements EditRatingUiHandlers {
    public interface MyView extends PopupView, HasUiHandlers<EditRatingUiHandlers> {
        void edit(Rating rating);

        void setAllowedCars(List<Car> cars);
    }

    private final DispatchAsync dispatcher;
    private final EditRatingMessages messages;

    @Inject
    public EditRatingPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatcher,
            final EditRatingMessages messages) {
        super(eventBus, view);

        this.dispatcher = dispatcher;
        this.messages = messages;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void createNew() {
        reveal();
    }

    @Override
    public void onCancel() {
        getView().hide();
    }

    @Override
    public void onSave(Rating rating) {
        dispatcher.execute(new SaveRatingAction(rating), new ErrorHandlerAsyncCallback<GetResult<Rating>>(this) {
            @Override
            public void onSuccess(GetResult<Rating> result) {
                DisplayMessageEvent.fire(EditRatingPresenter.this, new Message(messages.ratingSaved(),
                        MessageStyle.SUCCESS));
                RatingAddedEvent.fire(EditRatingPresenter.this, result.getResult());
                getView().hide();
            }
        });
    }

    private void reveal() {
        dispatcher.execute(new GetCarsAction(), new SafeAsyncCallback<GetResults<Car>>() {
            @Override
            public void onSuccess(GetResults<Car> result) {
                onGetCarsSuccess(result.getResults());
            }
        });
    }

    private void onGetCarsSuccess(List<Car> cars) {
        getView().setAllowedCars(cars);
        getView().edit(new Rating());
        RevealRootPopupContentEvent.fire(this, this);
    }
}
