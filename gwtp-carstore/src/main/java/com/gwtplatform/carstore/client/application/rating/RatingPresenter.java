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
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.DeleteRatingAction;
import com.gwtplatform.carstore.shared.dispatch.GetRatingsAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class RatingPresenter extends Presenter<RatingPresenter.MyView, RatingPresenter.MyProxy> implements
        RatingUiHandlers, RatingAddedHandler, ActionBarEvent.ActionBarHandler {
    public interface MyView extends View, HasUiHandlers<RatingUiHandlers> {
        void displayRatings(List<Rating> results);

        void removeRating(Rating rating);

        void addRating(Rating rating);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.rating)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<RatingPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final EditRatingPresenter editRatingPresenter;
    private final PlaceManager placeManager;

    @Inject
    public RatingPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
            final DispatchAsync dispatcher, final EditRatingPresenter editRatingPresenter,
            final PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.editRatingPresenter = editRatingPresenter;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getRating())) {
            placeManager.revealPlace(new PlaceRequest(NameTokens.getDetailRating()));
        }
    }

    @Override
    public void onCreate() {
        editRatingPresenter.createNew();
    }

    @Override
    public void onDelete(final Rating rating) {
        dispatcher.execute(new DeleteRatingAction(rating), new SafeAsyncCallback<NoResults>() {
            @Override
            public void onSuccess(NoResults result) {
                getView().removeRating(rating);
            }
        });
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(new ActionType[] { ActionType.ADD }), true);

        dispatcher.execute(new GetRatingsAction(), new SafeAsyncCallback<GetResults<Rating>>() {
            @Override
            public void onSuccess(GetResults<Rating> result) {
                getView().displayRatings(result.getResults());
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    @ProxyEvent
    @Override
    public void onRatingAdded(RatingAddedEvent event) {
        getView().addRating(event.getRating());
    }
}
