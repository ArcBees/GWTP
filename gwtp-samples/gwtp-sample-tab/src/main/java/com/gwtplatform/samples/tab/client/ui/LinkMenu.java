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

package com.gwtplatform.samples.tab.client.ui;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * A simple menu that can be reused.
 * <p />
 * Instead of using a static field and {@code GWT.create(Binder.class)} to
 * instantiate the binder we rely on GIN dependency injection. This
 * would facilitate testing if we ever wanted to test this class.
 * Using this widget in a UiBinder file is made possible by our use or
 * {@code GinUiBinder.gwt.xml} in the module file, {@code Gwtptabsample.gwt.xml}.
 * We also need a method to return {@link LinkMenu} in 
 * {@link com.gwtplatform.samples.tab.client.gin.ClientGinjector ClientGinjector}.
 * Finally, it is good practice to bind the {@link LinkMenu.Binder} interface
 * as a singleton, which is done in {@link UiModule}.
 * 
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public class LinkMenu extends Composite {
  /**
   * Our custom binder.
   */
  public interface Binder extends UiBinder<Widget, LinkMenu> { }

  @Inject
  public LinkMenu(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }
}