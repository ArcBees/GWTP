package com.arcbees.carsample.server.authentication;

import com.arcbees.carsample.server.dao.UserDao;
import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.dto.CurrentUserDto;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;

@RunWith(JukitoRunner.class)
public class CurrentUserDtoProviderTest {
    @SuppressWarnings("unused")
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }

    private static final int A_USER_ID = 1;

    @Inject
    HttpSession httpSession;
    @Inject
    UserDao userDao;
    @Inject
    CurrentUserDtoProvider currentUserDtoProvider;

    @Test
    public void aValidSessionShouldReturnTheCurrentUser() {
        // Given
        User user = mock(User.class);
        given(httpSession.getAttribute(SecurityParameters.getUserSessionKey())).willReturn(A_USER_ID);
        given(userDao.find(A_USER_ID)).willReturn(user);

        // When
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();

        // Then
        assertTrue(currentUserDto.isLoggedIn());
        assertEquals(user, currentUserDto.getUser());
    }

    @Test
    public void anInvalidSessionShouldNotReturnTheCurrentUser() {
        // Given
        given(httpSession.getAttribute(SecurityParameters.getUserSessionKey())).willReturn(null);
        given(userDao.find(anyInt())).willReturn(null);

        // When
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();

        // Then
        assertFalse(currentUserDto.isLoggedIn());
        assertNull(currentUserDto.getUser());
    }
}
