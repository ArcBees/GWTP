/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.mvp.processors.proxy;

import java.util.Arrays;
import java.util.List;

import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

public class ProxyProcessors {
    private static final String NO_PROCESSORS_FOUND = "Can not find a processor for proxy `%s`.";

    private List<ProxyProcessor> processors;
    private boolean processedLast;
    private boolean initialized;

    private final Logger logger;
    private final Utils utils;
    private final Outputter outputter;
    private final ProxyModules proxyModules;

    public ProxyProcessors(
            Logger logger,
            Utils utils,
            Outputter outputter,
            ProxyModules proxyModules) {
        this.logger = logger;
        this.utils = utils;
        this.outputter = outputter;
        this.proxyModules = proxyModules;

        if (processors == null) {
            processors = Arrays.<ProxyProcessor>asList(
                    new ProxyPlaceProcessor(),
                    new SimpleProxyProcessor());
        }
    }

    public void process(ProxyDetails proxyDetails) {
        ensureInitialized();
        boolean processed = false;

        for (ProxyProcessor processor : processors) {
            if (processor.canProcess(proxyDetails)) {

                processor.process(proxyDetails);
                processed = true;
            }
        }

        if (!processed) {
            logger.error(NO_PROCESSORS_FOUND, proxyDetails.getProxyType());
        }
    }

    public void processLast() {
        ensureInitialized();

        if (!processedLast) {
            processedLast = true;

            for (ProxyProcessor processor : processors) {
                processor.processLast();
            }
        }
    }

    private void ensureInitialized() {
        if (!initialized) {
            for (ProxyProcessor processor : processors) {
                processor.init(logger, utils, outputter);
                processor.setProxyModules(proxyModules);
            }

            initialized = true;
        }
    }
}
