package com.gwtplatform.carstore.server.authentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

public class Authenticator {
    private final UserDao userDao;
    private final Provider<HttpSession> sessionProvider;
    private final PasswordSecurity passwordSecurity;
    private final CurrentUserDtoProvider currentUserDtoProvider;
    private final UserSessionDao loginCookieDao;

    @Inject
    public Authenticator(final UserDao userDao, final Provider<HttpSession> sessionProvider,
            final PasswordSecurity passwordSecurity, final CurrentUserDtoProvider currentUserDtoProvider,
            final UserSessionDao loginCookieDao) {
        this.userDao = userDao;
        this.sessionProvider = sessionProvider;
        this.passwordSecurity = passwordSecurity;
        this.currentUserDtoProvider = currentUserDtoProvider;
        this.loginCookieDao = loginCookieDao;
    }

    public UserDto authenticateCredentials(final String username, final String password) {
        try {
            UserDto userDto = userDao.findByUsername(username);

            if (passwordSecurity.check(password, userDto.getHashPassword())) {
                login(userDto);
                return userDto;
            } else {
                throw new AuthenticationException();
            }
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    public UserDto authenticatCookie(String loggedInCookie) throws AuthenticationException {
        UserDto userDto = loginCookieDao.getUserFromCookie(loggedInCookie);

        if (userDto == null) {
            throw new AuthenticationException();
        } else {
            login(userDto);
        }

        return userDto;
    }

    public void logout() {
        removeCurrentUserLoginCookie();

        HttpSession httpSession = sessionProvider.get();
        httpSession.invalidate();
    }

    private void login(UserDto userDto) {
        HttpSession session = sessionProvider.get();
        session.setAttribute(SecurityParameters.getUserSessionKey(), userDto.getId());
    }

    public Boolean isUserLoggedIn() {
        HttpSession session = sessionProvider.get();
        Integer userId = (Integer) session.getAttribute(SecurityParameters.getUserSessionKey());

        return userId != null;
    }

    private void removeCurrentUserLoginCookie() {
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();
        UserDto userDto = currentUserDto.getUser();
        loginCookieDao.removeLoggedInCookie(userDto);
    }
}
