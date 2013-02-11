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
import com.gwtplatform.dispatch.shared.Result;

public class SerializerDefinitions {
    private final TypeOracle typeOracle;
    private GeneratorUtil generatorUtil;
    private final List<JClassType> results = new ArrayList<JClassType>();

    @Inject
    public SerializerDefinitions(TypeOracle typeOracle, GeneratorUtil generatorUtil) throws UnableToCompleteException {
        this.typeOracle = typeOracle;
        this.generatorUtil = generatorUtil;

        findAllNeededSerializers();
    }

    private void findAllNeededSerializers() throws UnableToCompleteException {
        JClassType result = generatorUtil.getType(Result.class.getName());
        for (JClassType type : typeOracle.getTypes()) {
            if (type.isAssignableTo(result)) {
                results.add(type);
            }
        }
    }

    public List<JClassType> getResults() {
        return results;
    }
}
