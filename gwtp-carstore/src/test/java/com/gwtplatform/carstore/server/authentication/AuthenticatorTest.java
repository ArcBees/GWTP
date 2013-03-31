package com.gwtplatform.carstore.server.authentication;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.carstore.server.authentication.AuthenticationException;
import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.authentication.SecurityParameters;
import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.UserSessionDao;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;

@RunWith(JukitoRunner.class)
public class AuthenticatorTest {
    @SuppressWarnings("unused")
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
            forceMock(UserSessionDao.class);
            forceMock(CurrentUserDtoProvider.class);
        }
    }

    private static final String A_VALID_USER = "valid-user";
    private static final String AN_INVALID_USER = "invalid-user";
    private static final String A_VALID_PASSWORD = "valid-password";
    private static final String AN_INVALID_PASSWORD = "invalid-password";
    private static final long A_USER_ID = 1;

    @Inject
    Authenticator authenticator;
    @Inject
    UserDao userDao;
    @Inject
    PasswordSecurity passwordSecurity;
    @Inject
    HttpSession httpSession;
    @Inject
    CurrentUserDtoProvider currentUserDtoProvider;

    @Test
    public void aValidUserShouldBeAbleToConnect() {
        // Given
        User user = mock(User.class);
        given(user.getId()).willReturn(A_USER_ID);
        given(userDao.findByUsername(A_VALID_USER)).willReturn(user);
        given(passwordSecurity.check(anyString(), anyString())).willReturn(true);

        // When
        User fetchUser = authenticator.authenticateCredentials(A_VALID_USER, A_VALID_PASSWORD);

        // Then
        assertNotNull(fetchUser);
        verify(httpSession).setAttribute(SecurityParameters.getUserSessionKey(), A_USER_ID);
    }

    @Test(expected = AuthenticationException.class)
    public void aValidUserWithAnInvalidPasswordShouldntBeAbleToConnect() {
        // Given
        given(userDao.findByUsername(A_VALID_USER)).willReturn(mock(User.class));
        given(passwordSecurity.check(anyString(), anyString())).willReturn(false);

        // When
        authenticator.authenticateCredentials(A_VALID_USER, AN_INVALID_PASSWORD);

        // Then
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test(expected = AuthenticationException.class)
    public void anInvalidUserShouldntBeAbleToConnect() {
        // Given
        given(userDao.findByUsername(AN_INVALID_USER)).willThrow(new NoResultException());

        // When
        authenticator.authenticateCredentials(AN_INVALID_USER, AN_INVALID_PASSWORD);

        // Then
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    public void logoutShouldDestroyTheSession(UserSessionDao userSessionDao) {
        // Given
        User user = mock(User.class);
        given(user.getId()).willReturn(0l);
        
        CurrentUserDto currentUserDto = mock(CurrentUserDto.class);
        given(currentUserDto.getUser()).willReturn(user);
        given(currentUserDtoProvider.get()).willReturn(currentUserDto);
        
        // When
        authenticator.logout();

        // Then
        verify(httpSession).invalidate();
    }
}
