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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;
import com.philbeaudoin.gwtp.mvp.client.proxy.ResetPresentersEvent;

public abstract class PresenterWidgetImpl<V extends View>
extends HandlerContainerImpl implements PresenterWidget {

  /**
   * The {@link EventBus} for the application.
   */
  protected final EventBus eventBus;

  /**
   * This map makes it possible to keep a list of all the active children
   * in every slot managed by this PresenterWidget. A slot is identified by an opaque
   * object. A single slot can have many children.
   */
  private final Map< Object, List<PresenterWidgetImpl<?>> > activeChildren =
    new HashMap< Object, List<PresenterWidgetImpl<?>> >();

  protected boolean visible = false;

  /**
   * The view for the presenter.
   */
  protected final V view;


  /**
   * Creates a {@link PresenterWidgetImpl}.
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   */
  public PresenterWidgetImpl(
      EventBus eventBus, 
      V view) {
    super();
    this.view = view;
    this.eventBus = eventBus;
  }

  /**
   * Creates a {@link PresenterWidgetImpl} that is not necessarily using automatic
   * binding (see {@link HandlerContainerImpl(boolean)}).
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   * @param autoBind {@code true} to request automatic binding, {@code false} otherwise.
   */
  public PresenterWidgetImpl( 
      boolean autoBind,
      EventBus eventBus, 
      V view) { 
    super(autoBind);
    this.view = view;
    this.eventBus = eventBus;
  }

  @Override
  public final V getView() {
    return view;
  }

  @Override
  public final boolean isVisible() {
    return visible;
  }

  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * 
   * @param slot An opaque object identifying
   *             which slot this content is being set into. The attached view should know
   *             what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   */
  public void setContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      // Assumes the user wants to clear the slot content.
      clearContent( slot );
      return;
    }

    PresenterWidgetImpl<?> contentImpl = (PresenterWidgetImpl<?>) content;

    List<PresenterWidgetImpl<?>> slotChildren = activeChildren.get( slot );

    if( slotChildren != null ) {
      if( slotChildren.size() == 1 && slotChildren.get(0) == contentImpl )
        // The slot contains the right content, nothing to do
        return;

      if( isVisible() ) {
        // We are visible, make sure the content that we're removing
        // is being notified as hidden
        for( PresenterWidgetImpl<?> activeChild : slotChildren )
          activeChild.notifyHide();
      }
      slotChildren.clear();
      slotChildren.add( contentImpl );
    }
    else {
      slotChildren = new ArrayList<PresenterWidgetImpl<?>>(1);
      slotChildren.add( contentImpl );
      activeChildren.put( slot, slotChildren );
    }

    // Set the content in the view
    getView().setContent( slot, contentImpl.getWidget() );
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      contentImpl.notifyReveal();
      // And to reset everything
      ResetPresentersEvent.fire( eventBus );
    }
  }


  /**
   * This method adds some content in a specific slot of the {@link Presenter}.
   * 
   * @param slot An opaque object identifying
   *             which slot this content is being added into. The attached view should know
   *             what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will not do anything.
   */
  public void addContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      return;
    }

    PresenterWidgetImpl<?> contentImpl = (PresenterWidgetImpl<?>) content;

    List<PresenterWidgetImpl<?>> slotChildren = activeChildren.get( slot );
    if( slotChildren != null ) {
      slotChildren.add( contentImpl );
    }
    else {
      slotChildren = new ArrayList<PresenterWidgetImpl<?>>(1);
      slotChildren.add( contentImpl );
      activeChildren.put( slot, slotChildren );
    }
    getView().addContent( slot, contentImpl.getWidget() );
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      contentImpl.notifyReveal();
      // And to reset everything
      ResetPresentersEvent.fire( eventBus );
    }
  }

  /**
   * This method clears the content in a specific slot.
   * 
   * @param slot An opaque object of type identifying
   *             which slot to clear. The attached view should know
   *             what to do with this slot.
   */
  public void clearContent( Object slot ) {
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      List<PresenterWidgetImpl<?>> slotChildren = activeChildren.get( slot );
      if( slotChildren != null ) {
        for( PresenterWidgetImpl<?> activeChild : slotChildren )
          activeChild.notifyHide();
        slotChildren.clear();
      }
    }
    getView().clearContent( slot );
  }

  @Override
  public Widget getWidget() {
    return getView().asWidget();
  }

  /**
   * Called right after the widget has been made revealed on screen.
   * You should not call this. Fire a 
   * {@link ResetPresentersEvent} instead.
   */
  final void notifyReveal() {
    assert !isVisible() : "notifyReveal() called on a visible presenter!";
    onReveal();
    visible = true;
    for (List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values())
      for( PresenterWidgetImpl<?> activeChild : slotChildren )
        activeChild.notifyReveal();
  }

  /**
   * Called right after the widget has been made revealed on screen.
   * You should not call this, fire a 
   * {@link ResetPresentersEvent} instead.
   */
  final void notifyHide() {
    assert isVisible() : "notifyHide() called on a hidden presenter!";
    for (List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values())
      for( PresenterWidgetImpl<?> activeChild : slotChildren )
        activeChild.notifyHide();
    visible = false;
    onHide();
  }  

  /**
   * Called whenever the presenters need to be reset. You should not 
   * call this, fire a {@link ResetPresentersEvent} instead.
   */
  final void reset() {
    onReset();
    for (List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values())
      for( PresenterWidgetImpl<?> activeChild : slotChildren )
        activeChild.reset();    
  }

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReveal()}
   * if you override.
   * <p />
   * This method will be called whenever the presenter is revealed. Override
   * it to perform any action (such as refreshing content) that needs
   * to be done when the presenter is revealed.
   */
  protected void onReveal() {}

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onHide()}
   * if you override.
   * <p />
   * Override this method to perform any clean-up operations. For example,
   * objects created directly or indirectly during the call to
   * {@link #onReveal()} should be disposed of in this methods.
   */
  protected void onHide() {}

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReset()}
   * if you override.
   * <p />
   * This method is called whenever a new presenter is requested, even if 
   * the presenter was already visible. It is called on every visible presenter,
   * starting from the top-level presenter and going to the leaves.
   */
  protected void onReset() {}

}