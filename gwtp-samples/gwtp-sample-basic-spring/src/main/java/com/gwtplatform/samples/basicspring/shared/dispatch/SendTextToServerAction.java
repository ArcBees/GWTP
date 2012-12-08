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

package com.gwtplatform.samples.basicspring.shared.dispatch;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

/**
 * An action that can be sent using an {@link DispatchAsync} (client-side)
 * corresponding to a {@link com.gwtplatform.dispatch.server.Dispatch}
 * (server-side).
 */
public class SendTextToServerAction extends UnsecuredActionImpl<SendTextToServerResult> {
  private String textToServer;

  public SendTextToServerAction(final String textToServer) {
    this.textToServer = textToServer;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private SendTextToServerAction() {
  }

  public String getTextToServer() {
    return textToServer;
  }
}
