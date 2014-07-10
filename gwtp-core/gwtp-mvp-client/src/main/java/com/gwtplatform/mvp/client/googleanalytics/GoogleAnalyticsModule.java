/**
 * Copyright 2011 ArcBees Inc.
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

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class GoogleAnalyticsModule extends AbstractGinModule {

    public static class Builder {
        private final String userAccount;
        private boolean trackNavigationEvents = true;

        public Builder(final String userAccount) {
            this.userAccount = userAccount;
        }

        public GoogleAnalyticsModule build() {
            return new GoogleAnalyticsModule(userAccount, trackNavigationEvents);
        }

        public Builder trackNavigationEvents(final boolean trackNavigationEvents) {
            this.trackNavigationEvents = trackNavigationEvents;
            return this;
        }
    }

    private boolean trackNavigationEvents;
    private String userAccount;

    private GoogleAnalyticsModule(final String userAccount, final boolean trackNavigationEvents) {
        this.userAccount = userAccount;
        this.trackNavigationEvents = trackNavigationEvents;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(GaAccount.class).to(userAccount);
        bind(GoogleAnalyticsImpl.class).in(Singleton.class);
        bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class);
        if (trackNavigationEvents) {
            bind(GoogleAnalyticsNavigationTracker.class).asEagerSingleton();
        }
    }
}
