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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation in your custom ginjector to annotate a method returning a
 * {@link com.gwtplatform.mvp.client.proxy.Gatekeeper}-derived class. This class
 * will be used to provide places for proxys that are not annotated with the
 * {@link UseGatekeeper} annotation.
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.METHOD)
public @interface DefaultGatekeeper {
}
