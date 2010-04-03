package com.philbeaudoin.gwtp.dispatch.server.guice;

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
