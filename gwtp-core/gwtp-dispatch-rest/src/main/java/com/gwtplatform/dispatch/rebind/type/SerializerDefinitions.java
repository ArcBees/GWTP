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
import com.gwtplatform.dispatch.rebind.GeneratorUtil;
import com.gwtplatform.dispatch.shared.Result;

/**
 * TODO: Documentation.
 */
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
