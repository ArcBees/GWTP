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

package com.gwtplatform.dispatch.rebind.velocity;

import javax.inject.Inject;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rebind.velocity.type.ServiceDefinitions;

public class ServiceGenerator {
    private ServiceDefinitions serviceDefinitions;

    @Inject
    public ServiceGenerator(ServiceDefinitions serviceDefinitions) {
        this.serviceDefinitions = serviceDefinitions;
    }

    public void generate() throws Exception {
        for (JClassType service : serviceDefinitions.getServices()) {
            generateActions(service);
        }
    }

    private void generateActions(JClassType service) {
        JMethod[] actionMethods = service.getMethods();
        if (actionMethods != null) {
            for (JMethod actionMethod : actionMethods) {
                generateRestAction(actionMethod);
            }
        }
    }

    private void generateRestAction(JMethod actionMethod) {
        try {
            String actionClassName = new com.gwtplatform.dispatch.rebind.RestActionGenerator(baseRestPath)
                    .generate(getTreeLogger(), getGeneratorContext(), actionMethod);

            actions.put(actionMethod, actionClassName);
        } catch (UnableToCompleteException e) {
            String readableDeclaration = actionMethod.getReadableDeclaration(true, true, true, true, true);
            getTreeLogger().log(TreeLogger.Type.ERROR, "Unable to generate rest action for method " +
                    readableDeclaration + ".");

            throw new UnableToCompleteException();
        }
    }
}
