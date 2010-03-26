package com.philbeaudoin.gwtp.mvp.client;

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
  private final Map< Object, List<PresenterWidget> > activeChildren =
    new HashMap< Object, List<PresenterWidget> >();

  protected boolean visible = false;

  /**
   * The view for the presenter.
   */
  protected final V view;


  public PresenterWidgetImpl(
      EventBus eventBus, 
      V view) {
    super();
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

  @Override
  public void setContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      // Assumes the user wants to clear the slot content.
      clearContent( slot );
      return;
    }
    List<PresenterWidget> slotChildren = activeChildren.get( slot );

    if( slotChildren != null ) {
      if( slotChildren.size() == 1 && slotChildren.get(0) == content )
        // The slot contains the right content, nothing to do
        return;

      if( isVisible() ) {
        // We are visible, make sure the content that we're removing
        // is being notified as hidden
        for( PresenterWidget activeChild : slotChildren )
          activeChild.onHide();
      }
      slotChildren.clear();
      slotChildren.add( content );
    }
    else {
      slotChildren = new ArrayList<PresenterWidget>(1);
      slotChildren.add( content );
      activeChildren.put( slot, slotChildren );
    }

    // Set the content in the view
    getView().setContent( slot, content.getWidget() );
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.onReveal();
      // And to reset everything
      ResetPresentersEvent.fire( eventBus );
    }
  }

  @Override
  public void addContent( Object slot, PresenterWidget content ) {
    if( content == null ) {
      return;
    }
    List<PresenterWidget> slotChildren = activeChildren.get( slot );
    if( slotChildren != null ) {
      slotChildren.add( content );
    }
    else {
      slotChildren = new ArrayList<PresenterWidget>(1);
      slotChildren.add( content );
      activeChildren.put( slot, slotChildren );
    }
    getView().addContent( slot, content.getWidget() );
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      content.onReveal();
      // And to reset everything
      ResetPresentersEvent.fire( eventBus );
    }
  }

  @Override
  public void clearContent( Object slot ) {
    if( isVisible() ) {
      // This presenter is visible, its time to call onReveal
      // on the newly added child (and recursively on this child children)
      List<PresenterWidget> slotChildren = activeChildren.get( slot );
      if( slotChildren != null ) {
        for( PresenterWidget activeChild : slotChildren )
          activeChild.onHide();
        slotChildren.clear();
      }
    }
    getView().clearContent( slot );
  }

  @Override
  public Widget getWidget() {
    return getView().asWidget();
  }

  @Override
  public void onReveal() {
    assert !isVisible() : "onReveal() called on a visible presenter! Did somebody forget to call super.onReveal()?";
    visible = true;
    for (List<PresenterWidget> slotChildren : activeChildren.values())
      for( PresenterWidget activeChild : slotChildren )
        activeChild.onReveal();
  }

  @Override
  public void onHide() {
    assert isVisible() : "onHide() called on a hidden presenter! Did somebody forget to call super.onHide()?";
    for (List<PresenterWidget> slotChildren : activeChildren.values())
      for( PresenterWidget activeChild : slotChildren )
        activeChild.onHide();    
    visible = false;
  }  

  @Override
  public final void reset() {
    onReset();
    for (List<PresenterWidget> slotChildren : activeChildren.values())
      for( PresenterWidget activeChild : slotChildren )
        activeChild.reset();    
  }

  @Override
  public void onReset() {}

}