/*
 * Copyright 2016 ArcBees Inc.
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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.gwtplatform.mvp.client.PreBootstrapper;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Configures the {@link PreBootstrapper} to use in your generated entry-point.
 * <p>
 * This annotation must be put on a class that is also annotated with
 * {@link com.gwtplatform.common.client.annotations.GwtpApp @GwtpApp}. Only one such class is authorized in an
 * application.
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface UsePreBootstrapper {
    Class<? extends PreBootstrapper> value();
}
