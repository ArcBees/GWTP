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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.fieldsIn;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isTypeOf;

public abstract class AbstractProxyDetails implements ProxyDetails {
    protected final TypeElement element;
    protected final Logger logger;
    protected final Utils utils;
    protected final TypeMirror proxyMirror;

    private Type type;
    private TypeMirror presenterMirror;
    private Set<String> contentSlots;

    protected AbstractProxyDetails(
            Logger logger,
            Utils utils,
            TypeElement element,
            TypeMirror proxyMirror) {
        this.logger = logger;
        this.utils = utils;
        this.element = element;
        this.proxyMirror = proxyMirror;
    }

    @Override
    public Type getType() {
        if (type == null) {
            String packageName = new Type(asType(element)).getPackageName();
            String presenterName = getPresenterType().getSimpleName();
            String proxyName = element.getSimpleName().toString();

            type = new Type(packageName, String.format("%s$$%s", presenterName, proxyName));
        }

        return type;
    }

    @Override
    public Type getProxyType() {
        return new Type(element.asType());
    }

    @Override
    public Type getPresenterType() {
        return new Type(getPresenterMirror());
    }

    private TypeMirror getPresenterMirror() {
        if (presenterMirror == null) {
            List<? extends TypeMirror> proxyGenericTypes = asDeclared(proxyMirror).getTypeArguments();
            if (proxyGenericTypes.isEmpty()) {
                logger.error().context(element).log("This proxy must specify its presenter.");
                throw new UnableToProcessException();
            }

            presenterMirror = proxyGenericTypes.get(0);
        }

        return presenterMirror;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable
                .from(getProxyType().getImports())
                .append(getPresenterType().getImports())
                .toList();
    }

    @Override
    public Set<String> getContentSlots() {
        if (contentSlots == null) {
            TypeElement presenterElement = asTypeElement(getPresenterMirror());
            List<Element> presenterMembers = utils.getAllMembers(presenterElement);
            List<VariableElement> presenterFields = fieldsIn(presenterMembers);
            contentSlots = new HashSet<>();

            for (VariableElement field : presenterFields) {
                if (isValidContentSlot(field)) {
                    contentSlots.add(field.getSimpleName().toString());
                }
            }
        }

        return contentSlots;
    }

    private boolean isValidContentSlot(VariableElement field) {
        if (isTypeOf(NestedSlot.class, field.asType())) {
            if (!hasModifiers(Modifier.PRIVATE, Modifier.STATIC).apply(field)) {
                return true;
            }

            logger.mandatoryWarning()
                    .context(field)
                    .log("Content slots cannot be static or private.");
        }

        return false;
    }
}
