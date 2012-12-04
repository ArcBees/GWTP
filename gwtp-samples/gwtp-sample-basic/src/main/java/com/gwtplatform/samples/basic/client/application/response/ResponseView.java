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

package com.gwtplatform.samples.basic.client.application.response;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * @author Philippe Beaudoin
 */
public class ResponseView extends ViewImpl implements ResponsePresenter.MyView {
  /**
   */
  public interface Binder extends UiBinder<Widget, ResponseView> {
  }

  @UiField
  HTML textToServer;
  @UiField
  HTML serverResponse;
  @UiField
  Button closeButton;

  /**
   */
  private Widget widget;

  @Inject
  public ResponseView(final Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public Button getCloseButton() {
    return closeButton;
  }

  @Override
  public void setServerResponse(String serverResponse) {
    this.serverResponse.setHTML(serverResponse);
  }

  @Override
  public void setTextToServer(String textToServer) {
    this.textToServer.setHTML(textToServer);
  }
}
