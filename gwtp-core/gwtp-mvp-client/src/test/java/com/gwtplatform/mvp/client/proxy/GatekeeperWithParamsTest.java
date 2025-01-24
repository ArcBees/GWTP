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

import java.util.Arrays;
import java.util.List;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestEagerSingleton;
import org.jukito.TestMockSingleton;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImplTest.ProxyPlaceBase;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.tester.DeferredCommandManager;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link GatekeeperWithParams}.
 */
@RunWith(JukitoRunner.class)
public class GatekeeperWithParamsTest {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(DeferredCommandManager.class).in(TestSingleton.class);
            bind(EventBus.class).to(SimpleEventBus.class).in(TestSingleton.class);
            bind(PlaceManager.class).to(PlaceManagerTestUtil.class).in(TestSingleton.class);
            bind(GatekeeperWithParams.class).to(HasRoleGatekeeper.class).in(TestSingleton.class);
            bind(DummyProxyWithDenyGatekeeperWithParams.class);
            bind(DummyProxyPlaceWithDenyGatekeeperWithParams.class);
            bind(DummyProxyWithGrantGatekeeperWithParams.class);
            bind(DummyProxyPlaceWithGrantGatekeeperWithParams.class);
            bind(DummyProxyDefault.class);
            bind(DummyProxyPlaceDefault.class);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterWithDenyGatekeeperWithParams
            extends Presenter<View, DummyProxyPlaceWithDenyGatekeeperWithParams> {
        @Inject
        DummyPresenterWithDenyGatekeeperWithParams(EventBus eventBus, View view,
                DummyProxyPlaceWithDenyGatekeeperWithParams proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyWithDenyGatekeeperWithParams
            extends ProxyImpl<DummyPresenterWithDenyGatekeeperWithParams> {
        @Inject
        DummyProxyWithDenyGatekeeperWithParams(
                Provider<DummyPresenterWithDenyGatekeeperWithParams> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceWithDenyGatekeeperWithParams
            extends ProxyPlaceBase<DummyPresenterWithDenyGatekeeperWithParams> {
        @Inject
        DummyProxyPlaceWithDenyGatekeeperWithParams(
                DummyProxyWithDenyGatekeeperWithParams proxy,
                DeferredCommandManager deferredCommandManager,
                GatekeeperWithParams gatekeeper) {
            super(new PlaceWithGatekeeperWithParams("dummyNameTokenWithDenyGatekeeperWithParams",
                    gatekeeper, new String[]{"ROLE_ADMIN"}), proxy, deferredCommandManager);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterWithGrantGatekeeperWithParams
            extends Presenter<View, DummyProxyPlaceWithGrantGatekeeperWithParams> {
        @Inject
        DummyPresenterWithGrantGatekeeperWithParams(EventBus eventBus, View view,
                DummyProxyPlaceWithGrantGatekeeperWithParams proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyWithGrantGatekeeperWithParams
            extends ProxyImpl<DummyPresenterWithGrantGatekeeperWithParams> {
        @Inject
        DummyProxyWithGrantGatekeeperWithParams(
                Provider<DummyPresenterWithGrantGatekeeperWithParams> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceWithGrantGatekeeperWithParams
            extends ProxyPlaceBase<DummyPresenterWithGrantGatekeeperWithParams> {
        @Inject
        DummyProxyPlaceWithGrantGatekeeperWithParams(
                DummyProxyWithGrantGatekeeperWithParams proxy,
                DeferredCommandManager deferredCommandManager,
                GatekeeperWithParams gatekeeper) {
            super(new PlaceWithGatekeeperWithParams("dummyNameTokenWithGrantGatekeeperWithParams",
                    gatekeeper, new String[]{"ROLE_USER"}), proxy, deferredCommandManager);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterDefault extends Presenter<View, DummyProxyPlaceDefault> {
        @Inject
        DummyPresenterDefault(EventBus eventBus, View view, DummyProxyPlaceDefault proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyDefault extends ProxyImpl<DummyPresenterDefault> {
        @Inject
        DummyProxyDefault(Provider<DummyPresenterDefault> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceDefault extends ProxyPlaceBase<DummyPresenterDefault> {
        @Inject
        DummyProxyPlaceDefault(DummyProxyDefault proxy,
                DeferredCommandManager deferredCommandManager) {
            super(new PlaceImpl("defaultPlace"), proxy, deferredCommandManager);
        }
    }

    static class HasRoleGatekeeper implements GatekeeperWithParams {
        private static final String[] CURRENT_USER_ROLES = new String[]{"ROLE_USER"};
        private String role;

        public boolean canReveal() {
            return Arrays.asList(CURRENT_USER_ROLES).contains(this.role);
        }

        public GatekeeperWithParams withParams(String[] params) {
            if (params.length != 1) {
                throw new IllegalArgumentException("Just one parameter is expected");
            }
            this.role = params[0];
            return this;
        }
    }

    // SUT
    @Inject
    PlaceManager placeManager;

    @Inject
    DeferredCommandManager deferredCommandManager;

    @Test
    public void placeManagerRevealDefaultPlaceWhenGatekeeperWithParamsCanNotReveal(DummyPresenterDefault
            defaultPresenter) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenWithDenyGatekeeperWithParams")
                .build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), finalPlaceRequest);

        assertEquals("defaultPlace", finalPlaceRequest.getNameToken());
        assertEquals(0, finalPlaceRequest.getParameterNames().size());

        verify(defaultPresenter).prepareFromRequest(finalPlaceRequest);
        verify(defaultPresenter).forceReveal();
    }

    @Test
    public void placeManagerRevealRequestPlaceWhenGatekeeperWithParamsCanReveal(
            DummyPresenterWithGrantGatekeeperWithParams presenter) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenWithGrantGatekeeperWithParams")
                .build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), finalPlaceRequest);

        assertEquals("dummyNameTokenWithGrantGatekeeperWithParams", finalPlaceRequest.getNameToken());
        assertEquals(0, finalPlaceRequest.getParameterNames().size());

        verify(presenter).prepareFromRequest(finalPlaceRequest);
        verify(presenter).forceReveal();
    }
}
