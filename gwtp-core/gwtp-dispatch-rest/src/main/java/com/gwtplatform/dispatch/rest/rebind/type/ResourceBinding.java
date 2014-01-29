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

package com.gwtplatform.dispatch.rest.rebind.type;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.JParameter;

public class ResourceBinding {
    private final String resourcePath;
    private final String implPackage;
    private final String implName;
    private String superTypeName;
    private List<JParameter> ctorParameters = Lists.newArrayList();

    public ResourceBinding(
            String resourcePath,
            String implPackage,
            String implName) {
        this.resourcePath = Strings.nullToEmpty(resourcePath);
        this.implName = implName;
        this.implPackage = implPackage;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getImplPackage() {
        return implPackage;
    }

    public String getImplName() {
        return implName;
    }

    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName(String superTypeName) {
        this.superTypeName = superTypeName;
    }

    public List<JParameter> getCtorParameters() {
        return ctorParameters;
    }

    public void setCtorParameters(List<JParameter> ctorParameters) {
        this.ctorParameters = ctorParameters;
    }
}
