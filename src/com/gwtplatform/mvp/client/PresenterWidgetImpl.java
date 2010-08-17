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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Widget;

import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <V> The {@link View} type.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
@SuppressWarnings("deprecation")
// TODO: Remove after making members private
public abstract class PresenterWidgetImpl<V extends View> extends
    HandlerContainerImpl implements PresenterWidget<V> {
  /**
   * The {@link EventBus} for the application.
   * 
   * Deprecated to use directly, use {@link #getEventBus()} instead.
   */
  @Deprecated
  protected final EventBus eventBus; // TODO: Make private.

  /**
   * The view for the presenter.
   * 
   * Deprecated to use directly, use {@link #getView()} instead.
   */
  @Deprecated
  protected final V view;

  /**
   * The parent presenter, in order to make sure this widget is only ever in one
   * parent.
   */
  PresenterWidgetInternal<?> currentParentPresenter = null;

  boolean visible = false;

  /**
   * This map makes it possible to keep a list of all the active children in
   * every slot managed by this PresenterWidget. A slot is identified by an
   * opaque object. A single slot can have many children.
   */
  private final Map<Object, List<PresenterWidget<?>>> activeChildren = new HashMap<Object, List<PresenterWidget<?>>>();

  private final List<PopupPresenter<?>> popupChildren = new ArrayList<PopupPresenter<?>>();

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
    super(autoBind);
    this.view = view;
    this.eventBus = eventBus;
  }

  /**
   * Creates a {@link PresenterWidgetImpl}.
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   */
  public PresenterWidgetImpl(EventBus eventBus, V view) {
    super();
    this.view = view;
    this.eventBus = eventBus;
  }

  @Override
  public void addContent(Object slot, PresenterWidget<?> content) {
    if (content == null) {
      return;
    }

    content.reparent(this);

    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
    if (slotChildren != null) {
      slotChildren.add(content);
    } else {
      slotChildren = new ArrayList<PresenterWidget<?>>(1);
      slotChildren.add(content);
      activeChildren.put(slot, slotChildren);
    }
    getView().addContent(slot, content.getWidget());
    if (isVisible()) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.notifyReveal();
    }
  }

  @Override
  public final <H extends EventHandler> void addRegisteredHandler(
      Type<H> type, H handler) {
    registerHandler(getEventBus().addHandler(type, handler));
  }

  @Override
  public void clearContent(Object slot) {
    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
    if (slotChildren != null) {
      // This presenter is visible, its time to call onHide
      // on the children to be removed (and recursively on their children)
      if (isVisible()) {
        for (PresenterWidget<?> activeChild : slotChildren) {
          activeChild.notifyHide();
        }
      }
      slotChildren.clear();
    }
    getView().setContent(slot, null);
  }

  @Override
  public void detach(PresenterWidget<?> childPresenter) {
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      slotChildren.remove(childPresenter);
    }
    popupChildren.remove(childPresenter);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    getEventBus().fireEvent(event);
  }

  @Override
  public final EventBus getEventBus() {
    return eventBus;
  }

  // TODO This should be final. Can't be now because it makes testing injected
  // PresenterWidgets impossible. Make final once
  // http://code.google.com/p/gwt-platform/issues/detail?id=111 is solved.
  @Override
  public V getView() {
    return view;
  }

  @Override
  public Widget getWidget() {
    return getView().asWidget();
  }

  @Override
  public final boolean isVisible() {
    return visible;
  }

  @Override
  public void monitorCloseEvent(final PopupPresenter<?> popupPresenter) {
    PopupView popupView = popupPresenter.getView();

    popupView.setCloseHandler(new PopupViewCloseHandler() {
      @Override
      public void onClose() {
        if (isVisible()) {
          popupPresenter.notifyHide();
        }
        removePopupChildren(popupPresenter);
      }
    });
  }

  @Override
  public void notifyHide() {
    assert isVisible() : "notifyHide() called on a hidden presenter!";
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.notifyHide();
      }
    }
    for (PopupPresenter<?> popupPresenter : popupChildren) {
      popupPresenter.getView().setCloseHandler(null);
      popupPresenter.notifyHide();
      popupPresenter.getView().hide();
    }
    visible = false;
    onHide();
  }

  /**
   * Called right after the widget has been revealed on screen. You should not
   * call this. Fire a {@link ResetPresentersEvent} instead.
   */
  @Override
  public void notifyReveal() {
    assert !isVisible() : "notifyReveal() called on a visible presenter!";
    onReveal();
    visible = true;
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.notifyReveal();
      }
    }
    for (PopupPresenter<?> popupPresenter : popupChildren) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      popupPresenter.getView().show();
      monitorCloseEvent(popupPresenter);
      popupPresenter.notifyReveal();
    }
  }

  @Override
  public void removeContent(Object slot, PresenterWidget<?> content) {
    if (content == null) {
      return;
    }

    content.reparent(null);

    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
    if (slotChildren != null) {
      // This presenter is visible, its time to call onHide
      // on the child to be removed (and recursively on itschildren)
      if (isVisible()) {
        content.notifyHide();
      }
      slotChildren.remove(content);
    }
    getView().removeContent(slot, content.getWidget());
  }

  @Override
  public void reparent(PresenterWidget<?> newParent) {
    if (currentParentPresenter != null && currentParentPresenter != newParent) {
      currentParentPresenter.detach(this);
    }
    currentParentPresenter = newParent;
  }

  @Override
  public final void reset() {
    onReset();
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.reset();
      }
    }
    for (PopupPresenter<?> popupPresenter : popupChildren) {
      popupPresenter.reset();
    }
  }

  @Override
  public void setContent(Object slot, PresenterWidget<?> content) {
    setContent(slot, content, true);
  }

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
  protected void addPopupContent(final PopupPresenter<?> content) {
    addPopupContent(content, true);
  }

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
  protected void addPopupContent(final PopupPresenter<?> content, boolean center) {
    if (content == null) {
      return;
    }

    content.reparent(this);

    // Do nothing if the content is already added
    for (PopupPresenter<?> popupPresenter : popupChildren) {
      if (popupPresenter == content) {
        return;
      }
    }

    final PopupView popupView = content.getView();
    popupChildren.add(content);

    // Center if desired
    if (center) {
      popupView.center();
    }

    // Display the popup content
    if (isVisible()) {
      popupView.show();
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      monitorCloseEvent(content);
      content.notifyReveal();
    }
  }

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onHide()} if
   * you override.
   * <p />
   * Override this method to perform any clean-up operations. For example,
   * objects created directly or indirectly during the call to
   * {@link #onReveal()} should be disposed of in this methods.
   */
  protected void onHide() {
  }

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReset()} if
   * you override.
   * <p />
   * This method is called whenever a new presenter is requested, even if the
   * presenter was already visible. It is called on every visible presenter,
   * starting from the top-level presenter and going to the leaves.
   */
  protected void onReset() {
  }
  
  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReveal()} if
   * you override.
   * <p />
   * This method will be called whenever the presenter is revealed. Override it
   * to perform any action (such as refreshing content) that needs to be done
   * when the presenter is revealed.
   */
  protected void onReveal() {
  }

  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * The attached {@link View} should manage this slot when its
   * {@link View#setContent(Object, Widget)} is called. It should also clear the
   * slot when the {@code setContent} method is called with {@code null} as a
   * parameter.
   * 
   * @param slot An opaque object identifying which slot this content is being
   *          set into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   * @param performReset Pass {@code true} if you want a
   *          {@link ResetPresentersEvent} to be fired after the content has
   *          been added and this presenter is visible, pass {@code false}
   *          otherwise.
   */
  protected void setContent(Object slot, PresenterWidget<?> content,
      boolean performReset) {
    if (content == null) {
      // Assumes the user wants to clear the slot content.
      clearContent(slot);
      return;
    }

    content.reparent(this);

    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);

    if (slotChildren != null) {
      if (slotChildren.size() == 1 && slotChildren.get(0) == content) {
        // The slot contains the right content, nothing to do
        getView().setContent(slot, content.getWidget());
        return;
      }

      if (isVisible()) {
        // We are visible, make sure the content that we're removing
        // is being notified as hidden
        for (PresenterWidget<?> activeChild : slotChildren) {
          activeChild.notifyHide();
        }
      }
      slotChildren.clear();
      slotChildren.add(content);
    } else {
      slotChildren = new ArrayList<PresenterWidget<?>>(1);
      slotChildren.add(content);
      activeChildren.put(slot, slotChildren);
    }

    // Set the content in the view
    getView().setContent(slot, content.getWidget());
    if (isVisible()) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.notifyReveal();
      if (performReset) {
        // And to reset everything if needed
        ResetPresentersEvent.fire(this);
      }
    }
  }

  /**
   * Go through the popup children and remove the specified one.
   * 
   * @param content The {@link PresenterWidget} added as a popup which we now
   *          remove.
   */
  private void removePopupChildren(PopupPresenter<?> content) {
    int i;
    for (i = 0; i < popupChildren.size(); ++i) {
      PopupPresenter<?> popupPresenter = popupChildren.get(i);
      if (popupPresenter == content) {
        (popupPresenter.getView()).setCloseHandler(null);
        break;
      }
    }
    if (i < popupChildren.size()) {
      popupChildren.remove(i);
    }
  }
}