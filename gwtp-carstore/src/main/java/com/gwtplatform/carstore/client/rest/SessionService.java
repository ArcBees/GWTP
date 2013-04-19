package com.gwtplatform.carstore.client.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.LogInRequest;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path(ResourcesPath.SESSION)
public interface SessionService extends RestService {
    @DELETE
    Action<NoResult> logout();

    @GET
    Action<GetResult<CurrentUserDto>> getCurrentUser();

    @POST
    Action<LogInResult> login(LogInRequest logInRequest);
}
