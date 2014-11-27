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

package com.gwtplatform.dispatch.rest.rebind.action;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceMethodDefinition;

public class ActionMethodDefinition extends ResourceMethodDefinition {
    private final JClassType resultType;
    private final List<ActionDefinition> actionDefinitions;

    public ActionMethodDefinition(
            JMethod method,
            List<Parameter> parameters,
            List<Parameter> inheritedParameters,
            JClassType resultType) {
        super(method, parameters, inheritedParameters);

        this.resultType = resultType;
        this.actionDefinitions = Lists.newArrayList();
    }

    public JClassType getResultType() {
        return resultType;
    }

    public void addAction(ActionDefinition actionDefinition) {
        actionDefinitions.add(actionDefinition);

        addImport(actionDefinition.getQualifiedName());
    }

    public List<ActionDefinition> getActionDefinitions() {
        return Lists.newArrayList(actionDefinitions);
    }
}
