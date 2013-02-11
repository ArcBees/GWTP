package com.gwtplatform.samples.basic.shared.dispatch;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path("text")
public interface TextService extends RestService {
    @GET
    Action<BigResult> getTexts();

    @GET
    @Path("{uid}")
    Action<SmallResult> getText(@PathParam("uid") long id);

    @POST
    Action<SmallResult> createText(String body);
}
