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

package com.gwtplatform.mvp.processors.bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.processors.tools.domain.Type;

public class NamedProviderBundleProcessor extends AbstractContextProcessor<BundleDetails, BundleDetails> {
    private static final String TEMPLATE = "com/gwtplatform/mvp/processors/bundle/ProviderBundle.vm";

    private final Map<Type, List<Type>> bundleContents = new HashMap<>();
    private final Map<Type, String> bundleNames = new HashMap<>();

    @Override
    public BundleDetails process(BundleDetails bundle) {
        if (bundle.isValid() && !bundle.isManualBundle()) {
            List<Type> bundleContent = getOrInitializeBundleContent(bundle);

            bundleContent.add(bundle.getTargetType());
            bundleNames.put(bundle.getBundleType(), bundle.getBundleName());
        }

        return bundle;
    }

    private List<Type> getOrInitializeBundleContent(BundleDetails bundle) {
        Type bundleType = bundle.getBundleType();

        if (!bundleContents.containsKey(bundleType)) {
            return initializeBundle(bundleType);
        } else {
            return bundleContents.get(bundleType);
        }
    }

    private List<Type> initializeBundle(Type bundleType) {
        List<Type> bundlePresenters = new ArrayList<>();
        bundleContents.put(bundleType, bundlePresenters);

        return bundlePresenters;
    }

    public void flush() {
        if (!bundleContents.isEmpty()) {
            bundleContents.keySet().forEach(this::outputBundle);

            bundleContents.clear();
            bundleNames.clear();
        }
    }

    private void outputBundle(Type bundleType) {
        logger.debug("Generating provider bundle `%s`.", bundleType);

        Type roundBundleType = new Type(
                bundleType.getPackageName(),
                appendRoundNumber(bundleType.getSimpleName()),
                bundleType.getTypeArguments());

        outputter.configure(TEMPLATE)
                .withParam("providerTypes", bundleContents.get(bundleType))
                .withParam("name", bundleNames.get(bundleType))
                .writeTo(roundBundleType);
    }

    private String appendRoundNumber(String className) {
        int roundNumber = utils.getRoundNumber();
        if (roundNumber > 1) {
            return className + "$$" + roundNumber;
        }

        return className;
    }
}
