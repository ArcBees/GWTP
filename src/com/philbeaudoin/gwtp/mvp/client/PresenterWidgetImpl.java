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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Widget;
import com.philbeaudoin.gwtp.mvp.client.proxy.ResetPresentersEvent;

/**
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
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

  private final List<PresenterWidgetImpl<? extends PopupView>> popupChildren = 
    new ArrayList<PresenterWidgetImpl<? extends PopupView>>();

  protected boolean visible = false;

  /**
   * The view for the presenter.
   */
  protected final V view;

  /**
   * The parent presenter, in order to make sure this widget is only ever in one parent.
   */
  PresenterWidgetImpl<? extends View> currentParentPresenter = null;
  

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

  // TODO This should be final. Can't be now because it makes testing injected 
  //      PresenterWidgets impossible. Make final once http://code.google.com/p/gwt-platform/issues/detail?id=111 is solved.
  @Override
  public V getView() {
    return view;
  }

  @Override
  public final boolean isVisible() {
    return visible;
  }

  /**
   * This method sets some content in a specific slot of the {@link Presenter}. A 
   * {@link ResetPresentersEvent} will be fired after the top-most visible presenter is
   * revealed. 
   * 
   * @param slot An opaque object identifying
   *             which slot this content is being set into. The attached view should know
   *             what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   */
  public void setContent( Object slot, PresenterWidget content ) {
    setContent(slot, content, true);
  }

  /**
   * This method sets some popup content within the {@link Presenter} and centers it.  
   * The view associated with the {@code content}'s presenter must inherit from 
   * {@link PopupView}. The popup will be visible and the corresponding presenter 
   * will receive the lifecycle events as needed.
   * <p />
   * Contrary to the {@link setContent()} method, no {@link ResetPresentersEvent} is fired.
   * 
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   * @param center Pass {@code true} to center the popup, otherwise its position will not be adjusted.
   * 
   * @see #addPopupContent(PresenterWidget)
   */
  protected void addPopupContent( final PresenterWidget content ) {
    addPopupContent( content, true );
  }

  /**
   * This method sets some popup content within the {@link Presenter}. 
   * The view associated with the {@code content}'s presenter must inherit from 
   * {@link PopupView}. The popup will be visible and the corresponding presenter 
   * will receive the lifecycle events as needed.
   * <p />
   * Contrary to the {@link setContent()} method, no {@link ResetPresentersEvent} is fired.
   * 
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   * @param center Pass {@code true} to center the popup, otherwise its position will not be adjusted.
   * 
   * @see #addPopupContent(PresenterWidget)
   */
  @SuppressWarnings("unchecked")
  protected void addPopupContent( final PresenterWidget content, boolean center ) {
    if( content == null )
      return;
    
    final PresenterWidgetImpl<? extends PopupView> contentImpl = 
      (PresenterWidgetImpl<? extends PopupView>) content;
    contentImpl.reparent( this );

    // Do nothing if the content is already added
    for( PresenterWidgetImpl<? extends PopupView> popupPresenter : popupChildren ) {
      if( popupPresenter == contentImpl )
        return;
    }

    PopupView popupView = contentImpl.getView();
    popupChildren.add( contentImpl );

    // Center if desired
    if( center )
      popupView.center();

    // Display the popup content
    if( isVisible() ) {
      popupView.show();
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      monitorCloseEvent( contentImpl );
      contentImpl.notifyReveal();
    }

  }

  /**
   * Makes sure we monitor the specified popup presenter so that we know when
   * it is closing. This way we can make sure it doesn't receive future messages.
   * 
   * @param popupPresenter The {@link PresenterWidgetImpl} to monitor.
   */
  private void monitorCloseEvent(final PresenterWidgetImpl<? extends PopupView> popupPresenter) {
    PopupView popupView = popupPresenter.getView();    

    popupView.setCloseHandler( new PopupViewCloseHandler() {      
      @Override
      public void onClose() {
        if( isVisible() )
          popupPresenter.notifyHide();
        removePopupChildren( popupPresenter );
      }
    } );
  }

  /**
   * Go through the popup children and remove the specified one.
   * 
   * @param content The {@link PresenterWidget} added as a popup which we now remove.
   */
  private void removePopupChildren(PresenterWidgetImpl<?> content) {
    int i;
    for( i=0; i < popupChildren.size(); ++i ) {
      PresenterWidgetImpl<? extends PopupView> popupPresenter = popupChildren.get(i);
      if( popupPresenter == content ) {
        (popupPresenter.getView()).setCloseHandler(null);
        break;
      }
    }
    if( i < popupChildren.size() )
      popupChildren.remove(i);
  }      

  /**
   * This method sets some content in a specific slot of the {@link Presenter}. The attached 
   * {@link View} should manage this slot when its {@link View#setContent(Object, Widget)} is called.
   * It should also clear the slot when the {@code setContent} method is called with {@code null} as
   * a parameter.
   * 
   * @param slot An opaque object identifying which slot this content is being set into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   * @param performReset Pass {@code true} if you want a {@link ResetPresentersEvent} to be fired
   *                     after the content has been added and this presenter is visible, pass 
   *                     {@code false} otherwise.
   */
  protected void setContent( Object slot, PresenterWidget content, boolean performReset ) {
    if( content == null ) {
      // Assumes the user wants to clear the slot content.
      clearContent( slot );
      return;
    }

    PresenterWidgetImpl<?> contentImpl = (PresenterWidgetImpl<?>) content;
    contentImpl.reparent( this );

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
      if( performReset ) {
        // And to reset everything if needed
        ResetPresentersEvent.fire( eventBus );
      }
    }
  }


  /**
   * This method adds some content in a specific slot of the {@link Presenter}. No
   * {@link ResetPresentersEvent} is fired. The attached {@link View} should manage
   * this slot when its {@link View#addContent(Object, Widget)} is called.
   * 
   * @param slot An opaque object identifying which slot this content is being added into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will not add anything.
   */
  public void addContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      return;
    }

    PresenterWidgetImpl<?> contentImpl = (PresenterWidgetImpl<?>) content;
    contentImpl.reparent( this );

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
    if( isVisible() )
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      contentImpl.notifyReveal();
  }


  /**
   * This method removes some content in a specific slot of the {@link Presenter}. No
   * {@link ResetPresentersEvent} is fired. The attached {@link View} should manage
   * this slot when its {@link View#removeContent(Object, Widget)} is called.
   * 
   * @param slot An opaque object identifying which slot this content is being removed from. 
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will not remove anything.
   */
  public void removeContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      return;
    }

    PresenterWidgetImpl<?> contentImpl = (PresenterWidgetImpl<?>) content;
    contentImpl.reparent( null );

    List<PresenterWidgetImpl<?>> slotChildren = activeChildren.get( slot );
    if( slotChildren != null ) {
      // This presenter is visible, its time to call onHide
      // on the child to be removed (and recursively on itschildren)
      if( isVisible() ) {
        contentImpl.notifyHide();
      }
      slotChildren.remove( contentImpl );
    }
    getView().removeContent( slot, contentImpl.getWidget() );
  }
  
  
  /**
   * This method clears the content in a specific slot. No {@link ResetPresentersEvent} is fired. 
   * The attached {@link View} should manage this slot when its {@link View#setContent(Object, Widget)} 
   * is called. It should also clear the slot when the {@code setContent} method is called with 
   * {@code null} as a parameter.
   * 
   * @param slot An opaque object identifying which slot to clear.
   */
  public void clearContent( Object slot ) {
    List<PresenterWidgetImpl<?>> slotChildren = activeChildren.get( slot );
    if( slotChildren != null ) {
      // This presenter is visible, its time to call onHide
      // on the children to be removed (and recursively on their children)
      if( isVisible() ) {
        for( PresenterWidgetImpl<?> activeChild : slotChildren ) {
          activeChild.notifyHide();
        }
      }
      slotChildren.clear();
    }
    getView().setContent( slot, null );
  }

  @Override
  public Widget getWidget() {
    return getView().asWidget();
  }

  /**
   * Called right after the widget has been revealed on screen.
   * You should not call this. Fire a 
   * {@link ResetPresentersEvent} instead.
   */
  // TODO This was private. Can't be now because it makes testing injected 
  //      PresenterWidgets impossible. Should move to base class
  //      once http://code.google.com/p/gwt-platform/issues/detail?id=111 is solved.
  public void notifyReveal() {
    assert !isVisible() : "notifyReveal() called on a visible presenter!";
    onReveal();
    visible = true;
    for (List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values())
      for( PresenterWidgetImpl<?> activeChild : slotChildren )
        activeChild.notifyReveal();
    for( PresenterWidgetImpl<? extends PopupView> popupPresenter : popupChildren ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      popupPresenter.getView().show();
      monitorCloseEvent( popupPresenter );
      popupPresenter.notifyReveal();
    }
  }

  /**
   * Called right after the widget has been made revealed on screen.
   * You should not call this, fire a 
   * {@link ResetPresentersEvent} instead.
   */
  void notifyHide() {
    assert isVisible() : "notifyHide() called on a hidden presenter!";
    for (List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values())
      for( PresenterWidgetImpl<?> activeChild : slotChildren )
        activeChild.notifyHide();
    for( PresenterWidgetImpl<? extends PopupView> popupPresenter : popupChildren ) {
      popupPresenter.getView().setCloseHandler(null);
      popupPresenter.notifyHide();
      popupPresenter.getView().hide();
    }
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
    for( PresenterWidgetImpl<? extends PopupView> popupPresenter : popupChildren )
      popupPresenter.reset();
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
  protected final <H extends EventHandler> void addRegisteredHandler(Type<H> type,
      H handler) {
    registerHandler(eventBus.addHandler(type, handler));
  }
  

  /**
   * Called by a child {@link PresenterWidget} when it wants to detach itself
   * from this parent.  
   * 
   * @param childPresenter The {@link PresenterWidgetImpl} that is a child of this presenter. 
   */
  private void detach( PresenterWidgetImpl<? extends View> childPresenter ) {
    for( List<PresenterWidgetImpl<?>> slotChildren : activeChildren.values() )    
      slotChildren.remove( childPresenter );
    popupChildren.remove( childPresenter );
  }
  
  

  /**
   * <b>Important!</b> Do not call directly, this is meant to be called only internally
   * by GWTP.
   * <p/>
   * This methods attaches this presenter to its parent.  
   * 
   * @param newParent The {@link PresenterWidgetImpl} that will be this presenter's new parent, 
   *                  or {@code null} to detach from all parents.
   */
  private void reparent( PresenterWidgetImpl<? extends View> newParent ) {
    if( currentParentPresenter != null && currentParentPresenter != newParent )
      currentParentPresenter.detach( this );
    currentParentPresenter = newParent;
  }
  
}