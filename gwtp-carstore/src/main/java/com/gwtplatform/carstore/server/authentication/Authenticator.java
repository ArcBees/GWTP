package com.gwtplatform.carstore.server.authentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.server.dao.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

public class Authenticator {
    private final UserDao userDao;
    private final Provider<HttpSession> sessionProvider;
    private final PasswordSecurity passwordSecurity;
    private final CurrentUserDtoProvider currentUserDtoProvider;
    private final UserSessionDao userSessionDao;

    @Inject
    public Authenticator(final UserDao userDao, final Provider<HttpSession> sessionProvider,
            final PasswordSecurity passwordSecurity, final CurrentUserDtoProvider currentUserDtoProvider,
            final UserSessionDao userSessionDao) {
        this.userDao = userDao;
        this.sessionProvider = sessionProvider;
        this.passwordSecurity = passwordSecurity;
        this.currentUserDtoProvider = currentUserDtoProvider;
        this.userSessionDao = userSessionDao;
    }

    public UserDto authenticateCredentials(final String username, final String password) {
        try {
            User user = userDao.findByUsername(username);
            
            if (passwordSecurity.check(password, user.getHashPassword())) {
                UserDto userDto = User.createDto(user);
                persistHttpSessionCookie(userDto);
                
                return userDto;
            } else {
                throw new AuthenticationException();
            }
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    public UserDto authenticatCookie(String loggedInCookie) throws AuthenticationException {
        UserDto userDto = userSessionDao.getUserFromCookie(loggedInCookie);

        if (userDto == null) {
            throw new AuthenticationException();
        } else {
            persistHttpSessionCookie(userDto);
        }

        return userDto;
    }

    public void logout() {
        removeCurrentUserLoginCookie();

        HttpSession httpSession = sessionProvider.get();
        httpSession.invalidate();
    }

    /**
     * Session support has to be enabled in the appengine-web.xml
     */
    private void persistHttpSessionCookie(UserDto user) {
        HttpSession session = sessionProvider.get();
        session.setAttribute(SecurityParameters.getUserSessionKey(), user.getId());
    }

    public Boolean isUserLoggedIn() {
        HttpSession session = sessionProvider.get();
        Long userId = (Long) session.getAttribute(SecurityParameters.getUserSessionKey());

        return userId != null;
    }

    private void removeCurrentUserLoginCookie() {
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();
        UserDto userDto = currentUserDto.getUser();
        userSessionDao.removeLoggedInCookie(userDto);
    }
}
