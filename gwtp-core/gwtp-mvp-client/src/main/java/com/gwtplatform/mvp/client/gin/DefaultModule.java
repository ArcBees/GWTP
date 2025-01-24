/*
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

import java.lang.annotation.Annotation;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.common.client.CommonGinModule;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
import com.gwtplatform.mvp.client.proxy.DefaultPlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;

import jakarta.inject.Singleton;

/**
 * Module with default GWTP bindings. You can use {@link DefaultModule.Builder} to configure each required bindings.
 */
public class DefaultModule extends AbstractGinModule {
    /**
     * A builder to configure the bindings used by {@link DefaultModule}.
     */
    public static class Builder {
        private String defaultPlace;
        private String errorPlace;
        private String unauthorizedPlace;
        private Class<? extends EventBus> eventBusClass = SimpleEventBus.class;
        private Class<? extends PlaceManager> placeManagerClass = DefaultPlaceManager.class;
        private Class<? extends TokenFormatter> tokenFormatterClass = ParameterTokenFormatter.class;

        public Builder() {
        }

        public Builder placeManager(Class<? extends PlaceManager> placeManagerClass) {
            this.placeManagerClass = placeManagerClass;
            return this;
        }

        public Builder tokenFormatter(Class<? extends TokenFormatter> tokenFormatterClass) {
            this.tokenFormatterClass = tokenFormatterClass;
            return this;
        }

        public Builder eventBus(Class<? extends EventBus> eventBusClass) {
            this.eventBusClass = eventBusClass;
            return this;
        }

        /**
         * Bind a value to the constant annotated with {@link DefaultPlace @DefaultPlace}. Assigning a value to this
         * constant is mandatory if using {@link DefaultPlaceManager}. As a convenience, you may have your custom {@link
         * PlaceManager} reuse this constant.
         */
        public Builder defaultPlace(String defaultPlace) {
            this.defaultPlace = defaultPlace;
            return this;
        }

        /**
         * Bind a value to the constant annotated with {@link ErrorPlace @ErrorPlace}. Assigning a value to this
         * constant is mandatory if using {@link DefaultPlaceManager}. As a convenience, you may have your custom {@link
         * PlaceManager} reuse this constant.
         */
        public Builder errorPlace(String errorPlace) {
            this.errorPlace = errorPlace;
            return this;
        }

        /**
         * Bind a value to the constant annotated with {@link UnauthorizedPlace @UnauthorizedPlace}. Assigning a value
         * to this constant is mandatory if using {@link DefaultPlaceManager}. As a convenience, you may have your
         * custom {@link PlaceManager} reuse this constant.
         */
        public Builder unauthorizedPlace(String unauthorizedPlace) {
            this.unauthorizedPlace = unauthorizedPlace;
            return this;
        }

        public DefaultModule build() {
            return new DefaultModule(this);
        }

        Class<? extends TokenFormatter> getTokenFormatterClass() {
            return tokenFormatterClass;
        }

        Class<? extends PlaceManager> getPlaceManagerClass() {
            return placeManagerClass;
        }

        Class<? extends EventBus> getEventBusClass() {
            return eventBusClass;
        }

        String getUnauthorizedPlace() {
            return unauthorizedPlace;
        }

        String getErrorPlace() {
            return errorPlace;
        }

        String getDefaultPlace() {
            return defaultPlace;
        }
    }

    private final Builder builder;

    /**
     * When instantiating the module this way be sure to read {@link DefaultPlaceManager} and manually bind the required
     * constants. Consider using {@link DefaultModule.Builder} if you need to use a different implementation than the
     * default one for a binding.
     * <p>
     * <b>Important!</b> If you use this class, don't forget to bind {@link DefaultPlace}, {@link ErrorPlace} and {@link
     * UnauthorizedPlace} to name tokens associated to a presenter.<br/>
     */
    public DefaultModule() {
        this(new Builder());
    }

    private DefaultModule(Builder builder) {
        this.builder = builder;
    }

    @Override
    protected void configure() {
        install(new CommonGinModule());

        bind(RootPresenter.class).asEagerSingleton();

        bind(EventBus.class).to(builder.getEventBusClass()).in(Singleton.class);
        bind(TokenFormatter.class).to(builder.getTokenFormatterClass()).in(Singleton.class);
        bind(PlaceManager.class).to(builder.getPlaceManagerClass()).in(Singleton.class);
        bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class).in(Singleton.class);

        maybeBindConstant(DefaultPlace.class, builder.getDefaultPlace());
        maybeBindConstant(ErrorPlace.class, builder.getErrorPlace());
        maybeBindConstant(UnauthorizedPlace.class, builder.getUnauthorizedPlace());
    }

    private void maybeBindConstant(Class<? extends Annotation> annotationClass, String nameToken) {
        if (nameToken != null && !nameToken.isEmpty()) {
            bindConstant().annotatedWith(annotationClass).to(nameToken);
        }
    }
}
