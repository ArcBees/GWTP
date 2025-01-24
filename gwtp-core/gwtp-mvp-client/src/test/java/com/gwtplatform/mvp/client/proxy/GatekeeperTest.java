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

import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImplTest.ProxyPlaceBase;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.tester.DeferredCommandManager;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link Gatekeeper}.
 */
@RunWith(JukitoRunner.class)
public class GatekeeperTest {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(DeferredCommandManager.class).in(TestSingleton.class);
            bind(EventBus.class).to(SimpleEventBus.class).in(TestSingleton.class);
            bind(PlaceManager.class).to(PlaceManagerTestUtil.class).in(TestSingleton.class);
            bind(Gatekeeper.class).annotatedWith(Names.named("DenyGatekeeper")).to(DenyGatekeeper.class)
                    .in(TestSingleton.class);
            bind(Gatekeeper.class).annotatedWith(Names.named("GrantGatekeeper")).to(GrantGatekeeper.class)
                    .in(TestSingleton.class);
            bind(DummyProxyWithDenyGatekeeper.class);
            bind(DummyProxyPlaceWithDenyGatekeeper.class);
            bind(DummyProxyWithGrantGatekeeper.class);
            bind(DummyProxyPlaceWithGrantGatekeeper.class);
            bind(DummyProxyDefault.class);
            bind(DummyProxyPlaceDefault.class);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterWithDenyGatekeeper
            extends Presenter<View, DummyProxyPlaceWithDenyGatekeeper> {
        @Inject
        DummyPresenterWithDenyGatekeeper(EventBus eventBus, View view,
                DummyProxyPlaceWithDenyGatekeeper proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyWithDenyGatekeeper extends ProxyImpl<DummyPresenterWithDenyGatekeeper> {
        @Inject
        DummyProxyWithDenyGatekeeper(Provider<DummyPresenterWithDenyGatekeeper> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceWithDenyGatekeeper
            extends ProxyPlaceBase<DummyPresenterWithDenyGatekeeper> {
        @Inject
        DummyProxyPlaceWithDenyGatekeeper(DummyProxyWithDenyGatekeeper proxy,
                DeferredCommandManager deferredCommandManager,
                @Named("DenyGatekeeper") Gatekeeper gatekeeper) {
            super(new PlaceWithGatekeeper("dummyNameTokenWithDenyGatekeeper", gatekeeper), proxy,
                    deferredCommandManager);
        }
    }

    @TestMockSingleton
    abstract static class DummyPresenterWithGrantGatekeeper
            extends Presenter<View, DummyProxyPlaceWithGrantGatekeeper> {
        @Inject
        DummyPresenterWithGrantGatekeeper(EventBus eventBus, View view,
                DummyProxyPlaceWithGrantGatekeeper proxy) {
            super(eventBus, view, proxy);
        }

        @Override
        public final boolean isVisible() {
            return super.isVisible();
        }
    }

    @TestEagerSingleton
    static class DummyProxyWithGrantGatekeeper extends ProxyImpl<DummyPresenterWithGrantGatekeeper> {
        @Inject
        DummyProxyWithGrantGatekeeper(Provider<DummyPresenterWithGrantGatekeeper> presenter) {
            this.presenter = new StandardProvider<>(presenter);
        }
    }

    @TestEagerSingleton
    static class DummyProxyPlaceWithGrantGatekeeper
            extends ProxyPlaceBase<DummyPresenterWithGrantGatekeeper> {
        @Inject
        DummyProxyPlaceWithGrantGatekeeper(DummyProxyWithGrantGatekeeper proxy,
                DeferredCommandManager deferredCommandManager,
                @Named("GrantGatekeeper") Gatekeeper gatekeeper) {
            super(new PlaceWithGatekeeper("dummyNameTokenWithGrantGatekeeper", gatekeeper), proxy,
                    deferredCommandManager);
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

    static class DenyGatekeeper implements Gatekeeper {
        public boolean canReveal() {
            return false;
        }
    }

    static class GrantGatekeeper implements Gatekeeper {
        public boolean canReveal() {
            return true;
        }
    }

    // SUT
    @Inject
    PlaceManager placeManager;

    @Inject
    DeferredCommandManager deferredCommandManager;

    @Test
    public void placeManagerRevealDefaultPlaceWhenGatekeeperCanNotReveal(DummyPresenterDefault defaultPresenter) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenWithDenyGatekeeper").build();

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
    public void placeManagerRevealRequestPlaceWhenGatekeeperCanReveal(
            DummyPresenterWithGrantGatekeeper presenterWithGatekeeper) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("dummyNameTokenWithGrantGatekeeper").build();

        // When
        placeManager.revealPlace(placeRequest);
        deferredCommandManager.pump();

        // Then
        List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
        assertEquals(1, placeHierarchy.size());

        PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
        assertEquals(placeHierarchy.get(0), finalPlaceRequest);

        assertEquals("dummyNameTokenWithGrantGatekeeper", finalPlaceRequest.getNameToken());
        assertEquals(0, finalPlaceRequest.getParameterNames().size());

        verify(presenterWithGatekeeper).prepareFromRequest(finalPlaceRequest);
        verify(presenterWithGatekeeper).forceReveal();
    }
}
