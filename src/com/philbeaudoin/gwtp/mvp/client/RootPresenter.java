package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.philbeaudoin.gwtp.mvp.client.proxy.ResetPresentersEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.ResetPresentersHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealRootContentEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealRootContentHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealRootLayoutContentHandler;

/**
 * This is the presenter for the top-level of the application. It is
 * derived from presenter widget, but it's just because it doesn't
 * need a proxy has it will be bound as an eager singleton.
 * It sets content within
 * GWT's {@link RootPanel} and {@link RootLayoutPanel}.
 * <p />
 * Fire a {@link RevealContentEvent} with type {@link #TYPE_RevealContent}
 * or {@link #TYPE_RevealLayoutContent} to set your presenter at the
 * top level. The choice depends on whether your presenter works
 * as a {@link com.google.gwt.user.client.ui.Panel} or as a
 * {@link com.google.gwt.user.client.ui.LayoutPanel}.
 * 
 * @author Philippe Beaudoin
 */
public class RootPresenter extends PresenterWidgetImpl<RootPresenter.RootView> implements ResetPresentersHandler {

  private final static Object rootSlot = new Object();
  private PresenterWidgetImpl<?> activePresenter = null;
  
  public final static class RootView extends ViewImpl {
    
    private Widget contentToSet = null;
    
    @Override
    public void setContent(Object slot, Widget content) {
      assert slot == rootSlot : "Unknown slot used in the root proxy.";
      contentToSet = content;
    }
    
    private void setContentInRootPanel() {
      assert contentToSet != null : "Trying to set null content in root presenter.";
      // TODO Next 2 lines are a dirty workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=4737
      RootLayoutPanel.get().clear();
      RootPanel.get().clear();
      RootPanel.get().add(contentToSet);
    } 
    
    private void setContentInRootLayoutPanel() {
      assert contentToSet != null : "Trying to set null content in root presenter.";
      // TODO Next 2 lines are a dirty workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=4737
      RootPanel.get().clear();
      RootLayoutPanel.get().clear();
      RootPanel.get().add( RootLayoutPanel.get() );
      RootLayoutPanel.get().add(contentToSet);
    }       

    @Override
    public Widget asWidget() {
      assert false : "Root view has no widget, you should never call asWidget()";
      return null;
    }
  }
  
  
  /**
   * Creates a proxy class for a presenter that can contain tabs.
   * 
   * @param eventBus The event bus.
   */
  @Inject
  public RootPresenter(
      final EventBus eventBus,
      final RootView view ) {
    super(eventBus, view);
    visible = true;
  }

  @Override
  protected void onBind() {
    super.onBind();

    registerHandler( eventBus.addHandler( RevealRootContentEvent.getType(), new RevealRootContentHandler(){
      @Override
      public void onRevealContent(final RevealRootContentEvent revealContentEvent) {
        activePresenter = (PresenterWidgetImpl<?>)revealContentEvent.getContent();
        setContent( rootSlot, activePresenter );
        getView().setContentInRootPanel();
      }
    } ) );    

    registerHandler( eventBus.addHandler( RevealRootLayoutContentEvent.getType(), new RevealRootLayoutContentHandler(){
      @Override
      public void onRevealContent(final RevealRootLayoutContentEvent revealContentEvent) {
        activePresenter = (PresenterWidgetImpl<?>)revealContentEvent.getContent();
        setContent( rootSlot, activePresenter );
        getView().setContentInRootLayoutPanel();
      }
    } ) );  

    registerHandler( eventBus.addHandler( ResetPresentersEvent.getType(), this ) );    

  }

  @Override
  public void onResetPresenters(ResetPresentersEvent resetPresentersEvent) {
    if( activePresenter != null )
      activePresenter.reset();    
  }
}
