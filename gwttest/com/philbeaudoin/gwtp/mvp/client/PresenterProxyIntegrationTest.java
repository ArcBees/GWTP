package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.philbeaudoin.gwtp.mvp.client.annotations.ProxyCodeSplit;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;

/**
 * Integration test for {@link PresenterImpl} and 
 * automatically-generated {@link Proxy} classes.
 * <p />
 * TODO: This doesn't work yet. See http://code.google.com/p/gwt-platform/issues/detail?id=38
 * 
 * @author Philippe Beaudoin
 */
public class PresenterProxyIntegrationTest extends GWTTestCase {

  public static class MyPresenter extends PresenterImpl<View, MyPresenter.MyProxy> {

    @ProxyCodeSplit
    public interface MyProxy extends Proxy<MyPresenter> {}
        
    public MyPresenter() {
      super((EventBus)GWT.create(EventBus.class), (View)GWT.create(View.class), (MyProxy)GWT.create(MyProxy.class));
    }

    @Override
    protected void revealInParent() {
      // TODO Auto-generated method stubs
    }
    
  }
  
  public void test() {
    GWT.create(MyPresenter.class);   
  }

  @Override
  public String getModuleName() {
    return "com.philbeaudoin.gwtp.mvp.Mvp";
  }
}
