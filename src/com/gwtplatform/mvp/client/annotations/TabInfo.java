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

import com.gwtplatform.mvp.client.TabContainerPresenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the priority and name to display on a tab. Specify
 * either {@link #label} or {@link #getLabel}. The latter let you specify the
 * code to call to obtain the name of the tab. Make sure class names are fully
 * qualified. Also, you have access to the variable {@code ginjector} (your
 * specific {@link Ginjector}-derived class). For example:
 * 
 * <pre>
 * {@code @}TabInfo( 
 *    priority = 0,
 *    label = "Default" )
 *    
 * {@code @}TabInfo( 
 *    priority = 12,
 *    getLabel = "ginjector.getTranslations().tabDetailsLabel()" )
 * </pre>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface TabInfo {
  Class<? extends TabContainerPresenter<?>> container();

  String getLabel() default "";

  String label() default "";

  String nameToken() default "";

  int priority();
}
