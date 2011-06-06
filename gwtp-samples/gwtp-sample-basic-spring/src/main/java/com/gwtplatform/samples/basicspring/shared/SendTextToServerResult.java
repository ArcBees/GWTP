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

package com.gwtplatform.samples.basicspring.shared;

import com.gwtplatform.dispatch.shared.Result;

/**
 * The result of a {@link SendTextToServer} action.
 */
public class SendTextToServerResult implements Result {

  private static final long serialVersionUID = 4621412923270714515L;

  private String response;

  public SendTextToServerResult(final String response) {
    this.response = response;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private SendTextToServerResult() {
  }

  public String getResponse() {
    return response;
  }

}
