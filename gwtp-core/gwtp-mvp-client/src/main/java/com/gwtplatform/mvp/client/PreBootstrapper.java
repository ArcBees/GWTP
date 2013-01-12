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
 * When a class implementing the PreBoostrapper interface and annotated with
 * {@link com.gwtplatform.mvp.client.annotations.Bootstrap} is found, GWTP will call onPreBootstrap() before GWTP
 * is initialized. Because of this fact, no injection can be provided for this class.
 */
public interface PreBootstrapper {
    void onPreBootstrap();
}
