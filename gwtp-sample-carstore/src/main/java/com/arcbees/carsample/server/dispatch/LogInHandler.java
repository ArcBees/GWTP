package com.arcbees.carsample.server.dispatch;

import java.util.logging.Logger;

import com.arcbees.carsample.server.authentication.AuthenticationException;
import com.arcbees.carsample.server.authentication.Authenticator;
import com.arcbees.carsample.server.dao.UserSessionDao;
import com.arcbees.carsample.shared.dispatch.ActionType;
import com.arcbees.carsample.shared.dispatch.LogInAction;
import com.arcbees.carsample.shared.dispatch.LogInResult;
import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.dto.CurrentUserDto;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

@Transactional
public class LogInHandler extends AbstractActionHandler<LogInAction, LogInResult> {
    private final Authenticator authenticator;
    private final UserSessionDao loginCookieDao;
    private boolean isLoggedIn;
    private Logger logger;

    @Inject
    public LogInHandler(final Logger logger, final Authenticator authenticator, final UserSessionDao loginCookieDao) {
        super(LogInAction.class);
        
        this.logger = logger;
        this.authenticator = authenticator;
        this.loginCookieDao = loginCookieDao;
    }

    @Override
    public LogInResult execute(LogInAction action, ExecutionContext context) throws ActionException {
        User user = null;
        isLoggedIn = true;

        if (action.getActionType() == ActionType.VIA_COOKIE) {
            user = getUserFromCookie(action.getLoggedInCookie());
        } else {
            user = getUserFromCredentials(action.getUsername(), action.getPassword());
        }

        CurrentUserDto currentUserDto = new CurrentUserDto(isLoggedIn, user);

        String loggedInCookie = "";
        if (isLoggedIn) {
            loggedInCookie = loginCookieDao.createLoggedInCookie(user);
        }
        
        logger.info("LogInHandlerexecut(): actiontype=" + getActionType());
        logger.info("LogInHandlerexecut(): currentUserDto=" + currentUserDto);
        logger.info("LogInHandlerexecut(): loggedInCookie=" + loggedInCookie);

        return new LogInResult(action.getActionType(), currentUserDto, loggedInCookie);
    }

    private User getUserFromCookie(String loggedInCookie) {
        User user = null;
        try {
            user = authenticator.authenticatCookie(loggedInCookie);
        } catch (AuthenticationException e) {
            isLoggedIn = false;
        }

        return user;
    }

    private User getUserFromCredentials(String username, String password) {
        User user = null;
        try {
            user = authenticator.authenticateCredentials(username, password);
        } catch (AuthenticationException e) {
            isLoggedIn = false;
        }

        return user;
    }
}
