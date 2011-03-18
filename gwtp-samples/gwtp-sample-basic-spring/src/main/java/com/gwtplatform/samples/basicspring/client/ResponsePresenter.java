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
package com.gwtplatform.samples.basicspring.client;
import com.gwtplatform.dispatch.client.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.samples.basicspring.shared.SendTextToServer;
import com.gwtplatform.samples.basicspring.shared.SendTextToServerResult;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
/**
 * @author Philippe Beaudoin
 */
public class ResponsePresenter extends
    Presenter<ResponsePresenter.MyView, ResponsePresenter.MyProxy> {
  /**
   * {@link com.gwtplatform.samples.basicspring.client.ResponsePresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(nameToken)
  public interface MyProxy extends Proxy<ResponsePresenter>, Place {
  }
  /**
   * {@link com.gwtplatform.samples.basicspring.client.ResponsePresenter}'s view.
   */
  public interface MyView extends View {
    Button getCloseButton();
    void setServerResponse(String serverResponse);
    void setTextToServer(String textToServer);
  }
  public static final String nameToken = "response";
  public static final String textToServerParam = "textToServer";
  private final DispatchAsync dispatcher;
  private final PlaceManager placeManager;
  private String textToServer;
  @Inject
  public ResponsePresenter(EventBus eventBus, MyView view, MyProxy proxy,
      PlaceManager placeManager, DispatchAsync dispatcher) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.dispatcher = dispatcher;
  }
  @Override
  public void prepareFromRequest(PlaceRequest request) {
    super.prepareFromRequest(request);
    textToServer = request.getParameter(textToServerParam, null);
  }
  @Override
  protected void onBind() {
    super.onBind();
    registerHandler(getView().getCloseButton().addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            placeManager.revealPlace(new PlaceRequest(
                MainPagePresenter.nameToken));
          }
        }));
  }
  @Override
  protected void onReset() {
    super.onReset();
    getView().setTextToServer(textToServer);
    getView().setServerResponse("Waiting for response...");
    dispatcher.execute(new SendTextToServer(textToServer),
        new AsyncCallback<SendTextToServerResult>() {
          @Override
          public void onFailure(Throwable caught) {
            getView().setServerResponse(
                "An error occured: " + caught.getMessage());
          }
          @Override
          public void onSuccess(SendTextToServerResult result) {
            getView().setServerResponse(result.getResponse());
          }
        });
  }
  @Override
  protected void revealInParent() {
    RevealRootContentEvent.fire(this, this);
  }
}