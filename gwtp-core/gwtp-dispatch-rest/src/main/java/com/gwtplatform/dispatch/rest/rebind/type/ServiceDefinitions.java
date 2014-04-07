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

package com.gwtplatform.dispatch.rest.rebind.type;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.rebind.Logger;

public class ServiceDefinitions {
    private final TypeOracle typeOracle;
    private final List<JClassType> services = Lists.newArrayList();
    private final Logger logger;

    @Inject
    ServiceDefinitions(
            Logger logger,
            TypeOracle typeOracle)
            throws UnableToCompleteException {
        this.logger = logger;
        this.typeOracle = typeOracle;

        findAllServices();
    }

    public List<JClassType> getServices() {
        return services;
    }

    public boolean isService(JClassType type) throws UnableToCompleteException {
        boolean isService = type.isAnnotationPresent(Path.class);

        if (isService) {
            ensureIsInterface(type);
        }

        return isService;
    }

    private void ensureIsInterface(JClassType type) throws UnableToCompleteException {
        if (type.isInterface() == null) {
            String typeName = type.getQualifiedSourceName();
            logger.die(typeName + " must be an interface.");
        }
    }

    private void findAllServices() throws UnableToCompleteException {
        for (JClassType type : typeOracle.getTypes()) {
            if (isService(type)) {
                services.add(type);
            }
        }
    }
}
