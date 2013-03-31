package com.gwtplatform.carstore.server.authentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;

public class CurrentUserDtoProvider implements Provider<CurrentUserDto> {
    private final UserDao userDao;
    private final Provider<HttpSession> sessionProvider;

    @Inject
    public CurrentUserDtoProvider(final UserDao userDao, final Provider<HttpSession> sessionProvider) {
        this.userDao = userDao;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public CurrentUserDto get() {
        HttpSession session = sessionProvider.get();
        Long currentUserId = (Long) session.getAttribute(SecurityParameters.getUserSessionKey());

        User user = null;
        if (currentUserId != null) {
            user = userDao.get(currentUserId);
        }

        boolean isLoggedIn = user != null;

        return new CurrentUserDto(isLoggedIn, user);
    }
}
