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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is used on the declaration of a presenter's
 * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace ProxyPlace}
 * to specify the name token to use to access this presenter.
 *
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface NameToken {
    String[] value();
}
