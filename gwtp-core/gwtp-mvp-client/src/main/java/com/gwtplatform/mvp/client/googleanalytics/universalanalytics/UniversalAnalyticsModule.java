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
package com.gwtplatform.mvp.client.googleanalytics.universalanalytics;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.name.Names;
import com.gwtplatform.mvp.client.googleanalytics.GaAccount;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsNavigationTracker;

public class UniversalAnalyticsModule extends AbstractGinModule {
    public static class Builder {
        private final String userAccount;
        private boolean trackNavigationEvents = true;
        private boolean autoCreate = true;

        public Builder(final String userAccount) {
            this.userAccount = userAccount;
        }

        /**
         * Set this to false if you want to create the universal analytics tracker with extra option.
         * You can manually create the tracker by calling analytics.create().options().go() from your bootstrapper;
         * @param autoCreate
         * @return
         */
        public Builder autoCreate(final boolean autoCreate) {
            this.autoCreate = autoCreate;
            return this;
        }

        public UniversalAnalyticsModule build() {
            return new UniversalAnalyticsModule(userAccount, trackNavigationEvents, autoCreate);
        }

        public Builder trackNavigationEvents(final boolean trackNavigationEvents) {
            this.trackNavigationEvents = trackNavigationEvents;
            return this;
        }
    }

    private final boolean trackNavigationEvents;
    private final String userAccount;
    private boolean autoCreate;

    private UniversalAnalyticsModule(final String userAccount,
            final boolean trackNavigationEvents,
            final boolean autoCreate) {
        this.userAccount = userAccount;
        this.trackNavigationEvents = trackNavigationEvents;
        this.autoCreate = autoCreate;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(GaAccount.class).to(userAccount);
        bindConstant().annotatedWith(Names.named("uaAutoCreate")).to(autoCreate);
        bind(UniversalAnalyticsImpl.class).asEagerSingleton();
        bind(GoogleAnalytics.class).to(UniversalAnalyticsImpl.class);
        bind(UniversalAnalytics.class).to(UniversalAnalyticsImpl.class);

        if (trackNavigationEvents) {
            bind(GoogleAnalyticsNavigationTracker.class).asEagerSingleton();
        }
    }
}
