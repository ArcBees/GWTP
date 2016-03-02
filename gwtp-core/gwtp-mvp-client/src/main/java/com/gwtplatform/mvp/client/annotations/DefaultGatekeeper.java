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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a {@link com.gwtplatform.mvp.client.proxy.Gatekeeper Gatekeeper}-derived class to tell GWTP it has tp use it
 * by default. This class will be used to provide places for proxies that are not annotated with the {@link
 * UseGatekeeper} or {@link NoGatekeeper} annotations. Only one gatekeeper can be annotated with this annotation.
 */
@Target({TYPE, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface DefaultGatekeeper {
}
