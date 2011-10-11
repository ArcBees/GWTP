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

import com.gwtplatform.common.client.ProviderBundle;

/**
 * Use this annotation if you want to have certain
 * {@link com.gwtplatform.mvp.client.proxy.Proxy}s and their associated
 * {@link com.gwtplatform.mvp.client.Presenter}s to sit behind one split point
 * and to be compiled into one javascript file seperately from others.
 * <p/>
 * Use this annotation if you already have too much code splitting using
 * {@link ProxyCodeSplit} and it is more efficient to group
 * {@link com.gwtplatform.mvp.client.Presenter}s because they share a bulk of
 * their code. You will also have to set up your own implementation of a
 * {@link ProviderBundle}.
 * <p/>
 * Here is an example use of {@link ProxyCodeSplitBundle}:
 * 
 * <pre>
 * &#064;ProxyCodeSplitBundle(bundleClass = MyPresenterBundle.class, id = MyPresenterBundle.ID_Object1)
 * public interface MyProxy extends ProxyPlace&lt;Object1&gt; {
 * }
 * </pre>
 * 
 * 
 * @see ProviderBundle
 * @see <a
 *      href="http://code.google.com/intl/de-DE/webtoolkit/doc/latest/DevGuideCodeSplitting.html">Code
 *      Splitting</a>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface ProxyCodeSplitBundle {
  Class<? extends ProviderBundle> bundleClass();

  int id();
}
