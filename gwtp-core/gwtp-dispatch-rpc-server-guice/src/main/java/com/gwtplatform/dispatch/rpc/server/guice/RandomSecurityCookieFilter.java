/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.server.guice;

import com.gwtplatform.dispatch.rpc.server.AbstractRandomSecurityCookieFilter;
import com.gwtplatform.dispatch.shared.SecurityCookie;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RandomSecurityCookieFilter extends AbstractRandomSecurityCookieFilter {

    @Inject
    RandomSecurityCookieFilter(@SecurityCookie String securityCookieName, SecureRandomSingleton random) {
        super(securityCookieName, random);
    }
}
