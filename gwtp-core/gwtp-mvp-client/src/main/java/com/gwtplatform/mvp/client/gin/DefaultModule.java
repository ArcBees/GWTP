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

package com.gwtplatform.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
import com.gwtplatform.mvp.client.proxy.DefaultPlaceManager;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RouteTokenFormatter;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * Module with default GWTP bindings. You can
 * {@code install(new DefaultModule(MyPlaceManager.class))} instead of manually
 * binding the different classes to their default implementation.
 */
public class DefaultModule extends AbstractGinModule {
    private final Class<? extends PlaceManager> placeManagerClass;

    private final Class<? extends TokenFormatter> tokenFormatterClass;

    /**
     * When instantiating the module this way be sure to read
     * {@link DefaultPlaceManager}
     *
     * <b>Important!</b> If you use this class, don't forget to bind
     * {@link com.gwtplatform.mvp.client.annotations.DefaultPlace DefaultPlace},
     * {@link com.gwtplatform.mvp.client.annotations.ErrorPlace ErrorPlace} and
     * {@link com.gwtplatform.mvp.client.annotations.UnauthorizedPlace
     * UnauthorizedPlace} to Presenter name tokens in your Gin module.<br/>
     *
     * @see <a href="https://github.com/ArcBees/GWTP/wiki/PlaceManager">See
     *      PlaceManager wiki for more examples</a>
     */
    public DefaultModule() {
        this(DefaultPlaceManager.class, ParameterTokenFormatter.class);
    }

    /**
     * Manually setup a PlaceManager. See {@link DefaultPlaceManager} for more
     * details.<br/>
     *
     * @param placeManagerClass
     *            {@link DefaultPlaceManager} @see <a
     *            href="https://github.com/ArcBees/GWTP/wiki/PlaceManager">See
     *            PlaceManager wiki for more examples</a>
     */
    public DefaultModule(Class<? extends PlaceManager> placeManagerClass) {
        this(placeManagerClass, ParameterTokenFormatter.class);
    }

    /**
     * Manually setup a {@link PlaceManager} and {@link TokenFormatter}.
     *
     * <p>
     * See {@link DefaultPlaceManager}, {@link ParameterTokenFormatter} and {@link RouteTokenFormatter} for more
     * details.
     * </p>
     *
     * @param placeManagerClass   The {@link PlaceManager} implementation.
     * @param tokenFormatterClass The {@link TokenFormatter} implementation.
     */
    public DefaultModule(Class<? extends PlaceManager> placeManagerClass,
            Class<? extends TokenFormatter> tokenFormatterClass) {
        this.placeManagerClass = placeManagerClass;
        this.tokenFormatterClass = tokenFormatterClass;
    }

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(TokenFormatter.class).to(tokenFormatterClass).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();
        bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class).in(Singleton.class);
        bind(PlaceManager.class).to(placeManagerClass).in(Singleton.class);
    }
}
