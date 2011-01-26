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

package com.gwtplatform.samples.tab.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.TabLabelFunction;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.gin.ClientGinjector;

/**
 * @author Christian Goudreau
 */
public class HomeNewsPresenter extends
    Presenter<HomeNewsPresenter.MyView, HomeNewsPresenter.MyProxy> {
  /**
   * {@link HomeNewsPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.homePage)
  @TabInfo(container = HomePresenter.class, priority = 0) // The left-most tab
                                                          // in the home tab
  public interface MyProxy extends TabContentProxyPlace<HomeNewsPresenter> {
  }

  @TabLabelFunction
  public static String getTabLabel(ClientGinjector ginjector) {
    return ginjector.getMyConstants().news();
  }

  /**
   * {@link HomeNewsPresenter}'s view.
   */
  public interface MyView extends View {
  }

  @Inject
  public HomeNewsPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, HomePresenter.TYPE_SetTabContent, this);
  }
}
