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

package com.gwtplatform.carstore.server.authentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

public class CurrentUserDtoProvider implements Provider<CurrentUserDto> {
    private final UserDao userDao;
    private final Provider<HttpSession> sessionProvider;

    @Inject
    CurrentUserDtoProvider(UserDao userDao,
                           Provider<HttpSession> sessionProvider) {
        this.userDao = userDao;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public CurrentUserDto get() {
        HttpSession session = sessionProvider.get();
        Long currentUserId = (Long) session.getAttribute(SecurityParameters.getUserSessionKey());

        UserDto userDto = null;
        if (currentUserId != null) {
            userDto = User.createDto(userDao.get(currentUserId));
        }

        boolean isLoggedIn = userDto != null;

        return new CurrentUserDto(isLoggedIn, userDto);
    }
}
