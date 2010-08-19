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

package com.gwtplatform.mvp.client;

/**
 * This interface is meant to be used by internal classes only.
 * 
 * @author Christian Goudreau
 */
interface PresenterWidgetInternal {
  /**
   * Called right after the widget has been made revealed on screen. You should
   * not call this, fire a {@link ResetPresentersEvent} instead.
   */
  // void notifyHide();

  /**
   * Called by a child {@link PresenterWidget} when it wants to detach itself
   * from this parent.
   * 
   * @param childPresenter The {@link PresenterWidgetImpl} that is a child of
   *          this presenter.
   */
  void detach(PresenterWidget<?> childPresenter);

  /**
   * This methods attaches this presenter to its parent.
   * 
   * @param newParent The {@link PresenterWidgetImpl} that will be this
   *          presenter's new parent, or {@code null} to detach from all
   *          parents.
   */
  void reparent(PresenterWidget<?> newParent);
}
