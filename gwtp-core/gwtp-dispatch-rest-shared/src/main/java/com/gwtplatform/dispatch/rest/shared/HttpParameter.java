/*
 * Copyright 2013 ArcBees Inc.
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

import java.util.List;
import java.util.Map.Entry;

/**
 * This class is used by {@link RestAction} to associate a parameter name to a value.
 */
public interface HttpParameter {
    enum Type {
        /** Represent {@link javax.ws.rs.CookieParam @CookieParam} parameters. */
        COOKIE,
        /** Represent {@link javax.ws.rs.FormParam @FormParam} parameters. */
        FORM,
        /** Represent {@link javax.ws.rs.HeaderParam @HeaderParam} parameters. */
        HEADER,
        /** Represent {@link javax.ws.rs.MatrixParam @MatrixParam} parameters. */
        MATRIX,
        /** Represent {@link javax.ws.rs.PathParam @PathParam} parameters. */
        PATH,
        /** Represent {@link javax.ws.rs.QueryParam @QueryParam} parameters. */
        QUERY
    }

    Type getType();

    String getName();

    Object getObject();

    List<Entry<String, String>> getEncodedEntries();
}
