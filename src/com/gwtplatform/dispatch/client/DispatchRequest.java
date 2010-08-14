/**
 * Copyright 2010 ArcBees.Inc
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

package com.gwtplatform.dispatch.client;

/**
 * This interface provide a common base to give the ability to the client to
 * cancel a request or see if a request is still pending.
 * 
 * @author Christian Goudreau
 */
public interface DispatchRequest {
  public boolean isPending();

  public void cancel();
}
