package com.gwtplatform.carstore.client.application.report;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.ManufacturerService;
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
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
    private final ManufacturerService manufacturerService;

    @Inject
    public ReportPresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy,
            DispatchAsync dispatcher,
            ManufacturerService manufacturerService) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.manufacturerService = manufacturerService;
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, new ArrayList<ActionType>(), true);

        dispatcher.execute(manufacturerService.getAverageRatings(),
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
