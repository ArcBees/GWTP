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

package com.gwtplatform.common.processors.bundles;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.common.client.NamedProviderBundleCollection;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.HasType;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;

class ProviderBundleCollection implements HasType, HasImports {
    private static final String TEMPLATE = "com/gwtplatform/common/processors/bundles/NamedProviderBundleCollection.vm";
    private static final Type TYPE = new Type(
            NamedProviderBundleCollection.class.getPackage().getName(),
            "Generated" + NamedProviderBundleCollection.class.getSimpleName());

    private final Logger logger;
    private final Outputter outputter;
    private final FileObject file;
    private final Map<String, Type> bundles;
    private final Map<String, ProviderBundleAggregate> aggregates;

    ProviderBundleCollection(
            Logger logger,
            Outputter outputter) {
        this.logger = logger;
        this.outputter = outputter;
        this.file = outputter.prepareSourceFile(TYPE);
        this.bundles = new HashMap<>();
        this.aggregates = new HashMap<>();
    }

    public void addBundle(String name, Type type) {
        if (aggregates.containsKey(name)) {
            aggregates.get(name).addContent(type);
        } else if (bundles.containsKey(name)) {
            Type otherType = bundles.remove(name);
            ProviderBundleAggregate aggregate = new ProviderBundleAggregate(logger, outputter, name);
            aggregate.addContent(otherType, type);

            aggregates.put(name, aggregate);
            bundles.put(name, aggregate.getType());
        } else {
            bundles.put(name, type);
        }
    }

    public void write() {
        for (ProviderBundleAggregate aggregate : aggregates.values()) {
            aggregate.write();
        }

        logger.debug("Generating named provider bundle collection.");

        outputter.configure(TEMPLATE)
                .withParam("bundles", bundles)
                .withImports(getImports())
                .writeTo(TYPE, file);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(bundles.values())
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .toList();
    }
}
