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

package com.gwtplatform.mvp.client;

/**
 * ApplicationController will trigger the generation of your Ginjector. To activate the
 * generation of your Ginjector, add these lines to your module.gwt.xml file:
 * <p/>
 * <pre>{@code
 * <define-configuration-property name="gin.module.name" is-multi-valued="false" />
 * <set-configuration-property name="gin.module.name" value="com.arcbees.example.client.gin.ClientModule"/>
 * <p/>
 * <define-configuration-property name="gin.ginjector" is-multi-valued="false"/>
 * <set-configuration-property name="gin.ginjector" value="com.arcbees.example.client.gin.ClientGinjector"/>
 * }</pre>
 * <p/>
 *
 * The ClientModule and the ClientGinjector property must point to the same package and ClientGinjector class name
 * is mandatory.
 *
 * The final step is to call {@code GWT.create(ApplicationController.class)} inside your entry point and then call
 * {@code applicationController.init()} .
 *
 * You can add additional methods to your Ginjector by specifying an additional interface.
 * <p/>
 * <pre>{@code
 * <define-configuration-property name="gin.ginjector.additional" is-multi-valued="false"/>
 * <set-configuration-property name="gin.ginjector.additional" value="com.arcbees.example.client.gin.ClientInjectorAdditional"/>
 * }</pre>
 * <p/>
 *
 * In this additional interface you can for example add a {@link com.gwtplatform.mvp.client.annotations.DefaultGatekeeper}.
 * <p/>
 * <pre>{@code
 * public interface ClientInjectorAdditional {
 *   @DefaultGatekeeper
 *   MyDefaultGateKeeper getMyDefaultGateKeeper();
 * }</pre>
 * <p/>
 */
public interface ApplicationController {
  String GINJECTOR_NAME = "ClientGinjector";

  void init();
}
