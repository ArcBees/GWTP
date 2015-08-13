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

import javax.ws.rs.HeaderParam;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

@AutoService(HttpParamValueResolver.class)
public class HeaderParamValueResolver extends HttpParamValueResolver {
    @Override
    public Class<HeaderParam> getAssociatedClass() {
        return HeaderParam.class;
    }

    @Override
    public Type getAssociatedType() {
        return Type.HEADER;
    }
}
