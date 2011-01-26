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
 * This annotation can be used to specify a function returning the label of a 
 * tab as a string. For simple hard-coded labels see {@link TabLabel}.
 * <p />
 * You can use {@link TabLabelFunction} to annotate a static 
 * public method in your presenter that returns a string (the label). This 
 * method can optionally accept a parameter corresponding to your custom ginjector.
 * Example of use:
 * 
 * <pre>
 *  {@code @}TabLabelFunction
 *  static public String getTranslatedTabLabel( MyGinjector ginjector ) {
 *    return ginjector.getTranslations().productTabLabel();
 *  }
 * </pre>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.METHOD)
public @interface TabLabelFunction {
}
