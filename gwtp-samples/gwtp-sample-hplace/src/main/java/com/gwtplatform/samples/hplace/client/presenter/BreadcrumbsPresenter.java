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

package com.gwtplatform.samples.hplace.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.proxy.SetPlaceTitleHandler;

/**
 * This is the top-level presenter of the hierarchy. Other presenters reveal
 * themselves within this presenter. This presenter display a breadcrumbs with
 * the titles of the previously visited pages.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class BreadcrumbsPresenter extends
    Presenter<BreadcrumbsPresenter.MyView, BreadcrumbsPresenter.MyProxy> {
  /**
   * {@link BreadcrumbsPresenter}'s proxy.
   */
  @ProxyStandard
  public interface MyProxy extends Proxy<BreadcrumbsPresenter> {
  }

  /**
   * {@link BreadcrumbsPresenter}'s view.
   */
  public interface MyView extends View {
    void clearBreadcrumbs(int breadcrumbSize);
    void setBreadcrumbs(int index, String title);
  }

  /**
   * Use this in leaf presenters, inside their {@link #revealInParent} method.
   */
  @ContentSlot
  public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

  private final PlaceManager placeManager;

  @Inject
  public BreadcrumbsPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final PlaceManager placeManager) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
  }

  @Override
  protected void onReset() {
    super.onReset();
    int size = placeManager.getHierarchyDepth();
    getView().clearBreadcrumbs(size);
    for (int i = 0; i < size; ++i) {
      final int index = i;
      placeManager.getTitle(i, new SetPlaceTitleHandler() {
        @Override
        public void onSetPlaceTitle(String title) {
          getView().setBreadcrumbs(index, title);
        }
      });
    }
  }

  @Override
  protected void revealInParent() {
    RevealRootContentEvent.fire(this, this);
  }
}
