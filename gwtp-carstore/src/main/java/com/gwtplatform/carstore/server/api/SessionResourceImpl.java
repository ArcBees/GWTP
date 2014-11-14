/**
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

package com.gwtplatform.carstore.server.api;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.shared.api.SessionResource;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;

public class SessionResourceImpl implements SessionResource {
    private final Authenticator authenticator;
    private final CurrentUserDtoProvider currentUserDtoProvider;

    @Inject
    SessionResourceImpl(
            Authenticator authenticator,
            CurrentUserDtoProvider currentUserDtoProvider) {
        this.authenticator = authenticator;
        this.currentUserDtoProvider = currentUserDtoProvider;
    }

    @Override
    public CurrentUserDto getCurrentUser() {
        return currentUserDtoProvider.get();
    }

    @Override
    public void logout() {
        authenticator.logout();
    }
}
