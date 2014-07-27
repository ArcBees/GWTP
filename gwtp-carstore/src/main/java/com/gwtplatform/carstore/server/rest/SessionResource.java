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

package com.gwtplatform.carstore.server.rest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;

@Path(ResourcesPath.SESSION)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {
    private final Authenticator authenticator;
    private final CurrentUserDtoProvider currentUserDtoProvider;

    @Inject
    SessionResource(Authenticator authenticator,
                    CurrentUserDtoProvider currentUserDtoProvider) {
        this.authenticator = authenticator;
        this.currentUserDtoProvider = currentUserDtoProvider;
    }

    @GET
    public Response getCurrentUser() {
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();

        return Response.ok(currentUserDto).build();
    }

    @DELETE
    public Response logout() {
        authenticator.logout();

        return Response.ok().build();
    }
}
