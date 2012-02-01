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

import javax.inject.Named;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

/**
 * A dummy widget participating in dependency injection to test GinUiBinder.
 *
 * @author Philippe Beaudoin
 */
public class InjectedLabel extends Composite {

  private final Label label;

  @Inject
  public InjectedLabel(@Named("notice") String notice) {
    label = new Label(notice);
    initWidget(label);
  }
}
