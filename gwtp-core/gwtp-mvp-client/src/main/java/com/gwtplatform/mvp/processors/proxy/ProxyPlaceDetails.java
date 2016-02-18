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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.collect.Sets;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class ProxyPlaceDetails extends AbstractProxyDetails {
    private Set<String> nameTokens;

    public ProxyPlaceDetails(
            Logger logger,
            Utils utils,
            TypeElement element,
            TypeMirror proxyMirror) {
        super(logger, utils, element, proxyMirror);
    }

    public Set<String> getNameTokens() {
        if (nameTokens == null) {
            NameToken nameTokenAnnotation = element.getAnnotation(NameToken.class);
            nameTokens = Sets.newHashSet(nameTokenAnnotation.value());
        }

        return nameTokens;
    }

    public Type getGatekeeper() {
        // TODO: Handle gatekeeper
        return new Type("");
    }

    public List<String> getGatekeeperParams() {
        // TODO: Handle gatekeeper with params
        return new ArrayList<>();
    }
}
