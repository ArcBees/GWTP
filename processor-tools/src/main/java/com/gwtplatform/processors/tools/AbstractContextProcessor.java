/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.processors.tools;

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

public abstract class AbstractContextProcessor<I, O> implements ContextProcessor<I, O> {
    protected ProcessingEnvironment processingEnv;
    protected Logger logger;
    protected Utils utils;
    protected Outputter outputter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        this.utils = new Utils(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        this.outputter = new Outputter(logger, new Type(getClass()), processingEnv.getFiler(), getMacroFiles());
    }

    protected String[] getMacroFiles() {
        return new String[0];
    }

    @Override
    public boolean canProcess(I context) {
        return true;
    }

    @Override
    public void processLast() {
    }
}
