package com.gwtplatform.carstore.server.rest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.gwtplatform.carstore.server.authentication.AuthenticationException;
import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.LogInRequest;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.carstore.shared.dto.ActionType;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.dispatch.shared.NoResult;

@Path(ResourcesPath.USER)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final Authenticator authenticator;
    private final CurrentUserDtoProvider currentUserDtoProvider;
    private final UserSessionDao loginCookieDao;

    private boolean isLoggedIn;
    private Logger logger;

    @Inject
    public UserResource(
            Logger logger,
            Authenticator authenticator,
            CurrentUserDtoProvider currentUserDtoProvider,
            UserSessionDao userSessionDao) {
        this.logger = logger;
        this.authenticator = authenticator;
        this.currentUserDtoProvider = currentUserDtoProvider;
        this.loginCookieDao = userSessionDao;
    }

    @Path(ResourcesPath.CURRENT)
    @GET
    public GetResult<CurrentUserDto> getCurrentUser() {
        return new GetResult<CurrentUserDto>(currentUserDtoProvider.get());
    }

    @Path(ResourcesPath.LOGOUT)
    @GET
    public NoResult logout() {
        authenticator.logout();
        return new NoResult();
    }

    @Path(ResourcesPath.LOGIN)
    @POST
    public LogInResult login(LogInRequest action) {
        UserDto userDto;
        isLoggedIn = true;

        if (action.getActionType() == ActionType.VIA_COOKIE) {
            userDto = getUserFromCookie(action.getLoggedInCookie());
        } else {
            userDto = getUserFromCredentials(action.getUsername(), action.getPassword());
        }

        CurrentUserDto currentUserDto = new CurrentUserDto(isLoggedIn, userDto);

        String loggedInCookie = "";
        if (isLoggedIn) {
            loggedInCookie = loginCookieDao.createSessionCookie(userDto);
        }

        logger.info("Login: actiontype=" + action.getActionType());
        logger.info("Login: currentUserDto=" + currentUserDto);
        logger.info("Login: loggedInCookie=" + loggedInCookie);

        return new LogInResult(action.getActionType(), currentUserDto, loggedInCookie);
    }

    private UserDto getUserFromCookie(String loggedInCookie) {
        UserDto userDto = null;
        try {
            userDto = authenticator.authenticatCookie(loggedInCookie);
        } catch (AuthenticationException e) {
            isLoggedIn = false;
        }

        return userDto;
    }

    private UserDto getUserFromCredentials(String username, String password) {
        UserDto userDto = null;
        try {
            userDto = authenticator.authenticateCredentials(username, password);
        } catch (AuthenticationException e) {
            isLoggedIn = false;
        }

        return userDto;
    }
}
