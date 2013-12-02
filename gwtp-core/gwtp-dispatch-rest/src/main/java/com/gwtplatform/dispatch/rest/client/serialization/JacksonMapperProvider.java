/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

/**
 * Provides access to the gwt-jackson object mappers generated at the compilation.
 */
public interface JacksonMapperProvider {
    /**
     * @return {@code true} if there is a mapper for {@code type}.
     */
    boolean hasMapper(String type);

    /**
     * @return the mapper for {@code type} or {@code null} if there is no mapper registered.
     */
    <T> ObjectMapper<T> getMapper(String type);
}
