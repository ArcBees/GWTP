package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * A simple implementation of view that simply disregards every call to
 * {@link #setContent(Object, Widget)}, {@link #addContent(Object, Widget)},
 * and {@link #clearContent(Object)}.
 * 
 * @author Philippe Beaudoin
 */
public abstract class ViewImpl implements View {

  @Override
  public void addContent(Object slot, Widget content) {
  }
  @Override
  public void clearContent(Object slot) {
  }
  @Override
  public void setContent(Object slot, Widget content) {
  }

}
