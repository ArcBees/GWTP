/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * A simple implementation of {@link View} that simply disregards every call to
 * {@link #setContent(Object, Widget)}, {@link #addContent(Object, Widget)},
 * and {@link #clearContent(Object)}.
 * <p />
 * Feel free not to inherit from this if you need another base class (such as
 * {@link com.google.gwt.user.client.ui.Composite}), but you will have to
 * define the above methods.
 * 
 * @author Philippe Beaudoin
 */
public abstract class ViewImpl implements View {

  @Override
  public void setContent(Object slot, Widget content) {
  }
  @Override
  public void addContent(Object slot, Widget content) {
  }
  @Override
  public void removeContent(Object slot, Widget content) {
  }
}
