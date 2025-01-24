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

package com.gwtplatform.dispatch.rest.processors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;

import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;

public class NameUtils {
    private static final String REST_MODULE_NAME = "GeneratedRestModule";
    private static final String JACKSON_MAPPER_PROVIDER_NAME = "GeneratedJacksonMapperProvider";

    public static String parentName(VariableElement element) {
        return element.getEnclosingElement().accept(new SimpleElementVisitor8<String, Void>("") {
            @Override
            public String visitExecutable(ExecutableElement parent, Void v) {
                return qualifiedMethodName(parent);
            }

            @Override
            public String visitType(TypeElement parent, Void v) {
                return parent.getQualifiedName().toString();
            }
        }, null);
    }

    public static String qualifiedMethodName(ExecutableElement element) {
        Name className = asType(element.getEnclosingElement()).getQualifiedName();
        Name methodName = element.getSimpleName();

        return className + "#" + methodName;
    }

    public static String qualifiedMethodName(Type parent, ExecutableElement element) {
        return parent.getQualifiedName() + "#" + element.getSimpleName();
    }

    public static String qualifiedMethodName(ResourceMethod method) {
        return method.getParentResource().getType() + "#" + method.getMethod().getName();
    }

    public static Type findRestModuleType(Utils utils) {
        return new Type(utils.getSourceFilter().getApplicationPackage(),
                REST_MODULE_NAME + "$$" + utils.getRoundNumber());
    }

    public static Type findJacksonMapperProviderType(Utils utils) {
        return new Type(utils.getSourceFilter().getApplicationPackage(), JACKSON_MAPPER_PROVIDER_NAME);
    }
}
