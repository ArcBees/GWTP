package com.gwtplatform.carstore.server.dispatch;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.gwtplatform.carstore.server.DevBootStrapper;
import com.gwtplatform.carstore.server.authentication.AuthenticationException;
import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.dispatch.ActionType;
import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class LogInHandler extends AbstractActionHandler<LogInAction, LogInResult> {
    private final Authenticator authenticator;
    private final UserSessionDao loginCookieDao;
    private boolean isLoggedIn;
    private Logger logger;

    @Inject
    public LogInHandler(Logger logger, Authenticator authenticator, UserSessionDao loginCookieDao,
            DevBootStrapper bootStrapper) {
        super(LogInAction.class);

        this.logger = logger;
        this.authenticator = authenticator;
        this.loginCookieDao = loginCookieDao;
    }

    @Override
    public LogInResult execute(LogInAction action, ExecutionContext context) throws ActionException {
        UserDto userDto = null;
        isLoggedIn = true;

        if (action.getActionType() == ActionType.VIA_COOKIE) {
            userDto = getUserFromCookie(action.getLoggedInCookie());
        } else {
            userDto = getUserFromCredentials(action.getUsername(), action.getPassword());
        }

        CurrentUserDto currentUserDto = new CurrentUserDto(isLoggedIn, userDto);

        String loggedInCookie = "";
        if (isLoggedIn) {
            loggedInCookie = loginCookieDao.createLoggedInCookie(userDto);
        }

        logger.info("LogInHandlerexecut(): actiontype=" + getActionType());
        logger.info("LogInHandlerexecut(): currentUserDto=" + currentUserDto);
        logger.info("LogInHandlerexecut(): loggedInCookie=" + loggedInCookie);

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
