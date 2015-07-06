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

package com.gwtplatform.dispatch.rest.processors.resolvers.parameters;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.utils.UnconstrainedTypes;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isType;
import static com.gwtplatform.dispatch.rest.processors.NameFactory.parentName;

@AutoService(HttpParamValueResolver.class)
public class CookieParamValueResolver extends HttpParamValueResolver {
    private static final String COOKIE_BAD_RETURN_TYPE =
            "Method `%s` parameter's `%s` is annotated with @CookieParam but has a bad return type. "
                    + "It may only be a primitive, `Cookie` or a `Collection<Cookie>`.";
    private static final SimpleTypeVisitor6<Boolean, CookieParamValueResolver>
            VALID_COOKIE_TYPE_VISITOR = new SimpleTypeVisitor6<Boolean, CookieParamValueResolver>(false) {
        @Override
        public Boolean visitPrimitive(PrimitiveType type, CookieParamValueResolver resolver) {
            return true;
        }

        @Override
        public Boolean visitDeclared(DeclaredType type, CookieParamValueResolver resolver) {
            return resolver.isValidCookieType(type);
        }
    };

    private UnconstrainedTypes unconstrainedTypes;

    @Override
    protected void init() {
        unconstrainedTypes = new UnconstrainedTypes(types, elements);
    }

    @Override
    public Class<CookieParam> getAssociatedClass() {
        return CookieParam.class;
    }

    @Override
    public Type getAssociatedType() {
        return Type.COOKIE;
    }

    @Override
    public boolean canResolve(VariableElement element) {
        boolean valid = !isPresent(element) ||
                element.asType().accept(VALID_COOKIE_TYPE_VISITOR, this);

        if (!valid) {
            logger.error(COOKIE_BAD_RETURN_TYPE, parentName(element), element.getSimpleName());
        }

        return valid;
    }

    private boolean isValidCookieType(DeclaredType type) {
        Name qualifiedName = asTypeElement(type).getQualifiedName();

        return qualifiedName.contentEquals(Date.class.getCanonicalName())
                || qualifiedName.contentEquals(String.class.getCanonicalName())
                || qualifiedName.contentEquals(Cookie.class.getCanonicalName())
                || isBoxed(type)
                || isCookieCollection(type);
    }

    private boolean isBoxed(DeclaredType type) {
        try {
            PrimitiveType primitiveType = types.unboxedType(type);
            return primitiveType != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isCookieCollection(DeclaredType type) {
        TypeMirror collectionType = unconstrainedTypes.create(Collection.class);
        Optional<TypeMirror> typeArg = extractCollectionTypeArg(type);

        return types.isAssignable(type, collectionType)
                 && typeArg.isPresent()
                 && isType(typeArg.get())
                 && asTypeElement(typeArg.get()).getQualifiedName().contentEquals(Cookie.class.getCanonicalName());
    }

    private Optional<TypeMirror> extractCollectionTypeArg(DeclaredType type) {
        List<? extends TypeMirror> typeArguments = type.getTypeArguments();

        return Optional.fromNullable(typeArguments.size() == 1 ? typeArguments.get(0) : null);
    }
}
