/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.stats;

import java.util.Date;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.stats.StatisticsPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.stats.StatisticsPresenter.MyView;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.shared.api.StatisticsResource;
import com.gwtplatform.dispatch.rest.client.ResourceDelegate;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class StatisticsPresenter extends Presenter<MyView, MyProxy> implements StatisticsUiHandlers {
    interface MyView extends View, HasUiHandlers<StatisticsUiHandlers> {
        void setResult(String result);
    }

    @ProxyStandard
    @NameToken(NameTokens.STATS)
    interface MyProxy extends ProxyPlace<StatisticsPresenter> {
    }

    private static final String FAILED = "Failed";

    private final ResourceDelegate<StatisticsResource> statisticsDelegate;

    @Inject
    StatisticsPresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy,
            ResourceDelegate<StatisticsResource> statisticsDelegate) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN_CONTENT);

        this.statisticsDelegate = statisticsDelegate;

        getView().setUiHandlers(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void extractYear(final Date date) {
        getView().setResult("");

        statisticsDelegate
                .withCallback(new AsyncCallback<Integer>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        getView().setResult(FAILED);
                    }

                    @Override
                    public void onSuccess(Integer year) {
                        int expectedYear = 1900 + date.getYear();
                        if (year == expectedYear) {
                            getView().setResult(year.toString());
                        } else {
                            getView().setResult(FAILED);
                        }
                    }
                })
                .extractYearFromDate(date);
    }
}
