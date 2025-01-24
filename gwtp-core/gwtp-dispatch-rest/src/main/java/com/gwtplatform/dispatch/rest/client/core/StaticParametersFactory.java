/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.core;

import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.annotations.DefaultDateFormat;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;

import jakarta.inject.Inject;

/**
 * Used internally by generated code to access various implementations bound by GIN. Injection could be used in
 * generated code but because of changes introduced to the compiler in GWT 2.7, GIN generators may run before the
 * generated GIN module is created and injection doesn't work in generated code.
 * <p>
 * <strong>Don't use this class as it may change without notice.</strong>
 */
public class StaticParametersFactory {
    @Inject
    static RestDispatch restDispatch;
    @Inject
    static HttpParameterFactory httpParameterFactory;
    @Inject
    @DefaultDateFormat
    static String defaultDateFormat;

    public static RestDispatch getRestDispatch() {
        return restDispatch;
    }

    public static HttpParameterFactory getHttpParameterFactory() {
        return httpParameterFactory;
    }

    public static String getDefaultDateFormat() {
        return defaultDateFormat;
    }
}
