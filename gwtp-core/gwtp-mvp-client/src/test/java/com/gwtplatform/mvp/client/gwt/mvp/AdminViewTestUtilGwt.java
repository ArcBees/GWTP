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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * A test presenter meant to be run in a GWTTestCase.
 *
 * @author Philippe Beaudoin
 */
public class AdminViewTestUtilGwt extends ViewImpl implements AdminPresenterTestUtilGwt.MyView {

  public final FlowPanel widget;

  public AdminViewTestUtilGwt() {
    widget = new FlowPanel();
  }

  @Override
  public Widget asWidget() {
    return widget;
  }
}