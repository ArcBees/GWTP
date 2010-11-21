/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.shared.annotation;

import com.gwtplatform.dispatch.shared.Action;

/**
 * For testing purposes only.
 * 
 * @author Brendan Doherty
 */
@GenDispatch(isSecure = false, serviceName = Action.DEFAULT_SERVICE_NAME
    + "Blah", extraResultInterfaces = "com.gwtplatform.dispatch.shared.annotation.HasThing<com.gwtplatform.dispatch.shared.annotation.Foo>")
public class RetrieveBar {
  @In(1)
  String goodName;

  @Out(1)
  Foo thing;

  @Out(2)
  int meaningOfLife;
}
