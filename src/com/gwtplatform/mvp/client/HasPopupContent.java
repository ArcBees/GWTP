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
 * @author Christian Goudreau
 */
public interface HasPopupContent {

  /**
   * This method sets some popup content within the {@link Presenter} and
   * centers it. The view associated with the {@code content}'s presenter must
   * inherit from {@link PopupView}. The popup will be visible and the
   * corresponding presenter will receive the lifecycle events as needed.
   * <p />
   * Contrary to the {@link setContent()} method, no
   * {@link ResetPresentersEvent} is fired.
   * 
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   * 
   * @see #addPopupContent(PresenterWidget)
   */
  void addPopupContent(final PresenterWidget<? extends PopupView> content);
  
  /**
   * This method sets some popup content within the {@link Presenter}. The view
   * associated with the {@code content}'s presenter must inherit from
   * {@link PopupView}. The popup will be visible and the corresponding
   * presenter will receive the lifecycle events as needed.
   * <p />
   * Contrary to the {@link setContent()} method, no
   * {@link ResetPresentersEvent} is fired.
   * 
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   * @param center Pass {@code true} to center the popup, otherwise its position
   *          will not be adjusted.
   * 
   * @see #addPopupContent(PresenterWidget)
   */
  void addPopupContent(final PresenterWidget<? extends PopupView> content, boolean center);
}
