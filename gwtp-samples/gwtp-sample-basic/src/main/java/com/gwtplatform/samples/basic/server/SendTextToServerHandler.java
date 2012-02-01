/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.samples.basic.server;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.samples.basic.shared.FieldVerifier;
import com.gwtplatform.samples.basic.shared.SendTextToServer;
import com.gwtplatform.samples.basic.shared.SendTextToServerResult;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Philippe Beaudoin
 */
public class SendTextToServerHandler implements
    ActionHandler<SendTextToServer, SendTextToServerResult> {

  private Provider<HttpServletRequest> requestProvider;
  private ServletContext servletContext;

  @Inject
  SendTextToServerHandler(ServletContext servletContext,
      Provider<HttpServletRequest> requestProvider) {
    this.servletContext = servletContext;
    this.requestProvider = requestProvider;
  }

  @Override
  public SendTextToServerResult execute(SendTextToServer action,
      ExecutionContext context) throws ActionException {

    String input = action.getTextToServer();

    // Verify that the input is valid.
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new ActionException("Name must be at least 4 characters long");
    }

    String serverInfo = servletContext.getServerInfo();
    String userAgent = requestProvider.get().getHeader("User-Agent");
    return new SendTextToServerResult("Hello, " + input
        + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent);
  }

  @Override
  public Class<SendTextToServer> getActionType() {
    return SendTextToServer.class;
  }

  @Override
  public void undo(SendTextToServer action, SendTextToServerResult result,
      ExecutionContext context) throws ActionException {
    // Not undoable
  }

}
