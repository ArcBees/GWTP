package com.gwtplatform.carstore.server.authentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;

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

    public User authenticateCredentials(final String username, final String password) {
        try {
            User user = userDao.findByUsername(username);

            if (passwordSecurity.check(password, user.getHashPassword())) {
                login(user);
                return user;
            } else {
                throw new AuthenticationException();
            }
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    public User authenticatCookie(String loggedInCookie) throws AuthenticationException {
        User user = loginCookieDao.getUserFromCookie(loggedInCookie);

        if (user == null) {
            throw new AuthenticationException();
        } else {
            login(user);
        }

        return user;
    }

    public void logout() {
        removeCurrentUserLoginCookie();

        HttpSession httpSession = sessionProvider.get();
        httpSession.invalidate();
    }

    private void login(User user) {
        HttpSession session = sessionProvider.get();
        session.setAttribute(SecurityParameters.getUserSessionKey(), user.getId());
    }

    public Boolean isUserLoggedIn() {
        HttpSession session = sessionProvider.get();
        Integer userId = (Integer) session.getAttribute(SecurityParameters.getUserSessionKey());

        return userId != null;
    }

    private void removeCurrentUserLoginCookie() {
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();
        User user = currentUserDto.getUser();
        loginCookieDao.removeLoggedInCookie(user);
    }
}
