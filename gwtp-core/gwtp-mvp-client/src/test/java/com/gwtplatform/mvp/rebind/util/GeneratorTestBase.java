/**
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

package com.gwtplatform.mvp.rebind.util;

import com.google.common.collect.Sets;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.StaticPropertyOracle;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompilationStateBuilder;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.UnitTestTreeLogger;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneratorTestBase {
    protected class PropertyOracleBuilder {
        private final List<ConfigurationProperty> props;

        public PropertyOracleBuilder() {
            this.props = new ArrayList<ConfigurationProperty>();
        }

        public PropertyOracleBuilder with(String name, boolean multiValued, String... value) {
            final ConfigurationProperty property = new ConfigurationProperty(name, multiValued);
            if (value.length > 0) {
                property.setValue(value[0]);
            }

            if (multiValued && value.length > 1) {
                for (int i = 1; i < value.length; i++) {
                    property.addValue(value[i]);
                }
            }
            props.add(property);
            return this;
        }

        public PropertyOracle build() {
            return new StaticPropertyOracle(new BindingProperty[]{}, new String[]{},
                    props.toArray(new ConfigurationProperty[props.size()]));
        }
    }

    private static TreeLogger createCompileLogger() {
        PrintWriterTreeLogger logger = new PrintWriterTreeLogger(new PrintWriter(System.err, true));
        logger.setMaxDetail(TreeLogger.ERROR);
        return logger;
    }

    protected StandardGeneratorContext createGeneratorContext(PropertyOracle propOracle, Set<Resource> resources) {
        Set<Resource> res = Sets.<Resource>newHashSet(JavaResourceBase.getStandardResources());
        res.addAll(resources);
        StandardGeneratorContext ctx = new StandardGeneratorContext(buildCompilationState(res), null, null, null, false);
        ctx.setPropertyOracle(propOracle);
        return ctx;
    }

    private CompilationState buildCompilationState(Set<Resource> resources) {
        return new CompilationStateBuilder().doBuildFrom(createCompileLogger(), resources, null, false);
    }

    protected UnitTestTreeLogger createDefaultUnitTestTreeLogger() {
        return new UnitTestTreeLogger.Builder().createLogger();
    }

    protected UnitTestTreeLogger.Builder createUnitTestTreeLoggerBuilder() {
        return new UnitTestTreeLogger.Builder();
    }

    protected PropertyOracleBuilder createPropertyOracleBuilder() {
        return new PropertyOracleBuilder();
    }
    
    protected Set<Resource> gwtpResourcesWith(Resource... resources) {
        final Set<Resource> res = Sets.newHashSet(GwtpResourceBase.getResources());
        res.addAll(Sets.newHashSet(resources));
        return res;
    }
}
