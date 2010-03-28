package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.philbeaudoin.gwtp.dispatch.client.secure.SecureDispatchService;
import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.server.secure.AbstractSecureDispatchServlet;
import com.philbeaudoin.gwtp.dispatch.server.secure.SecureSessionValidator;
import com.philbeaudoin.gwtp.dispatch.shared.secure.InvalidSessionException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A servlet implementation of the {@link SecureDispatchService}. This will
 * call the provided {@link SecureSessionValidator} to confirm that the provided
 * session ID is still valid before executing any actions. If not, an
 * {@link InvalidSessionException} is thrown back to the client.
 * 
 * @author David Peterson
 */
@Singleton
public class GuiceSecureDispatchServlet extends AbstractSecureDispatchServlet implements SecureDispatchService {

  private static final long serialVersionUID = 6018694046678872275L;

  private final Dispatch dispatch;

  private final SecureSessionValidator sessionValidator;

  @Inject
  public GuiceSecureDispatchServlet( Dispatch dispatch, SecureSessionValidator sessionValidator ) {
    this.dispatch = dispatch;
    this.sessionValidator = sessionValidator;
  }

  @Override
  public SecureSessionValidator getSessionValidator() {
    return sessionValidator;
  }

  @Override
  protected Dispatch getDispatch() {
    return dispatch;
  }
}
