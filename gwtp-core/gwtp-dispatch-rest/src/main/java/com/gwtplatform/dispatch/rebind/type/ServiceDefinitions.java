/*
 * Copyright (c) 2012 by Zafin Labs, All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of Zafin Labs ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.gwtplatform.dispatch.rebind.type;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rebind.GeneratorUtil;
import com.gwtplatform.dispatch.rebind.Logger;
import com.gwtplatform.dispatch.shared.rest.RestService;

public class ServiceDefinitions {
    private Logger logger;
    private final TypeOracle typeOracle;
    private GeneratorUtil generatorUtil;
    private final List<JClassType> services = new ArrayList<JClassType>();

    @Inject
    public ServiceDefinitions(Logger logger, TypeOracle typeOracle, GeneratorUtil generatorUtil)
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
        JClassType serviceInterface = generatorUtil.getType(RestService.class.getName());

        for (JClassType type : typeOracle.getTypes()) {
            if (type.isAssignableTo(serviceInterface)) {
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
