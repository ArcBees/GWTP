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
 * ApplicationController when bound in your .gwt.xml will trigger the generation of your Ginjector. To activate the
 * generation of your Ginjector, remove this line from your module.gwt.xml file:
 * <pre>{@code
 * <inherits name='com.gwtplatform.mvp.Mvp'/>
 * }</pre>
 * and replace it by:
 * <pre>{@code
 * <inherits name='com.gwtplatform.mvp.MvpGinjectorGenerator'/>
 * }</pre>
 *
 * The next step is to replace on your Ginjector the annotation {@link com.google.gwt.inject.client.GinModules
 * GinModules} by {@link com.gwtplatform.mvp.client.annotations.GWTPGinModules GWTPGinModules}.
 * <br/>
 * The final step is to call {@code GWTP.create(ApplicationController.class)} inside your entry point.
 */
public interface ApplicationController {
}
