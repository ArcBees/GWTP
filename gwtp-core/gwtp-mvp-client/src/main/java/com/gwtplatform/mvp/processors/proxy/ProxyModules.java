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

import java.util.HashSet;
import java.util.Set;

import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.bindings.BindingContext.flushModule;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newEagerSingletonBinding;

public class ProxyModules {
    private static final String PROXY_MODULE_NAME = "GeneratedProxyModule";

    private final Utils utils;
    private final BindingsProcessors bindingsProcessors;
    private final Set<Type> modules;

    public ProxyModules(
            Utils utils,
            BindingsProcessors bindingsProcessors) {
        this.utils = utils;
        this.bindingsProcessors = bindingsProcessors;
        this.modules = new HashSet<>();
    }

    public void bindProxy(ProxyDetails proxy) {
        Type module = createModuleType(proxy);
        BindingContext bindingContext = newEagerSingletonBinding(module, proxy.getProxyType(), proxy.getType());

        bindingsProcessors.process(bindingContext);
        modules.add(module);
    }

    private Type createModuleType(ProxyDetails proxy) {
        String packageName = proxy.getType().getPackageName();
        String moduleName = PROXY_MODULE_NAME;

        if (utils.getRoundNumber() > 1) {
            moduleName += "$$" + utils.getRoundNumber();
        }

        return new Type(packageName, moduleName);
    }

    public void flush() {
        for (Type module : modules) {
            bindingsProcessors.process(flushModule(module));
        }

        modules.clear();
    }
}
