/**
 * Copyright 2010 ArcBees Inc.
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
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;

/**
 * This class let's you register every navigation event to a Google Analytics
 * account. To use it, you must bind GoogleAnalytics as eager singleton in your
 * gin module and also bind the annotation {@link GaAccount} to your Google
 * Analytics account number:
 * <p />
 * <code>bind(GoogleAnalyticsImpl.class).to(GoogleAnalytics.class).asEagerSingleton();
 * bindConstant().annotatedWith(GaAccount.class).to("UA-12345678-1");</code>
 * <p />
 * If you want to log custom events, see {@link GoogleAnalytics}.
 *
 * @author Christian Goudreau
 */
public class GoogleAnalyticsNavigationTracker implements NavigationHandler {
  private final GoogleAnalytics analytics;

  @Inject
  public GoogleAnalyticsNavigationTracker(@GaAccount final String gaAccount,
      final EventBus eventBus, final GoogleAnalytics analytics) {
    this.analytics = analytics;

    if (GWT.isScript()) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          analytics.init(gaAccount);

          eventBus.addHandler(NavigationEvent.getType(), GoogleAnalyticsNavigationTracker.this);
        }
      });
    }
  }

  @Override
  public void onNavigation(NavigationEvent navigationEvent) {
    analytics.trackPageview(navigationEvent.getRequest().getNameToken());
  }
}
