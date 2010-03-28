package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.server.standard.AbstractStandardDispatchServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GuiceStandardDispatchServlet extends AbstractStandardDispatchServlet {

  private static final long serialVersionUID = -3370273399970264625L;

  private final Dispatch dispatch;

  @Inject
  public GuiceStandardDispatchServlet( Dispatch dispatch ) {
    this.dispatch = dispatch;
  }

  @Override
  protected Dispatch getDispatch() {
    return dispatch;
  }
}
