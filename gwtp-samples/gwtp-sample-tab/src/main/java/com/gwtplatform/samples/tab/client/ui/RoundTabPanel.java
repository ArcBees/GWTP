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

package com.gwtplatform.samples.tab.client.ui;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.samples.tab.client.TabDataExt;

/**
 * A {@link BaseTabPanel} styled to contain {@link RoundTab}.
 * <p />
 * Look at {@link LinkMenu} to see how we can use this widget within a
 * UiBinder file even though its constructor relies on dependency injection.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class RoundTabPanel extends BaseTabPanel {

  /**
   */
  public interface Binder extends UiBinder<Widget, RoundTabPanel> { }

  @Inject
  public RoundTabPanel(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }

  @Override
  protected BaseTab createNewTab(TabData tabData) {
    Gatekeeper gatekeeper = null;
    if (tabData instanceof TabDataExt) {
      TabDataExt tabDataExt = (TabDataExt) tabData;
      gatekeeper = tabDataExt.getGatekeeper();
    }

    // TODO Try using assisted injection here (to inject UiBinder in RoundTab)
    return new RoundTab(tabData, gatekeeper);
  }

}
