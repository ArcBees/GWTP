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

import java.util.List;

import com.google.gwt.event.shared.EventBus;
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
import com.gwtplatform.samples.hplace.client.NameTokens;
import com.gwtplatform.samples.hplace.shared.GetProductListAction;
import com.gwtplatform.samples.hplace.shared.GetProductListResult;
import com.gwtplatform.samples.hplace.shared.Product;

/**
 * @author Philippe Beaudoin
 */
public class ProductListPresenter extends
    Presenter<ProductListPresenter.MyView, ProductListPresenter.MyProxy> {
  /**
   * {@link ProductListPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.productList)
  public interface MyProxy extends ProxyPlace<ProductListPresenter> {
  }

  /**
   * {@link ProductListPresenter}'s view.
   */
  public interface MyView extends View {
    void setBackLinkHistoryToken(String historyToken);
    void setList(List<Product> products);
    void setMessage(String string);
    void setTitle(String title);
  }

  public static final String TOKEN_TYPE = "type";
  public static final String TYPE_ALL_PRODUCTS = "all";

  public static final String TYPE_FAVORITE_PRODUCTS = "fav";

  public static final String TYPE_SPECIALS = "spec";

  @TitleFunction
  public static String getListTitle(PlaceRequest request) {
    return getTitleFor(request.getParameter(TOKEN_TYPE, null));
  }

  private static String getTitleFor(String type) {
    if (type.equals(TYPE_FAVORITE_PRODUCTS)) {
      return "Favorite products";
    } else if (type.equals(TYPE_SPECIALS)) {
      return "Specials";
    } else {
      return "All products";
    }
  }

  private String currentType = TYPE_ALL_PRODUCTS;

  private final DispatchAsync dispatcher;

  private final PlaceManager placeManager;

  @Inject
  public ProductListPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final PlaceManager placeManager,
      final DispatchAsync dispatcher) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.dispatcher = dispatcher;
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    super.prepareFromRequest(request);
    String type = request.getParameter(TOKEN_TYPE, TYPE_ALL_PRODUCTS);
    if (type.equals(TYPE_FAVORITE_PRODUCTS)) {
      currentType = TYPE_FAVORITE_PRODUCTS;
    } else if (type.equals(TYPE_SPECIALS)) {
      currentType = TYPE_SPECIALS;
    } else {
      currentType = TYPE_ALL_PRODUCTS;
    }

    setViewTitle();
  }

  @Override
  protected void onReset() {
    super.onReset();
    getView().setMessage("Loading list...");
    getView().setBackLinkHistoryToken(
        placeManager.buildRelativeHistoryToken(-1));
    dispatcher.execute(new GetProductListAction(getFlags()),
        new AsyncCallback<GetProductListResult>() {
          @Override
          public void onFailure(Throwable caught) {
            getView().setMessage("Loading error!");
          }

          @Override
          public void onSuccess(GetProductListResult result) {
            getView().setList(result.getProducts());
          }
        });
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, BreadcrumbsPresenter.TYPE_SetMainContent,
        this);
  }

  private int getFlags() {
    if (currentType.equals(TYPE_FAVORITE_PRODUCTS)) {
      return Product.FLAG_FAVORITE;
    } else if (currentType.equals(TYPE_SPECIALS)) {
      return Product.FLAG_SPECIAL;
    }
    return 0;
  }

  private void setViewTitle() {
    getView().setTitle(getTitleFor(currentType));
  }

}
