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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.tools.FileObject;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.common.client.NamedProviderBundle;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.HasType;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;

class ProviderBundleAggregate implements HasType, HasImports {
    private static final String TEMPLATE = "com/gwtplatform/common/processors/bundles/NamedProviderBundleAggregate.vm";
    private static final String PACKAGE_NAME = NamedProviderBundle.class.getCanonicalName() + ".generated";

    private final Logger logger;
    private final Outputter outputter;
    private final Type type;
    private final FileObject file;
    private final List<Type> content;

    ProviderBundleAggregate(
            Logger logger,
            Outputter outputter,
            String bundleName) {
        this.logger = logger;
        this.outputter = outputter;
        this.type = new Type(PACKAGE_NAME, bundleName + "ProviderBundleAggregate");
        this.file = outputter.prepareSourceFile(type);
        this.content = new ArrayList<>();
    }

    public void addContent(Type... types) {
        Collections.addAll(content, types);
    }

    public void write() {
        logger.debug("Generating named provider bundle aggregate `%s`.", type);

        outputter.configure(TEMPLATE)
                .withParam("bundles", content)
                .writeTo(type, file);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(content)
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .toList();
    }
}
