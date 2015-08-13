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

package com.gwtplatform.processors.tools.domain;

import java.util.Collection;

import com.google.common.base.Function;

public interface HasImports {
    Function<HasImports, Iterable<String>> EXTRACT_IMPORTS_FUNCTION = new Function<HasImports, Iterable<String>>() {
        @Override
        public Iterable<String> apply(HasImports hasImports) {
            return hasImports.getImports();
        }
    };

    Collection<String> getImports();
}
