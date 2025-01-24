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

package com.gwtplatform.mvp.client.proxy;

import java.util.List;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestEagerSingleton;
import org.jukito.TestMockSingleton;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.tester.DeferredCommandManager;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link PlaceManagerImpl}.
 */
@RunWith(JukitoRunner.class)
public class PlaceManagerImplTest {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            GWTMockUtilities.disarm();
            bind(DeferredCommandManager.class).in(TestSingleton.class);
            bind(EventBus.class).to(SimpleEventBus.class).in(TestSingleton.class);
            bind(PlaceManager.class).to(PlaceManagerTestUtil.class).in(TestSingleton.class);
            bind(DummyPresenterRedirect.class);
            bind(DummyPresenterRedirectNoHistory.class);
            bind(DummyProxyBasic.class);
            bind(DummyProxyPlaceBasic.class);
            bind(DummyProxyRedirect.class);
            bind(DummyProxyPlaceRedirect.class);
            bind(DummyProxyRedirectNoHistory.class);
            bind(DummyProxyPlaceRedirectNoHistory.class);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterBasic extends Presenter<View, DummyProxyPlaceBasic> {
        @Inject
        DummyPresenterBasic(EventBus eventBus, View view, DummyProxyPlaceBasic proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyBasic extends ProxyImpl<DummyPresenterBasic> {
        @Inject
        DummyProxyBasic(Provider<DummyPresenterBasic> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    abstract static class ProxyPlaceBase<P extends Presenter<?, ?>> extends ProxyPlaceImpl<P> {
        private final DeferredCommandManager deferredCommandManager;

        ProxyPlaceBase(Place place,
                Proxy<P> proxy,
                DeferredCommandManager deferredCommandManager) {
            super();
            super.setPlace(place);
            super.setProxy(proxy);
            this.deferredCommandManager = deferredCommandManager;
        }

        @Override
        void addDeferredCommand(Command command) {
            deferredCommandManager.addCommand(command);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceBasic extends ProxyPlaceBase<DummyPresenterBasic> {
        @Inject
        DummyProxyPlaceBasic(DummyProxyBasic proxy,
                DeferredCommandManager deferredCommandManager) {
            super(new PlaceImpl("dummyNameTokenBasic"), proxy, deferredCommandManager);
        }
    }

    /**
     * This presenter automatically redirects in prepareFromRequest to PresenterBasic.
     */
    @TestEagerSingleton
    static class DummyPresenterRedirect extends Presenter<View, DummyProxyPlaceBasic> {
        public PlaceRequest preparedRequest;
        public int prepareFromRequestCalls;
        public int revealInParentCalls;

        private final PlaceManager placeManager;

        @Inject
        DummyPresenterRedirect(EventBus eventBus, DummyProxyPlaceBasic proxy,
                PlaceManager placeManager) {
            super(eventBus, mock(View.class), proxy);
            this.placeManager = placeManager;
        }

        @Override
        public void prepareFromRequest(PlaceRequest request) {
            super.prepareFromRequest(request);
            ++prepareFromRequestCalls;
            preparedRequest = request;
            placeManager.revealPlace(new PlaceRequest.Builder().nameToken("dummyNameTokenBasic").build());
        }

        @Override
        protected void revealInParent() {
            ++revealInParentCalls;
        }
    }

    @TestEagerSingleton
    static class DummyProxyRedirect extends ProxyImpl<DummyPresenterRedirect> {
        @Inject
        DummyProxyRedirect(Provider<DummyPresenterRedirect> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceRedirect extends ProxyPlaceBase<DummyPresenterRedirect> {
        @Inject
        DummyProxyPlaceRedirect(DummyProxyRedirect proxy,
                DeferredCommandManager deferredCommandManager) {
            super(new PlaceImpl("dummyNameTokenRedirect"), proxy, deferredCommandManager);
        }
    }

    @TestEagerSingleton
    static class DummyPresenterRedirectNoHistory extends
            Presenter<View, DummyProxyPlaceRedirectNoHistory> {
        private static final String TOKEN = "dummyNameTokenRedirectNoHistory";
        private final PlaceManager placeManager;

        @Inject
        DummyPresenterRedirectNoHistory(EventBus eventBus,
                DummyProxyPlaceRedirectNoHistory proxy, PlaceManager placeManager) {
            super(eventBus, mock(View.class), proxy);
            this.placeManager = placeManager;
        }

        @Override
        public void prepareFromRequest(PlaceRequest request) {
            super.prepareFromRequest(request);
            // This call is deferred by GWTP
            placeManager.revealPlace(new PlaceRequest.Builder().nameToken("dummyNameTokenBasic").build(), false);
        }

        @Override
        protected void revealInParent() {
        }
    }

    @TestEagerSingleton
    static class DummyProxyRedirectNoHistory extends ProxyImpl<DummyPresenterRedirectNoHistory> {
        @Inject
        DummyProxyRedirectNoHistory(Provider<DummyPresenterRedirectNoHistory> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceRedirectNoHistory extends
            ProxyPlaceBase<DummyPresenterRedirectNoHistory> {

        @Inject
        DummyProxyPlaceRedirectNoHistory(DummyProxyRedirectNoHistory proxy,
                DeferredCommandManager deferredCommandManager) {
            super(new PlaceImpl(DummyPresenterRedirectNoHistory.TOKEN), proxy, deferredCommandManager);
        }
    }

    @TestSingleton
    static class NavigationEventSpy implements NavigationHandler {
        int navCount;
        NavigationEvent lastEvent;

        @Override
        public void onNavigation(NavigationEvent navigationEvent) {
            navCount++;
            lastEvent = navigationEvent;
        }
    }

    // SUT
    @Inject
    PlaceManager placeManager;

    @Inject
    DeferredCommandManager deferredCommandManager;
    @Inject
    PlaceManagerWindowMethodsTestUtil gwtWindowMethods;
    @Inject
    NavigationEventSpy navigationHandler;
    @Inject
    EventBus eventBus;

    @Test
    public void placeManagerRevealPlaceStandard(
            DummyPresenterBasic presenter) {

        // Given
        eventBus.addHandler(NavigationEvent.getType(), navigationHandler);

        // When
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken("dummyNameTokenBasic").with("dummyParam",
                "dummyValue").build());
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), placeRequest);

        assertEquals("dummyNameTokenBasic", placeRequest.getNameToken());
        assertEquals(1, placeRequest.getParameterNames().size());
        assertEquals("dummyValue", placeRequest.getParameter("dummyParam", null));

        verify(presenter).prepareFromRequest(placeRequest);
        verify(presenter).forceReveal();

        verify(gwtWindowMethods).setBrowserHistoryToken(isNull(), eq(false));

        assertEquals(1, navigationHandler.navCount);
        placeRequest = navigationHandler.lastEvent.getRequest();
        assertEquals("dummyNameTokenBasic", placeRequest.getNameToken());
        assertEquals(1, placeRequest.getParameterNames().size());
        assertEquals("dummyValue", placeRequest.getParameter("dummyParam", null));
    }

    /**
     * DummyPresenterRedirectNoHistory makes a call to revealPlace in prepareFromRequest. This call
     * is deferred but useBrowserUrl must be preserved and the history token must be set only once.
     */
    @Test
    public void placeManagerRevealPlaceRedirectInPrepareFromRequestNoHistory() {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(DummyPresenterRedirectNoHistory.TOKEN).build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        // assert called only once
        verify(gwtWindowMethods, times(1)).setBrowserHistoryToken(isNull(), eq(false));

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals("dummyNameTokenBasic", finalPlaceRequest.getNameToken());
    }

    @Test
    public void placeManagerRevealPlaceRedirectInPrepareFromRequest(
            DummyPresenterRedirect presenter,
            DummyPresenterBasic otherPresenter) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenRedirect").with("dummyParam",
                "dummyValue").build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), finalPlaceRequest);

        assertEquals("dummyNameTokenBasic", finalPlaceRequest.getNameToken());
        assertEquals(0, finalPlaceRequest.getParameterNames().size());

        assertEquals(1, presenter.prepareFromRequestCalls);
        assertEquals(placeRequest, presenter.preparedRequest);
        assertEquals(0, presenter.revealInParentCalls);

        verify(otherPresenter).prepareFromRequest(finalPlaceRequest);
        verify(otherPresenter).forceReveal();
    }

    @Test
    public void placeManagerUserUpdateHistoryWhenRevealPlace(
            DummyPresenterRedirect presenter,
            DummyPresenterBasic otherPresenter) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenRedirect").with("dummyParam",
                "dummyValue").build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), finalPlaceRequest);

        assertEquals("dummyNameTokenBasic", finalPlaceRequest.getNameToken());
        assertEquals(0, finalPlaceRequest.getParameterNames().size());

        assertEquals(1, presenter.prepareFromRequestCalls);
        assertEquals(placeRequest, presenter.preparedRequest);
        assertEquals(0, presenter.revealInParentCalls);

        verify(otherPresenter).prepareFromRequest(finalPlaceRequest);
        verify(otherPresenter).forceReveal();
    }

}
