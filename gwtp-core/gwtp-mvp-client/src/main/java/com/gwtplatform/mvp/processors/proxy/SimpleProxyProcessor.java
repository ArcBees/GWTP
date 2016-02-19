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

import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.processors.tools.domain.Type;

public class SimpleProxyProcessor extends AbstractContextProcessor<ProxyDetails, Type> implements ProxyProcessor {
    private static final String TEMPLATE = "com/gwtplatform/mvp/processors/SimpleProxy.vm";

    private ProxyModules proxyModules;

    @Override
    public void setProxyModules(ProxyModules proxyModules) {
        this.proxyModules = proxyModules;
    }

    @Override
    public boolean canProcess(ProxyDetails proxy) {
        return proxy instanceof SimpleProxyDetails;
    }

    @Override
    public Type process(ProxyDetails proxy) {
        logger.debug("Generating proxy `%s`.", proxy.getProxyType());

        outputter.configure(TEMPLATE)
                .withParam("proxyType", proxy.getProxyType())
                .withParam("presenterType", proxy.getPresenterType())
                .withParam("slotNames", proxy.getContentSlots())
                .withParam("codeSplit", proxy.isCodeSplit())
                .withParam("bundle", proxy.getBundleDetails())
                .writeTo(proxy.getType());

        proxyModules.bindProxy(proxy);

        return proxy.getType();
    }
}
