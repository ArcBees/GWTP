/*
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

import com.google.gwt.core.client.EntryPoint;

/**
 * ApplicationController will trigger the generation of your Ginjector. To activate the
 * generation you have to add one line to your module.gwt.xml file:
 * <p>
 * <pre>{@code
 * <set-configuration-property name="gin.ginjector.modules" value="com.arcbees.example.client.gin.ClientModule"/>
 * }</pre>
 * Multiple modules may be supplied as comma separated list.
 * <p>
 * <p>
 * The final step is to call {@code GWT.create(ApplicationController.class)} inside your entry point and then call
 * {@code applicationController.init()} .
 * <p>
 * You can add extension methods to your Ginjector by specifying additional interfaces. This property also
 * accepts a comma separated list.
 * <p>
 * <pre>{@code
 * <set-configuration-property name="gin.ginjector.extensions" value="com.arcbees.example.client.gin
 * .ClientInjectorAdditional"/>
 * }</pre>
 * <p>
 * <p>
 * In these additional interfaces you may for example add a {@link com.gwtplatform.mvp.client.annotations
 * .DefaultGatekeeper}.
 * <p>
 * <pre>{@code
 * public interface ClientInjectorAdditional {
 *   @DefaultGatekeeper
 *   MyDefaultGateKeeper getMyDefaultGateKeeper();
 * }}</pre>
 * <p>
 * see https://github.com/ArcBees/GWTP/wiki/Bootstrapping
 */
public interface ApplicationController extends EntryPoint {
    void init();
}
