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

package com.gwtplatform.externaltest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

import com.gwtplatform.mvp.client.ViewImpl;

/**
 * This is the test view of the {@link MainPresenter}.
 * 
 * @author Philippe Beaudoin
 */
@Singleton
public class MainView extends ViewImpl implements MainPresenter.MyView {

  public final FlowPanel widget;
  
  public MainView() {
    widget = new FlowPanel();
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setContent(Object slot, Widget content) {
    if (slot == MainPresenter.TYPE_SetMainContent) {
      setMainContent(content);
    } else {
      super.setContent(slot, content);
    }
  }

  private void setMainContent(Widget content) {
    widget.clear();    
    if (content != null) {
      widget.add(content);
    }
  }
}