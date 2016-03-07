/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.rebind.velocity;

import java.io.PrintWriter;

import javax.inject.Inject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.common.rebind.Logger;

public class GeneratorUtil {
    private final Logger logger;
    private final GeneratorContext generatorContext;

    @Inject
    public GeneratorUtil(Logger logger, GeneratorContext generatorContext) {
        this.logger = logger;
        this.generatorContext = generatorContext;
    }

    public void closeDefinition(PrintWriter printWriter) {
        generatorContext.commit(logger.getTreeLogger(), printWriter);
    }

    public PrintWriter tryCreatePrintWriter(String packageName, String className)
            throws UnableToCompleteException {
        return generatorContext.tryCreate(logger.getTreeLogger(), packageName, className);
    }
}
