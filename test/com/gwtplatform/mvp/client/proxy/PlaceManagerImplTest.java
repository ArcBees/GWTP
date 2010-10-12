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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.mvp.client.DefaultEventBus;
import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.StandardProvider;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.test.DeferredCommandManager;
import com.gwtplatform.test.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.test.mockito.TestModule;
import com.gwtplatform.test.mockito.TestScope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
/**
 * Unit tests for {@link PlaceManagerImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PlaceManagerImplTest {

  // Guice environment
  /**
   * @author Philippe Beaudoin
   */
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      GWTMockUtilities.disarm();
      bind(DeferredCommandManager.class).in(TestScope.SINGLETON);
      bind(EventBus.class).to(DefaultEventBus.class).in(TestScope.SINGLETON);
      bindMock(TokenFormatter.class).in(TestScope.SINGLETON);
      bindMock(ProxyFailureHandler.class).in(TestScope.SINGLETON);
      bindMock(GwtWindowMethods.class).in(TestScope.SINGLETON);
      bind(PlaceManager.class).to(MyPlaceManager.class).in(TestScope.SINGLETON);
      bind(DummyProxyBasic.class).in(TestScope.EAGER_SINGLETON);
      bind(DummyProxyPlaceBasic.class).in(TestScope.EAGER_SINGLETON);
      bindMock(DummyPresenterBasic.class).in(TestScope.SINGLETON);
      bind(DummyProxyRedirect.class).in(TestScope.EAGER_SINGLETON);
      bind(DummyProxyPlaceRedirect.class).in(TestScope.EAGER_SINGLETON);
      bind(DummyPresenterRedirect.class).in(TestScope.SINGLETON);
    }
  }
  
  /**
   * GWT window and history related methods that will be mocked.
   */
  public interface GwtWindowMethods {
    void registerTowardsHistory();     
    String getBrowserHistoryToken();
    String getCurrentHref();
    void revealCurrentPlace();
    void setBrowserHistoryToken(String historyToken, boolean issueEvent);
  }
  
  /**
   * This place manager overrides all the methods that use
   * GWT-dependent classes.
   * 
   * @author Philippe Beaudoin
   */
  static class MyPlaceManager extends PlaceManagerImpl {

    private final PlaceRequest defaultPlaceRequest = new PlaceRequest("defaultPlace");
    private final GwtWindowMethods gwtWindowMethods;

    @Inject
    public MyPlaceManager(EventBus eventBus, TokenFormatter tokenFormatter, 
        GwtWindowMethods gwtWindowMethods) {
      super(eventBus, tokenFormatter);
      this.gwtWindowMethods = gwtWindowMethods;
    }

    @Override
    public void revealDefaultPlace() {
      revealPlace(defaultPlaceRequest);
    }    
    
    @Override 
    void registerTowardsHistory() {      
      if (gwtWindowMethods != null) {
        gwtWindowMethods.registerTowardsHistory();
      }
    }
    
    @Override
    String getBrowserHistoryToken() {
      return gwtWindowMethods.getBrowserHistoryToken();
    }
    
    @Override
    String getCurrentHref() {
      return gwtWindowMethods.getCurrentHref();
    }

    @Override
    public void revealCurrentPlace() {
      gwtWindowMethods.revealCurrentPlace();
    }
    
    @Override
    void setBrowserHistoryToken(String historyToken, boolean issueEvent) {
      gwtWindowMethods.setBrowserHistoryToken(historyToken, issueEvent);
    }
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
  
  abstract static class DummyPresenterBasic extends Presenter<View,DummyProxyPlaceBasic> {
    @Inject
    public DummyPresenterBasic(EventBus eventBus, View view, DummyProxyPlaceBasic proxy) {
      super(eventBus, view, proxy);
    }
    
    public final boolean isVisible() {
      return super.isVisible();
    }
  }
  
  static class DummyProxyBasic extends ProxyImpl<DummyPresenterBasic> {
    @Inject
    public DummyProxyBasic(Provider<DummyPresenterBasic> presenter) {
        this.presenter = new StandardProvider<DummyPresenterBasic>(presenter);        
    };
  }
    
  static class DummyProxyPlaceBasic extends ProxyPlaceBase<DummyPresenterBasic> {
    @Inject
    public DummyProxyPlaceBasic(DummyProxyBasic proxy, 
        DeferredCommandManager deferredCommandManager) {
        super(new PlaceImpl("dummyNameTokenBasic"), proxy, deferredCommandManager);
    }
  }

  // This presenter automatically redirects in prepareFromRequest to PresenterBasic
  static class DummyPresenterRedirect extends Presenter<View,DummyProxyPlaceBasic> {
    private final PlaceManager placeManager;

    public PlaceRequest preparedRequest;
    public int prepareFromRequestCalls;
    public int revealInParentCalls;

    @Inject
    public DummyPresenterRedirect(EventBus eventBus, DummyProxyPlaceBasic proxy, PlaceManager placeManager) {
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
  
  static class DummyProxyRedirect extends ProxyImpl<DummyPresenterRedirect> {
    @Inject
    public DummyProxyRedirect(Provider<DummyPresenterRedirect> presenter) {
        this.presenter = new StandardProvider<DummyPresenterRedirect>(presenter);        
    };
  }
    
  static class DummyProxyPlaceRedirect extends ProxyPlaceBase<DummyPresenterRedirect> {
    @Inject
    public DummyProxyPlaceRedirect(DummyProxyRedirect proxy, 
        DeferredCommandManager deferredCommandManager) {
        super(new PlaceImpl("dummyNameTokenRedirect"), proxy, deferredCommandManager);
    }
  }
  
  static class NavigationEventSpy implements NavigationHandler {
    int navCount;
    NavigationEvent lastEvent;
    @Override
    public void onNavigation(NavigationEvent navigationEvent) {
      navCount++;
      lastEvent = navigationEvent;
    };
  }
  
  // Providers to use Guice injection
  @Inject
  Provider<DeferredCommandManager> deferredCommandManagerProvider;
  @Inject
  Provider<PlaceManager> placeManagerProvider;
  @Inject
  Provider<DummyPresenterBasic> presenterBasicProvider;
  @Inject
  Provider<DummyProxyPlaceBasic> proxyPlaceBasicProvider;
  @Inject
  Provider<DummyPresenterRedirect> presenterRedirectProvider;
  @Inject
  Provider<DummyProxyPlaceRedirect> proxyPlaceRedirectProvider;
  @Inject
  Provider<GwtWindowMethods> gwtWindowMethodsProvider;
  @Inject
  Provider<EventBus> eventBusProvider;
  
  @Test
  public void placeManagerRevealPlaceStandard() {
    // Given
    DeferredCommandManager deferredCommandManager = deferredCommandManagerProvider.get();
    PlaceManager placeManager = placeManagerProvider.get();
    DummyPresenterBasic presenter = presenterBasicProvider.get();
    GwtWindowMethods gwtWindowMethods = gwtWindowMethodsProvider.get();
    NavigationEventSpy navigationHandler = new NavigationEventSpy();
    eventBusProvider.get().addHandler(NavigationEvent.getType(), navigationHandler);
    
    
    // When
    placeManager.revealPlace(new PlaceRequest("dummyNameTokenBasic").with("dummyParam", "dummyValue"));
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

  @Test
  public void placeManagerRevealPlaceRedirectInPrepareFromRequest() {
    // Given
    DeferredCommandManager deferredCommandManager = deferredCommandManagerProvider.get();
    PlaceManager placeManager = placeManagerProvider.get();
    DummyPresenterRedirect presenter = presenterRedirectProvider.get();
    DummyPresenterBasic otherPresenter = presenterBasicProvider.get();
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
  public void placeManagerUserUpdateHistoryWhenRevealPlace() {
    // Given
    DeferredCommandManager deferredCommandManager = deferredCommandManagerProvider.get();
    PlaceManager placeManager = placeManagerProvider.get();
    DummyPresenterRedirect presenter = presenterRedirectProvider.get();
    DummyPresenterBasic otherPresenter = presenterBasicProvider.get();
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
}
