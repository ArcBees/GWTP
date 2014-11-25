/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind2.resource;

import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JMethod;

// TODO: Include any ClassDefinition generated along
public class ResourceMethodDefinition {
    private final JMethod method;
    private final String output;
    private final Set<String> imports;

    public ResourceMethodDefinition(
            JMethod method,
            Set<String> imports,
            String output) {
        this.method = method;
        this.imports = imports;
        this.output = output;
    }

    public JMethod getMethod() {
        return method;
    }

    public Set<String> getImports() {
        return imports;
    }

    public String getOutput() {
        return output;
    }
}
