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

package com.gwtplatform.mvp.client.googleanalytics;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 * Used to provide complete URL's in your Google Analytics results when {@link RouteTokenFormatter}
 * is implemented. Replaces the route parameters in the name token with those supplied in the request
 * parameters. Route parameters missing in the request will remain unescaped. <br>
 * <br>
 * i.e. <code>/foo/{name}/bar/{number}</code> becomes <code>/foo/sam/bar/223</code> in Google
 * Analytics results<br>
 * <br>
 * Note: All parameters will be added to the URL, including those which are <b>NOT</b> route parameters.
 * 
 * @see the original {@link GoogleAnalyticsNavigationTracker} by Christian Goudrea for implementation details
 */
public class GoogleAnalyticsRouteTokenNavigationTracker implements NavigationHandler {

    private final GoogleAnalytics analytics;

    private final PlaceManager placeManager;

    @Inject
    public GoogleAnalyticsRouteTokenNavigationTracker(@GaAccount final String gaAccount,
            final PlaceManager placeManager,
            final EventBus eventBus, final GoogleAnalytics analytics) {
        this.analytics = analytics;
        this.placeManager = placeManager;

        if (GWT.isScript()) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    analytics.init(gaAccount);

                    eventBus.addHandler(NavigationEvent.getType(), GoogleAnalyticsRouteTokenNavigationTracker.this);
                }
            });
        }
    }

    @Override
    public void onNavigation(NavigationEvent navigationEvent) {
        final String pageName = placeManager.buildHistoryToken(navigationEvent.getRequest());
        analytics.trackPageview(pageName);
    }
}
