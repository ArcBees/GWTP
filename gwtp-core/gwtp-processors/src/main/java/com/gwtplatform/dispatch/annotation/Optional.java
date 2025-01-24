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

package com.gwtplatform.dispatch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use the @{@link Optional} annotation to specify optional fields.
 * <p>
 * <p>
 * If one or more fields are declared optional, the system generates an
 * additional pair of constructor and {@code fire} method that can be called
 * without these fields.
 * </p>
 * <p>
 * The omitted fields are not initialized and will contain
 * their default value (i.e. objects will be initialized to {@code null}).
 * </p>
 * <p>
 * <p>
 * You can use this annotation with:
 * </p>
 * <ul>
 * <li>@{@link GenEvent}</li>
 * <li>@{@link GenDto}</li>
 * <li>@{@link GenDispatch}</li>
 * </ul>
 * <p>
 * <p>
 * See the above annotations for specific behaviors with optional fields.
 * </p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Optional {
}
