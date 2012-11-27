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

package com.gwtplatform.mvp.client.proxy;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.tester.DeferredCommandManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestEagerSingleton;
import org.jukito.TestMockSingleton;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
/**
 * Unit tests for {@link PlaceManagerImpl}.
 *
 * @author Philippe Beaudoin
 */
@RunWith(JukitoRunner.class)
public class PlaceManagerImplTest {

  /**
   * Guice test module.
   */
  public static class Module extends JukitoModule {
    @Override
    protected void configureTest() {
      bind(DeferredCommandManager.class).in(TestSingleton.class);
      bind(EventBus.class).to(SimpleEventBus.class).in(TestSingleton.class);
      bind(PlaceManager.class).to(PlaceManagerTestUtil.class).in(TestSingleton.class);
      bind(Gatekeeper.class).annotatedWith(Names.named("DenyGatekeeper")).to(DenyGatekeeper.class).in(TestSingleton.class);
      bind(Gatekeeper.class).annotatedWith(Names.named("GrantGatekeeper")).to(GrantGatekeeper.class).in(TestSingleton.class);
      bind(ParameterizedGatekeeper.class).to(HasRoleGatekeeper.class).in(TestSingleton.class);
      bind(DummyPresenterRedirect.class);
      bind(DummyPresenterRedirectNoHistory.class);
      bind(DummyProxyBasic.class);
      bind(DummyProxyPlaceBasic.class);
      bind(DummyProxyRedirect.class);
      bind(DummyProxyPlaceRedirect.class);
      bind(DummyProxyRedirectNoHistory.class);
      bind(DummyProxyPlaceRedirectNoHistory.class);
      bind(DummyProxyWithDenyGatekeeper.class);
      bind(DummyProxyPlaceWithDenyGatekeeper.class);
      bind(DummyProxyWithGrantGatekeeper.class);
      bind(DummyProxyPlaceWithGrantGatekeeper.class);
      bind(DummyProxyWithDenyParameterizedGatekeeper.class);
      bind(DummyProxyPlaceWithDenyParameterizedGatekeeper.class);
      bind(DummyProxyWithGrantParameterizedGatekeeper.class);
      bind(DummyProxyPlaceWithGrantParameterizedGatekeeper.class);
      bind(DummyProxyDefault.class);
      bind(DummyProxyPlaceDefault.class);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterBasic extends Presenter<View,DummyProxyPlaceBasic> {
    @Inject
    public DummyPresenterBasic(EventBus eventBus, View view, DummyProxyPlaceBasic proxy) {
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
    public DummyProxyBasic(Provider<DummyPresenterBasic> presenter) {
        this.presenter = new StandardProvider<DummyPresenterBasic>(presenter);
    };
  }

  abstract static class ProxyPlaceBase<P extends Presenter<?,?>> extends ProxyPlaceImpl<P> {
    private final DeferredCommandManager deferredCommandManager;

    public ProxyPlaceBase(Place place,
        Proxy<P> proxy,
        DeferredCommandManager deferredCommandManager) {
        super();
        this.place = place;
        this.proxy = proxy;
        this.deferredCommandManager = deferredCommandManager;
    };

    @Override
    void addDeferredCommand(Command command) {
      deferredCommandManager.addCommand(command);
    }
  }

  @TestEagerSingleton
  static class DummyProxyPlaceBasic extends ProxyPlaceBase<DummyPresenterBasic> {
    @Inject
    public DummyProxyPlaceBasic(DummyProxyBasic proxy,
        DeferredCommandManager deferredCommandManager) {
        super(new PlaceImpl("dummyNameTokenBasic"), proxy, deferredCommandManager);
    }
  }

  /**
   * This presenter automatically redirects in prepareFromRequest to PresenterBasic.
   */
  @TestEagerSingleton
  static class DummyPresenterRedirect extends Presenter<View,DummyProxyPlaceBasic> {
    private final PlaceManager placeManager;

    public PlaceRequest preparedRequest;
    public int prepareFromRequestCalls;
    public int revealInParentCalls;

    @Inject
    public DummyPresenterRedirect(EventBus eventBus, DummyProxyPlaceBasic proxy,
        PlaceManager placeManager) {
      super(eventBus, mock(View.class), proxy);
      this.placeManager = placeManager;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
      super.prepareFromRequest(request);
      ++prepareFromRequestCalls;
      preparedRequest = request;
      placeManager.revealPlace(new PlaceRequest("dummyNameTokenBasic"));
    }

    @Override
    protected void revealInParent() {
      ++revealInParentCalls;
    }
  }

  @TestEagerSingleton
  static class DummyProxyRedirect extends ProxyImpl<DummyPresenterRedirect> {
    @Inject
    public DummyProxyRedirect(Provider<DummyPresenterRedirect> presenter) {
        this.presenter = new StandardProvider<DummyPresenterRedirect>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceRedirect extends ProxyPlaceBase<DummyPresenterRedirect> {
    @Inject
    public DummyProxyPlaceRedirect(DummyProxyRedirect proxy,
        DeferredCommandManager deferredCommandManager) {
        super(new PlaceImpl("dummyNameTokenRedirect"), proxy, deferredCommandManager);
    }
  }

  @TestEagerSingleton
  static class DummyPresenterRedirectNoHistory extends
      Presenter<View,DummyProxyPlaceRedirectNoHistory> {
    private static final String TOKEN = "dummyNameTokenRedirectNoHistory";
    private final PlaceManager placeManager;

    @Inject
    public DummyPresenterRedirectNoHistory(EventBus eventBus,
        DummyProxyPlaceRedirectNoHistory proxy, PlaceManager placeManager) {
      super(eventBus, mock(View.class), proxy);
      this.placeManager = placeManager;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
      super.prepareFromRequest(request);
      // This call is deferred by GWTP
      placeManager.revealPlace(new PlaceRequest("dummyNameTokenBasic"), false);
    }

    @Override
    protected void revealInParent() {
    }
  }

  @TestEagerSingleton
  static class DummyProxyRedirectNoHistory extends ProxyImpl<DummyPresenterRedirectNoHistory> {
    @Inject
    public DummyProxyRedirectNoHistory(Provider<DummyPresenterRedirectNoHistory> presenter) {
        this.presenter = new StandardProvider<DummyPresenterRedirectNoHistory>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceRedirectNoHistory extends
      ProxyPlaceBase<DummyPresenterRedirectNoHistory> {

    @Inject
    public DummyProxyPlaceRedirectNoHistory(DummyProxyRedirectNoHistory proxy,
        DeferredCommandManager deferredCommandManager) {
      super(new PlaceImpl(DummyPresenterRedirectNoHistory.TOKEN), proxy, deferredCommandManager);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterWithDenyGatekeeper
  extends Presenter<View,DummyProxyPlaceWithDenyGatekeeper> {
    @Inject
    public DummyPresenterWithDenyGatekeeper(EventBus eventBus, View view,
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
    public DummyProxyWithDenyGatekeeper(Provider<DummyPresenterWithDenyGatekeeper> presenter) {
        this.presenter = new StandardProvider<DummyPresenterWithDenyGatekeeper>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceWithDenyGatekeeper
  extends ProxyPlaceBase<DummyPresenterWithDenyGatekeeper> {
    @Inject
    public DummyProxyPlaceWithDenyGatekeeper(DummyProxyWithDenyGatekeeper proxy,
        DeferredCommandManager deferredCommandManager,
        @Named("DenyGatekeeper")Gatekeeper gatekeeper) {
      super(new PlaceWithGatekeeper("dummyNameTokenWithDenyGatekeeper", gatekeeper), proxy,
            deferredCommandManager);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterWithGrantGatekeeper
  extends Presenter<View,DummyProxyPlaceWithGrantGatekeeper> {
    @Inject
    public DummyPresenterWithGrantGatekeeper(EventBus eventBus, View view,
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
    public DummyProxyWithGrantGatekeeper(Provider<DummyPresenterWithGrantGatekeeper> presenter) {
        this.presenter = new StandardProvider<DummyPresenterWithGrantGatekeeper>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceWithGrantGatekeeper
  extends ProxyPlaceBase<DummyPresenterWithGrantGatekeeper> {
    @Inject
    public DummyProxyPlaceWithGrantGatekeeper(DummyProxyWithGrantGatekeeper proxy,
        DeferredCommandManager deferredCommandManager,
        @Named("GrantGatekeeper")Gatekeeper gatekeeper) {
      super(new PlaceWithGatekeeper("dummyNameTokenWithGrantGatekeeper", gatekeeper), proxy,
            deferredCommandManager);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterWithDenyParameterizedGatekeeper
  extends Presenter<View,DummyProxyPlaceWithDenyParameterizedGatekeeper> {
    @Inject
    public DummyPresenterWithDenyParameterizedGatekeeper(EventBus eventBus, View view,
        DummyProxyPlaceWithDenyParameterizedGatekeeper proxy) {
      super(eventBus, view, proxy);
    }
    @Override
    public final boolean isVisible() {
      return super.isVisible();
    }
  }

  @TestEagerSingleton
  static class DummyProxyWithDenyParameterizedGatekeeper
  extends ProxyImpl<DummyPresenterWithDenyParameterizedGatekeeper> {
    @Inject
    public DummyProxyWithDenyParameterizedGatekeeper(
        Provider<DummyPresenterWithDenyParameterizedGatekeeper> presenter) {
        this.presenter = new StandardProvider<DummyPresenterWithDenyParameterizedGatekeeper>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceWithDenyParameterizedGatekeeper
  extends ProxyPlaceBase<DummyPresenterWithDenyParameterizedGatekeeper> {
    @Inject
    public DummyProxyPlaceWithDenyParameterizedGatekeeper(
        DummyProxyWithDenyParameterizedGatekeeper proxy,
        DeferredCommandManager deferredCommandManager,
        ParameterizedGatekeeper pGatekeeper) {
        super(new PlaceWithParameterizedGatekeeper("dummyNameTokenWithDenyParameterizedGatekeeper",
            pGatekeeper, new String[] {"ROLE_ADMIN"}), proxy, deferredCommandManager);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterWithGrantParameterizedGatekeeper
  extends Presenter<View,DummyProxyPlaceWithGrantParameterizedGatekeeper> {
    @Inject
    public DummyPresenterWithGrantParameterizedGatekeeper(EventBus eventBus, View view,
        DummyProxyPlaceWithGrantParameterizedGatekeeper proxy) {
      super(eventBus, view, proxy);
    }
    @Override
    public final boolean isVisible() {
      return super.isVisible();
    }
  }

  @TestEagerSingleton
  static class DummyProxyWithGrantParameterizedGatekeeper
  extends ProxyImpl<DummyPresenterWithGrantParameterizedGatekeeper> {
    @Inject
    public DummyProxyWithGrantParameterizedGatekeeper(
        Provider<DummyPresenterWithGrantParameterizedGatekeeper> presenter) {
        this.presenter = new StandardProvider<DummyPresenterWithGrantParameterizedGatekeeper>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceWithGrantParameterizedGatekeeper
  extends ProxyPlaceBase<DummyPresenterWithGrantParameterizedGatekeeper> {
    @Inject
    public DummyProxyPlaceWithGrantParameterizedGatekeeper(
        DummyProxyWithGrantParameterizedGatekeeper proxy,
        DeferredCommandManager deferredCommandManager,
        ParameterizedGatekeeper pGatekeeper) {
        super(new PlaceWithParameterizedGatekeeper("dummyNameTokenWithGrantParameterizedGatekeeper",
            pGatekeeper, new String[] {"ROLE_USER"}), proxy, deferredCommandManager);
    }
  }

  @TestMockSingleton
  abstract static class DummyPresenterDefault extends Presenter<View,DummyProxyPlaceDefault> {
    @Inject
    public DummyPresenterDefault(EventBus eventBus, View view, DummyProxyPlaceDefault proxy) {
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
    public DummyProxyDefault(Provider<DummyPresenterDefault> presenter) {
        this.presenter = new StandardProvider<DummyPresenterDefault>(presenter);
    };
  }

  @TestEagerSingleton
  static class DummyProxyPlaceDefault extends ProxyPlaceBase<DummyPresenterDefault> {
    @Inject
    public DummyProxyPlaceDefault(DummyProxyDefault proxy,
        DeferredCommandManager deferredCommandManager) {
        super(new PlaceImpl("defaultPlace"), proxy, deferredCommandManager);
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
    };
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

  static class HasRoleGatekeeper implements ParameterizedGatekeeper {
    private static final String[] CURRENT_USER_ROLES = new String [] {"ROLE_USER"};
    private String role;

    public boolean canReveal() {
      return Arrays.asList(CURRENT_USER_ROLES).contains(this.role);
    }

    public ParameterizedGatekeeper withParams(String[] params) {
      if (params.length != 1) {
        throw new IllegalArgumentException("Just one parameter is expected");
      }
      this.role = params[0];
      return this;
    }
  }

  // SUT
  @Inject PlaceManager placeManager;

  @Inject DeferredCommandManager deferredCommandManager;
  @Inject PlaceManagerWindowMethodsTestUtil gwtWindowMethods;
  @Inject NavigationEventSpy navigationHandler;
  @Inject EventBus eventBus;

  @Test
  public void placeManagerRevealPlaceStandard(
      DummyPresenterBasic presenter) {

    // Given
    eventBus.addHandler(NavigationEvent.getType(), navigationHandler);

    // When
    placeManager.revealPlace(new PlaceRequest("dummyNameTokenBasic").with("dummyParam",
        "dummyValue"));
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

    verify(gwtWindowMethods).setBrowserHistoryToken(any(String.class), eq(false));

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
    PlaceRequest placeRequest = new PlaceRequest(DummyPresenterRedirectNoHistory.TOKEN);

    // When
    placeManager.revealPlace(placeRequest);
    deferredCommandManager.pump();

    // Then
    // assert called only once
    verify(gwtWindowMethods, times(1)).setBrowserHistoryToken(any(String.class), eq(false));

    PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
    assertEquals("dummyNameTokenBasic", finalPlaceRequest.getNameToken());
  }

  @Test
  public void placeManagerRevealPlaceRedirectInPrepareFromRequest(
      DummyPresenterRedirect presenter,
      DummyPresenterBasic otherPresenter) {
    // Given
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenRedirect").with("dummyParam",
        "dummyValue");

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
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenRedirect").with("dummyParam", "dummyValue");

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
  public void placeManagerRevealDefaultPlaceWhenGatekeeperCanNotReveal(
      DummyPresenterWithDenyGatekeeper presenterWithGatekeeper,
      DummyPresenterDefault defaultPresenter) {
    // Given
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenWithDenyGatekeeper");

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
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenWithGrantGatekeeper");

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

  @Test
  public void placeManagerRevealDefaultPlaceWhenParameterizedGatekeeperCanNotReveal(
      DummyPresenterWithDenyParameterizedGatekeeper presenterWithParameterizedGatekeeper,
      DummyPresenterDefault defaultPresenter) {
    // Given
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenWithDenyParameterizedGatekeeper");

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
  public void placeManagerRevealRequestPlaceWhenParameterizedGatekeeperCanReveal(
      DummyPresenterWithGrantParameterizedGatekeeper presenterWithParameterizedGatekeeper) {
    // Given
    PlaceRequest placeRequest = new PlaceRequest("dummyNameTokenWithGrantParameterizedGatekeeper");

    // When
    placeManager.revealPlace(placeRequest);
    deferredCommandManager.pump();

    // Then
    List<PlaceRequest> placeHierarchy = placeManager.getCurrentPlaceHierarchy();
    assertEquals(1, placeHierarchy.size());

    PlaceRequest finalPlaceRequest = placeManager.getCurrentPlaceRequest();
    assertEquals(placeHierarchy.get(0), finalPlaceRequest);

    assertEquals("dummyNameTokenWithGrantParameterizedGatekeeper", finalPlaceRequest.getNameToken());
    assertEquals(0, finalPlaceRequest.getParameterNames().size());

    verify(presenterWithParameterizedGatekeeper).prepareFromRequest(finalPlaceRequest);
    verify(presenterWithParameterizedGatekeeper).forceReveal();
  }
}
