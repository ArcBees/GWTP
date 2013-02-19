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

package com.gwtplatform.dispatch.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

import java.io.PrintWriter;
import javax.inject.Inject;

public class GeneratorUtil {
    private final TypeOracle typeOracle;
    private final Logger logger;
    private final GeneratorContext generatorContext;

    @Inject
    public GeneratorUtil(TypeOracle typeOracle, Logger logger, GeneratorContext generatorContext) {
        this.typeOracle = typeOracle;
        this.logger = logger;
        this.generatorContext = generatorContext;
    }

    public JClassType getType(String typeName) throws UnableToCompleteException {
        try {
            return typeOracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.die("Cannot find " + typeName);
        }

        return null;
    }

    public void closeDefinition(PrintWriter printWriter) {
        generatorContext.commit(logger.getTreeLogger(), printWriter);
    }

    public PrintWriter tryCreatePrintWriter(String packageName, String className)
            throws UnableToCompleteException {
        return generatorContext.tryCreate(logger.getTreeLogger(), packageName, className);
    }
}
