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
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;

import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isType;
import static com.gwtplatform.dispatch.rest.processors.NameUtils.parentName;

@AutoService(HttpParamValueResolver.class)
public class CookieParamValueResolver extends HttpParamValueResolver {
    private static final String COOKIE_BAD_RETURN_TYPE = "Method `%s` parameter's `%s` is annotated with @CookieParam "
            + "but has a bad return type. It may only be a primitive, `Cookie` or a `Collection<Cookie>`.";
    private static final SimpleTypeVisitor8<Boolean, CookieParamValueResolver> VALID_COOKIE_TYPE_VISITOR =
            new SimpleTypeVisitor8<Boolean, CookieParamValueResolver>(false) {
                @Override
                public Boolean visitPrimitive(PrimitiveType type, CookieParamValueResolver resolver) {
                    return true;
                }

                @Override
                public Boolean visitDeclared(DeclaredType type, CookieParamValueResolver resolver) {
                    return resolver.isValidCookieType(type);
                }
            };

    @Override
    public Class<CookieParam> getAssociatedClass() {
        return CookieParam.class;
    }

    @Override
    public Type getAssociatedType() {
        return Type.COOKIE;
    }

    @Override
    public String resolve(VariableElement element) {
        if (isPresent(element) && !element.asType().accept(VALID_COOKIE_TYPE_VISITOR, this)) {
            logger.error().context(element).log(COOKIE_BAD_RETURN_TYPE, parentName(element), element.getSimpleName());
            throw new UnableToProcessException();
        }

        return super.resolve(element);
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
            PrimitiveType primitiveType = utils.getTypes().unboxedType(type);
            return primitiveType != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isCookieCollection(DeclaredType type) {
        TypeMirror collectionType = utils.createWithWildcard(Collection.class);
        Optional<TypeMirror> typeArg = extractCollectionTypeArg(type);

        return utils.getTypes().isAssignable(type, collectionType)
                && typeArg.isPresent()
                && isType(typeArg.get())
                && asTypeElement(typeArg.get()).getQualifiedName().contentEquals(Cookie.class.getCanonicalName());
    }

    private Optional<TypeMirror> extractCollectionTypeArg(DeclaredType type) {
        List<? extends TypeMirror> typeArguments = type.getTypeArguments();

        return Optional.fromNullable(typeArguments.size() == 1 ? typeArguments.get(0) : null);
    }
}
