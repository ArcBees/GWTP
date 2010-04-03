package com.philbeaudoin.gwtp.mvp.client;

/**
 * Copyright 2010 Philippe Beaudoin
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


import com.google.gwt.user.client.ui.Widget;

/**
 * A simple implementation of view that simply disregards every call to
 * {@link #setContent(Object, Widget)}, {@link #addContent(Object, Widget)},
 * and {@link #clearContent(Object)}.
 * 
 * @author Philippe Beaudoin
 */
public abstract class ViewImpl implements View {

  @Override
  public void addContent(Object slot, Widget content) {
  }
  @Override
  public void clearContent(Object slot) {
  }
  @Override
  public void setContent(Object slot, Widget content) {
  }

}
