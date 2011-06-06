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

package com.gwtplatform.mvp.client;

/**
 * Base class for a {@link View} that implements the {@link HasUiHandlers}
 * interface. You should always call {@link #setUiHandlers(UiHandlers)} from your
 * presenter's constructor.
 * <p />
 * <b>Important!</b> Never call {@link #getUiHandlers()} inside your constructor
 * since the {@link UiHandlers} are not yet set.
 *
 * @param <C> Your {@link UiHandlers} interface type.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public abstract class ViewWithUiHandlers<C extends UiHandlers> extends ViewImpl
    implements HasUiHandlers<C> {
  private C uiHandlers;

  /**
   * Access the {@link UiHandlers} associated with this {@link View}.
   * <p>
   * <b>Important!</b> Never call {@link #getUiHandlers()} inside your constructor
   * since the {@link UiHandlers} are not yet set.
   *
   * @return The {@link UiHandlers}, or {@code null} if they are not yet set.
   */
  protected C getUiHandlers() {
    return uiHandlers;
  }

  @Override
  public void setUiHandlers(C uiHandlers) {
    this.uiHandlers = uiHandlers;
  }
}
