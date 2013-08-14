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

package com.gwtplatform.dispatch.rebind.type;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rebind.Logger;
import com.gwtplatform.dispatch.rebind.util.GeneratorUtil;
import com.gwtplatform.dispatch.shared.rest.RestService;

public class ServiceDefinitions {
    private static final String SERVICE_INTERFACE = RestService.class.getName();

    private final TypeOracle typeOracle;
    private final List<JClassType> services = new ArrayList<JClassType>();
    private final Logger logger;
    private final GeneratorUtil generatorUtil;

    @Inject
    public ServiceDefinitions(Logger logger,
                              TypeOracle typeOracle,
                              GeneratorUtil generatorUtil)
            throws UnableToCompleteException {
        this.logger = logger;
        this.typeOracle = typeOracle;
        this.generatorUtil = generatorUtil;

        findAllServices();
    }

    public List<JClassType> getServices() {
        return services;
    }

    private void findAllServices() throws UnableToCompleteException {
        JClassType serviceInterface = generatorUtil.getType(SERVICE_INTERFACE);

        for (JClassType type : typeOracle.getTypes()) {
            if (!SERVICE_INTERFACE.equals(type.getQualifiedSourceName()) && type.isAssignableTo(serviceInterface)) {
                verifyIsInterface(type);
                services.add(type);
            }
        }
    }

    private void verifyIsInterface(JClassType type) throws UnableToCompleteException {
        if (type.isInterface() == null) {
            String typeName = type.getQualifiedSourceName();
            logger.die(typeName + " must be an interface.");
        }
    }
}
