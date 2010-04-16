/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.inject.client.Ginjector;

/**
 * Annotation used to specify the code to call when creating a 
 * new place. Usually a simple call to {@code new}. Make sure
 * class names are fully qualified. Also, you have access
 * to the variable {@code nameToken} (a String) and 
 * {@code ginjector} (your specific {@link Ginjector}-derived
 * class). For example:
 * <pre>
 * {@code @}NewPlaceCode( "new com.project.client.AdminSecurePlace(nameToken, ginjector.getCurrentUser())" )
 * </pre>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface PlaceInstance {
  String value();
}
