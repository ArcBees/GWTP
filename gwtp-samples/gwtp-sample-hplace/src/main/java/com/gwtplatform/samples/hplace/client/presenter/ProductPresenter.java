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

package com.gwtplatform.samples.hplace.client.presenter;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TitleFunction;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.SetPlaceTitleHandler;
import com.gwtplatform.samples.hplace.client.NameTokens;
import com.gwtplatform.samples.hplace.shared.GetProductAction;
import com.gwtplatform.samples.hplace.shared.GetProductResult;
import com.gwtplatform.samples.hplace.shared.Product;

/**
 * @author Christian Goudreau
 */
public class ProductPresenter extends
    Presenter<ProductPresenter.MyView, ProductPresenter.MyProxy> {
  /**
   * {@link ProductPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.product)
  public interface MyProxy extends ProxyPlace<ProductPresenter> {
  }

  /**
   * {@link ProductPresenter}'s view.
   */
  public interface MyView extends View {
    void setBackLinkHistoryToken(String historyToken);
    void setMessage(String string);
    void setProductDetails(Product product);
  }

  public static final String TOKEN_ID = "id";

  private final DispatchAsync dispatcher;
  private int id;

  private final PlaceManager placeManager;

  @Inject
  public ProductPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final PlaceManager placeManager,
      final DispatchAsync dispatcher) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.dispatcher = dispatcher;
  }

  @TitleFunction
  public void getListTitle(PlaceRequest request,
      final SetPlaceTitleHandler handler) {
    prepareFromRequest(request);
    dispatcher.execute(new GetProductAction(id),
        new AsyncCallback<GetProductResult>() {

          @Override
          public void onFailure(Throwable caught) {
            handler.onSetPlaceTitle("Unknown Product");
          }

          @Override
          public void onSuccess(GetProductResult result) {
            handler.onSetPlaceTitle(result.getProduct().getName());
          }
        });
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    super.prepareFromRequest(request);
    String idString = request.getParameter(TOKEN_ID, null);
    try {
      id = Integer.parseInt(idString);
    } catch (NumberFormatException e) {
      id = 0;
    }
  }

  @Override
  protected void onReset() {
    super.onReset();
    getView().setMessage("Loading...");
    getView().setBackLinkHistoryToken(
        placeManager.buildRelativeHistoryToken(-1));
    dispatcher.execute(new GetProductAction(id),
        new AsyncCallback<GetProductResult>() {

          @Override
          public void onFailure(Throwable caught) {
            getView().setMessage("Unknown product");
          }

          @Override
          public void onSuccess(GetProductResult result) {
            getView().setProductDetails(result.getProduct());
          }
        });
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, BreadcrumbsPresenter.TYPE_SetMainContent, this);
  }
}