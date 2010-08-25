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
 * A presenter that does not have to be a singleton. Single pages from your
 * application will usually implement the singleton {@link Presenter} interface.
 * Use the {@link PresenterWidget} interface for complex widget that need to
 * interact with model objects, but that can be instantiated in many different
 * places in your application.
 * <p />
 * {@link PresenterWidget}s and {@link Presenter}s are organized in a hierarchy.
 * Parent presenters have links to their child presenters. To reveal a presenter
 * you should build a {@link PlaceRequest} and call one of the following method:
 * <ul>
 * <li>{@link PlaceManager#revealPlace(PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(PlaceRequest, int)}</li>
 * </ul>
 * The presenter's {@link Proxy} then asks the presenter to reveal itself by
 * calling {@link Presenter#forceReveal()}, triggering the following sequence of
 * operations:
 * <ul>
 * <li>The presenter that wants to reveal itself asks to be set in one of its
 * parent slot by firing a {@link RevealContentEvent} ;
 * <li>If a presenter already occupies this slot it is removed. If the parent is
 * currently visible, then {@link PresenterWidgetImpl#onHide()} is called on the
 * removed presenter and, recursively, on its children (bottom up: first the
 * child, then the parent) ;
 * <li>If the parent is not visible, it asks to be set in one of its parent slot
 * by firing a {@link RevealContentEvent} too, this continue recursively until
 * the top-level or until a visible presenter is reached ;
 * <li>When a visible presenter is reached, it calls
 * {@link PresenterWidgetImpl#onReveal()} on the presenter it just added to a
 * slot and, recursively, on that presenter's children (top down: first the
 * parent, then the children);</li>
 * <li>Finally {@link PresenterWidgetImpl#onReset()} is called on all the
 * currently visible presenters</li>
 * starting from the top-level presenter and going down to the leaf presenters
 * (top down: first the parent, then the children).</li>
 * </ul>
 * 
 * @param <V>
 *          The {@link View} type.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
// TODO: Remove after making members private
@SuppressWarnings("deprecation")
public abstract class PresenterWidget<V extends View> extends
    HandlerContainerImpl implements HasEventBus, HasSlots, HasPopupSlot {
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

  boolean visible;

  /**
   * This map makes it possible to keep a list of all the active children in
   * every slot managed by this PresenterWidget. A slot is identified by an
   * opaque object. A single slot can have many children.
   */
  private final Map<Object, List<PresenterWidget<?>>> activeChildren = new HashMap<Object, List<PresenterWidget<?>>>();

  /**
   * The parent presenter, in order to make sure this widget is only ever in one
   * parent.
   */
  private PresenterWidget<?> currentParentPresenter;

  private final List<PresenterWidget<? extends PopupView>> popupChildren = new ArrayList<PresenterWidget<? extends PopupView>>();

  /**
   * Creates a {@link PresenterWidget} that is not necessarily using automatic
   * binding (see {@link HandlerContainerImpl(boolean)}).
   * 
   * @param eventBus
   *          The {@link EventBus}.
   * @param view
   *          The {@link View}.
   * @param autoBind
   *          {@code true} to request automatic binding, {@code false}
   *          otherwise.
   */
  public PresenterWidget(boolean autoBind, EventBus eventBus, V view) {
    super(autoBind);

    this.eventBus = eventBus;
    this.view = view;
  }

  /**
   * Creates a {@link PresenterWidget} with automatic binding (see {@link
   * HandlerContainerImpl()}).
   * 
   * @param eventBus
   *          The {@link EventBus}.
   * @param view
   *          The {@link View}.
   */
  public PresenterWidget(EventBus eventBus, V view) {
    this(true, eventBus, view);
  }

  @Override
  public final void addToPopupSlot(
      final PresenterWidget<? extends PopupView> child) {
    addToPopupSlot(child, true);
  }

  @Override
  public final void addToPopupSlot(
      final PresenterWidget<? extends PopupView> child, boolean center) {
    if (child == null) {
      return;
    }

    child.reparent(this);

    // Do nothing if the content is already added
    for (PresenterWidget<?> popupPresenter : popupChildren) {
      if (popupPresenter == child) {
        return;
      }
    }

    final PopupView popupView = child.getView();
    popupChildren.add(child);

    // Center if desired
    if (center) {
      popupView.center();
    }

    // Display the popup content
    if (isVisible()) {
      popupView.show();
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      monitorCloseEvent(child);
      child.reveal();
    }
  }

  @Override
  public final void addToSlot(Object slot, PresenterWidget<?> content) {
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
    getView().addToSlot(slot, content.getWidget());
    if (isVisible()) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.reveal();
    }
  }

  @Override
  public final void clearSlot(Object slot) {
    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
    if (slotChildren != null) {
      // This presenter is visible, its time to call onHide
      // on the children to be removed (and recursively on their children)
      if (isVisible()) {
        for (PresenterWidget<?> activeChild : slotChildren) {
          activeChild.hide();
        }
      }
      slotChildren.clear();
    }
    getView().setInSlot(slot, null);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    getEventBus().fireEvent(this, event);
  }

  /**
   * Returns the {@link View} for the current presenter.
   * 
   * @return The view.
   */
  public V getView() {
    return view;
  }

  /**
   * Makes it possible to access the {@link Widget} object associated with that
   * presenter.
   * 
   * @return The Widget associated with that presenter.
   */
  public final Widget getWidget() {
    return view.asWidget();
  }

  /**
   * Verifies if the presenter is currently visible on the screen. A presenter
   * should be visible if it successfully revealed itself and was not hidden
   * later.
   * 
   * @return {@code true} if the presenter is visible, {@code false} otherwise.
   */
  public final boolean isVisible() {
    return visible;
  }

  @Override
  public final void removeFromSlot(Object slot, PresenterWidget<?> content) {
    if (content == null) {
      return;
    }

    content.reparent(null);

    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
    if (slotChildren != null) {
      // This presenter is visible, its time to call onHide
      // on the child to be removed (and recursively on itschildren)
      if (isVisible()) {
        content.hide();
      }
      slotChildren.remove(content);
    }
    getView().removeFromSlot(slot, content.getWidget());
  }

  // TODO This should be final but needs to be overriden in {@link
  // TabContainerPresenter}
  // We should be able to do this once we switch to an event-based mechanism for
  // highlighting tabs
  @Override
  public void setInSlot(Object slot, PresenterWidget<?> content) {
    setInSlot(slot, content, true);
  }

  @Override
  public final void setInSlot(Object slot, PresenterWidget<?> content,
      boolean performReset) {
    if (content == null) {
      // Assumes the user wants to clear the slot content.
      clearSlot(slot);
      return;
    }

    content.reparent(this);

    List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);

    if (slotChildren != null) {
      if (slotChildren.size() == 1 && slotChildren.get(0) == content) {
        // The slot contains the right content, nothing to do
        return;
      }

      if (isVisible()) {
        // We are visible, make sure the content that we're removing
        // is being notified as hidden
        for (PresenterWidget<?> activeChild : slotChildren) {
          activeChild.hide();
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
    getView().setInSlot(slot, content.getWidget());
    if (isVisible()) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.reveal();
      if (performReset) {
        // And to reset everything if needed
        ResetPresentersEvent.fire(this);
      }
    }
  }

  /**
   * Convenience method to register an event handler to the {@link EventBus}.
   * The handler will be automatically unregistered when
   * {@link HandlerContainer#unbind()} is called.
   * 
   * @param <H>
   *          The handler type
   * @param type
   *          See {@link Type}
   * @param handler
   *          The handler to register
   */
  protected final <H extends EventHandler> void addRegisteredHandler(
      Type<H> type, H handler) {
    registerHandler(getEventBus().addHandler(type, handler));
  }

  /**
   * Makes it possible to access the {@link EventBus} object associated with
   * that presenter.
   * 
   * @return The EventBus associated with that presenter.
   */
  protected final EventBus getEventBus() {
    return eventBus;
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
   * Call when you want to hide a presenter. You should fire a
   * {@link ResetPresentersEvent} instead.
   */
  void hide() {
    assert isVisible() : "Hide() called on a hidden presenter!";
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.hide();
      }
    }
    for (PresenterWidget<? extends PopupView> popupPresenter : popupChildren) {
      popupPresenter.getView().setCloseHandler(null);
      popupPresenter.hide();
      popupPresenter.getView().hide();
    }

    visible = false;
    onHide();
  }

  /**
   * Call when you need to reveal a presenter. You should fire a
   * {@link ResetPresentersEvent} instead.
   */
  void reveal() {
    assert !isVisible() : "notifyReveal() called on a visible presenter!";
    onReveal();
    visible = true;

    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.reveal();
      }
    }
    for (PresenterWidget<? extends PopupView> popupPresenter : popupChildren) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      popupPresenter.getView().show();
      monitorCloseEvent(popupPresenter);
      popupPresenter.reveal();
    }
  }

  void reparent(PresenterWidget<?> newParent) {
    if (currentParentPresenter != null && currentParentPresenter != newParent) {
      currentParentPresenter.detach(this);
    }

    currentParentPresenter = newParent;
  }

  /**
   * Call whenever the presenters need to be reset. You should fire a
   * {@link ResetPresentersEvent} instead.
   */
  void reset() {
    onReset();
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      for (PresenterWidget<?> activeChild : slotChildren) {
        activeChild.reset();
      }
    }
    for (PresenterWidget<?> popupPresenter : popupChildren) {
      popupPresenter.reset();
    }
  }

  /**
   * Called by a child {@link PresenterWidget} when it wants to detach itself
   * from this parent.
   * 
   * @param childPresenter
   *          The {@link PresenterWidgetImpl} that is a child of this presenter.
   */
  private void detach(PresenterWidget<?> childPresenter) {
    for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
      slotChildren.remove(childPresenter);
    }
    popupChildren.remove(childPresenter);
  }

  /**
   * Makes sure we monitor the specified popup presenter so that we know when it
   * is closing. This way we can make sure it doesn't receive future messages.
   * 
   * @param popupPresenter
   *          The {@link PresenterWidgetImpl} to monitor.
   */
  private void monitorCloseEvent(
      final PresenterWidget<? extends PopupView> popupPresenter) {
    PopupView popupView = popupPresenter.getView();

    popupView.setCloseHandler(new PopupViewCloseHandler() {
      @Override
      public void onClose() {
        if (isVisible()) {
          popupPresenter.hide();
        }
        removePopupChildren(popupPresenter);
      }
    });
  }

  /**
   * Go through the popup children and remove the specified one.
   * 
   * @param content
   *          The {@link PresenterWidget} added as a popup which we now remove.
   */
  private void removePopupChildren(PresenterWidget<? extends PopupView> content) {
    int i;
    for (i = 0; i < popupChildren.size(); ++i) {
      PresenterWidget<? extends PopupView> popupPresenter = popupChildren
          .get(i);
      if (popupPresenter == content) {
        (popupPresenter.getView()).setCloseHandler(null);
        break;
      }
    }
    if (i < popupChildren.size()) {
      popupChildren.remove(i);
    }
  }

  /**
   * Use {@link PresenterWidget#addToSlot(Object, PresenterWidget))} instead.
   */
  @Deprecated
  public void addContent(Object slot, PresenterWidget<?> content) {
    addToSlot(slot, content);
  }

  /**
   * Use {@link PresenterWidget#clearSlot(Object)} instead.
   */
  @Deprecated
  public void clearContent(Object slot) {
    clearSlot(slot);
  }

  /**
   * Use {@link PresenterWidget#removeFromSlot(Object, PresenterWidget)}
   * instead.
   */
  @Deprecated
  public void removeContent(Object slot, PresenterWidget<?> content) {
    removeFromSlot(slot, content);
  }

  /**
   * Use {@link PresenterWidget#setInSlot(Object, PresenterWidget)} instead.
   */
  @Deprecated
  public void setContent(Object slot, PresenterWidget<?> content) {
    setInSlot(slot, content, true);
  }

  /**
   * Use {@link PresenterWidget#setInSlot(Object, PresenterWidget, boolean)} instead.
   */
  @Deprecated
  public void setContent(Object slot, PresenterWidget<?> content, boolean performReset) {
    setInSlot(slot, content, performReset);
  }

  /**
   * Use {@link PresenterWidget#addToPopupSlot(PresenterWidget)} instead.
   */
  @Deprecated
  public void addPopupContent(final PresenterWidget<? extends PopupView> content) {
    addToPopupSlot(content, true);
  }

  /**
   * Use {@link PresenterWidget#addToPopupSlot(PresenterWidget, boolean)}
   * instead.
   */
  @Deprecated
  public void addPopupContent(
      final PresenterWidget<? extends PopupView> content, boolean center) {
    addToPopupSlot(content, center);
  }

}
