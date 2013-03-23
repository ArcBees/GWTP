package com.arcbees.carsample.client.application.report;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.ApplicationPresenter;
import com.arcbees.carsample.client.application.event.ActionBarVisibilityEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent.ActionType;
import com.arcbees.carsample.client.place.NameTokens;
import com.arcbees.carsample.client.security.LoggedInGatekeeper;
import com.arcbees.carsample.client.util.SafeAsyncCallback;
import com.arcbees.carsample.shared.dispatch.GetAverageCarRatingByManufacturerAction;
import com.arcbees.carsample.shared.dispatch.GetResults;
import com.arcbees.carsample.shared.dto.ManufacturerRatingDto;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class ReportPresenter extends Presenter<ReportPresenter.MyView, ReportPresenter.MyProxy> {
    public interface MyView extends View {
        void displayReport(List<ManufacturerRatingDto> manufacturerRatings);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.report)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<ReportPresenter> {
    }

    private final DispatchAsync dispatcher;

    @Inject
    public ReportPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
            final DispatchAsync dispatcher) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, new ArrayList<ActionType>(), true);

        dispatcher.execute(new GetAverageCarRatingByManufacturerAction(),
                new SafeAsyncCallback<GetResults<ManufacturerRatingDto>>() {
                    @Override
                    public void onSuccess(GetResults<ManufacturerRatingDto> result) {
                        getView().displayReport(result.getResults());
                    }
                });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }
}
