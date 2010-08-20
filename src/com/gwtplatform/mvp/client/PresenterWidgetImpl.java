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
 * @param <V> The {@link View} type.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
@Deprecated
public abstract class PresenterWidgetImpl<V extends View> extends PresenterWidget<V> {
  /**
   * Creates a {@link PresenterWidgetImpl} that is not necessarily using
   * automatic binding (see {@link HandlerContainerImpl(boolean)}).
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   * @param autoBind {@code true} to request automatic binding, {@code false}
   *          otherwise.
   */
  public PresenterWidgetImpl(boolean autoBind, EventBus eventBus, V view) {
    super(autoBind, eventBus, view);
  }

  /**
   * Creates a {@link PresenterWidgetImpl}.
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   */
  public PresenterWidgetImpl(EventBus eventBus, V view) {
    super(eventBus, view);
  }
  
  @Deprecated
  public void addContent(Object slot, PresenterWidget<?> content) {
    super.addInSlot(slot, content);
  }
  
  @Deprecated
  public void clearContent(Object slot) {
    super.clearSlot(slot);
  }

  @Deprecated
  public void removeContent(Object slot, PresenterWidget<?> content) {
    super.removeFromSlot(slot, content);
  }

  @Deprecated
  public void setContent(Object slot, PresenterWidget<?> content) {
    setContent(slot, content, true);
  }

  @Deprecated
  private void setContent(Object slot, PresenterWidget<?> content,
      boolean performReset) {
    super.setInSlot(slot, content);
  }
}