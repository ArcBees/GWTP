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

import com.gwtplatform.mvp.processors.bundle.BundleDetails;
import com.gwtplatform.mvp.processors.bundle.NamedProviderBundleProcessor;
import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.processors.tools.domain.Type;

public class ProxyPlaceProcessor extends AbstractContextProcessor<ProxyDetails, Type> implements ProxyProcessor {
    private static final String TEMPLATE = "com/gwtplatform/mvp/processors/proxy/ProxyPlace.vm";

    private ProxyModules proxyModules;
    private NamedProviderBundleProcessor providerBundleProcessor;

    @Override
    public void setProxyModules(ProxyModules proxyModules) {
        this.proxyModules = proxyModules;
    }

    @Override
    public void setProviderBundleProcessor(NamedProviderBundleProcessor providerBundleProcessor) {
        this.providerBundleProcessor = providerBundleProcessor;
    }

    @Override
    public boolean canProcess(ProxyDetails proxy) {
        return proxy instanceof ProxyPlaceDetails;
    }

    @Override
    public Type process(ProxyDetails proxy) {
        ProxyPlaceDetails proxyPlace = (ProxyPlaceDetails) proxy;

        logger.debug("Generating proxy place `%s`.", proxyPlace.getProxyType());

        BundleDetails bundleDetails = proxyPlace.getBundleDetails();
        outputter.configure(TEMPLATE)
                .withParam("proxyType", proxyPlace.getProxyType())
                .withParam("presenterType", proxyPlace.getPresenterType())
                .withParam("gatekeeperType", proxyPlace.getGatekeeperType())
                .withParam("gatekeeperParams", proxyPlace.getGatekeeperParams())
                .withParam("slotNames", proxyPlace.getContentSlots())
                .withParam("nameTokens", proxyPlace.getNameTokens())
                .withParam("codeSplit", proxyPlace.isCodeSplit())
                .withParam("proxyEvents", proxy.getProxyEventMethods())
                .withParam("bundle", bundleDetails)
                .writeTo(proxyPlace.getType());

        if (bundleDetails != null) {
            providerBundleProcessor.process(bundleDetails);
        }
        proxyModules.bindProxy(proxyPlace);

        return proxyPlace.getType();
    }
}
