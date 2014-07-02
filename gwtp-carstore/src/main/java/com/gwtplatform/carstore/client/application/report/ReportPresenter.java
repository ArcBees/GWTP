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
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class ReportPresenter extends Presenter<ReportPresenter.MyView, ReportPresenter.MyProxy> {
    public interface MyView extends View {
        void displayReport(List<ManufacturerRatingDto> manufacturerRatings);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.REPORT)
    public interface MyProxy extends ProxyPlace<ReportPresenter> {
    }

    private final RestDispatch dispatcher;
    private final ManufacturerService manufacturerService;

    @Inject
    ReportPresenter(EventBus eventBus,
                    MyView view,
                    MyProxy proxy,
                    RestDispatch dispatcher,
                    ManufacturerService manufacturerService) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN_CONTENT);

        this.dispatcher = dispatcher;
        this.manufacturerService = manufacturerService;
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, new ArrayList<ActionType>(), true);

        dispatcher.execute(manufacturerService.getAverageRatings(),
                new AbstractAsyncCallback<List<ManufacturerRatingDto>>() {
                    @Override
                    public void onSuccess(List<ManufacturerRatingDto> manufacturerRatings) {
                        getView().displayReport(manufacturerRatings);
                    }
                });
    }
}
