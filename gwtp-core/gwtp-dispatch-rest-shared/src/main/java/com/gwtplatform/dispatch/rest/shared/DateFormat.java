/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify a date format pattern.
 * <p>
 * This annotation may be put on {@link java.util.Date Date} parameters annotated with
 * {@link javax.ws.rs.FormParam @FormParam}, {@link javax.ws.rs.QueryParam @QueryParam},
 * {@link javax.ws.rs.PathParam @PathParam} or {@link javax.ws.rs.HeaderParam @HeaderParam}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DateFormat {
    /**
     * <a href="http://www.iso.org/iso/support/faqs/faqs_widely_used_standards/widely_used_standards_other/iso8601">
     * ISO 8601</a> date format.
     * <p>
     * Example: {@code 2014-02-25T10:59:26.046-05:00}
     */
    String DEFAULT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ";

    /**
     * The formatting pattern to use for parsing the date. The pattern must follow the rules defined by
     * {@link com.google.gwt.i18n.shared.DateTimeFormat DateTimeFormat}.
     * <p>
     * If not specified, {@link #DEFAULT} will be used.
     */
    String value() default DEFAULT;
}
