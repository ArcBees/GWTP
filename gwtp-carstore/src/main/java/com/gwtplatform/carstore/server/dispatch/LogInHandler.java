/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.carstore.server.dispatch;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.gwtplatform.carstore.server.authentication.AuthenticationException;
import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.dispatch.ActionType;
import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.AbstractActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

public class LogInHandler extends AbstractActionHandler<LogInAction, LogInResult> {
    private final Authenticator authenticator;
    private final UserSessionDao loginCookieDao;
    private final Logger logger;
    
    private boolean isLoggedIn;

    @Inject
    LogInHandler(Logger logger, Authenticator authenticator, UserSessionDao loginCookieDao) {
        super(LogInAction.class);

        this.logger = logger;
        this.authenticator = authenticator;
        this.loginCookieDao = loginCookieDao;
    }

    @Override
    public LogInResult execute(LogInAction action, ExecutionContext context) throws ActionException {
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

        logger.info("LogInHandlerexecut(): actiontype=" + getActionType());
        logger.info("LogInHandlerexecut(): currentUserDto=" + currentUserDto);
        logger.info("LogInHandlerexecut(): loggedInCookie=" + loggedInCookie);

        return new LogInResult(action.getActionType(), currentUserDto, loggedInCookie);
    }

    @Override
    public void undo(LogInAction action, LogInResult result, ExecutionContext context) throws ActionException {
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
