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

package com.gwtplatform.dispatch.rest.processors.resolvers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.SimpleTypeVisitor8;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.shared.DateFormat;
import com.gwtplatform.processors.tools.logger.Logger;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.isAnnotationPresent;
import static com.gwtplatform.dispatch.rest.processors.NameUtils.parentName;

public class DateFormatResolver {
    private static final String DATE_FORMAT_NOT_DATE =
            "Method `%s` parameter's `%s` is annotated with @DateFormat but its type is not Date, LocalDate "
            + "or LocalDateTime.";
    private static final SimpleTypeVisitor8<Boolean, Void> DATE_TYPE_VALIDATION_VISITOR =
            new SimpleTypeVisitor8<Boolean, Void>(false) {
                @Override
                public Boolean visitDeclared(DeclaredType type, Void v) {
                    final Name typeName = asType(type.asElement()).getQualifiedName();
                    return typeName.contentEquals(Date.class.getCanonicalName())
                            || typeName.contentEquals(LocalDate.class.getCanonicalName())
                            || typeName.contentEquals(LocalDateTime.class.getCanonicalName());
                }
            };

    private final Logger logger;

    public DateFormatResolver(Logger logger) {
        this.logger = logger;
    }

    public Optional<String> resolve(VariableElement element) {
        Optional<String> dateFormat = Optional.absent();

        if (isAnnotationPresent(element, DateFormat.class)) {
            if (element.asType().accept(DATE_TYPE_VALIDATION_VISITOR, null)) {
                dateFormat = Optional.of(element.getAnnotation(DateFormat.class).value());
            } else {
                logger.warning().context(element)
                        .log(DATE_FORMAT_NOT_DATE, parentName(element), element.getSimpleName());
            }
        }

        return dateFormat;
    }
}
