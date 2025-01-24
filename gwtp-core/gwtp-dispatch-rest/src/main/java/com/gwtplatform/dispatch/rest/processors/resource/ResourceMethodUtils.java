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

package com.gwtplatform.dispatch.rest.processors.resource;

import java.util.Collection;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleTypeVisitor8;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.processors.tools.domain.Method;

import static com.google.auto.common.MoreTypes.asTypeElement;

public class ResourceMethodUtils {
    private static final TypeVisitor<Boolean, Void> IS_REST_ACTION_VISITOR =
            new SimpleTypeVisitor8<Boolean, Void>(false) {
                @Override
                public Boolean visitDeclared(DeclaredType type, Void nothing) {
                    Name returnTypeName = asTypeElement(type).getQualifiedName();

                    return returnTypeName.contentEquals(REST_ACTION_FQN);
                }
            };
    private static final String REST_ACTION_FQN = RestAction.class.getCanonicalName();

    public Method processMethod(Resource parentResource, ExecutableElement element) {
        Collection<HttpVariable> variables = parentResource.getEndPointDetails().getHttpVariables();
        Collection<String> existingVariableNames = FluentIterable.from(variables)
                .transform(HttpVariable::getName)
                .toList();

        return new Method(element, existingVariableNames);
    }

    public boolean returnsRestAction(ExecutableElement element) {
        return element.getReturnType().accept(IS_REST_ACTION_VISITOR, null);
    }
}
