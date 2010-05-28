package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple implementation of {@link PopupView} that can be used when the
 * widget returned by {@link #asWidget()} inherits from {@link PopupPanel}.  
 * 
 * Also, this implementation simply disregards every call to
 * {@link #setContent(Object, Widget)}, {@link #addContent(Object, Widget)},
 * and {@link #clearContent(Object)}.
 * 
 * @author Philippe Beaudoin
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {  

  private HandlerRegistration closeHandlerRegistration;

  @Override
  public void show() {
    asPopupPanel().show();
  }

  @Override
  public void hide() {
    asPopupPanel().hide();    
  }

  @Override
  public void center() {
    boolean wasVisible = asPopupPanel().isVisible();
    asPopupPanel().center();
    if( !wasVisible )
      asPopupPanel().hide();
  }

  @Override
  public void setPosition( int left, int top ) {
    asPopupPanel().setPopupPosition(left, top);
  }

  @Override
  public void setCloseHandler(
      final PopupViewCloseHandler popupViewCloseHandler) {
    if( closeHandlerRegistration != null )
      closeHandlerRegistration.removeHandler();
    if( popupViewCloseHandler == null )
      closeHandlerRegistration = null;
    else {
      closeHandlerRegistration = 
        asPopupPanel().addCloseHandler( new CloseHandler<PopupPanel>() {      
          @Override
          public void onClose(CloseEvent<PopupPanel> event) {
            popupViewCloseHandler.onClose();
          }
        } );
    }
  }

  /**
   * Retrieves this view as a {@link PopupPanel}. See {@link #asWidget()}.
   * 
   * @return This view as a {@link PopupPanel} object.
   */
  protected PopupPanel asPopupPanel() {
    return (PopupPanel)asWidget();
  }
}
