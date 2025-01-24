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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation with a {@link com.gwtplatform.mvp.client.proxy.Proxy} if
 * you want the associated {@link com.gwtplatform.mvp.client.Presenter} to sit
 * behind a split point. Thus the javascript code will be compiled into a new
 * javascript file and only be loaded from the webapplication server if needed.
 * <p>
 * Here is an example use of {@link ProxyCodeSplit}:
 * <p>
 * <pre>
 * &#064;ProxyCodeSplit
 * public interface MyProxy extends ProxyPlace&lt;MyPresenter&gt; {
 * }
 * </pre>
 *
 * @see <a href="http://code.google.com/webtoolkit/doc/latest/DevGuideCodeSplitting.html">Code Splitting</a>
 */
@Target(ElementType.TYPE)
public @interface ProxyCodeSplit {
}
