package com.gwtplatform.carstore.server.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.server.authentication.SecurityParameters;
import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;

@RunWith(JukitoRunner.class)
public class CurrentUserDtoProviderTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }

    private static final long A_USER_ID = 1;

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
        given(userDao.get(A_USER_ID)).willReturn(user);

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
        given(userDao.get(anyLong())).willReturn(null);

        // When
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();

        // Then
        assertFalse(currentUserDto.isLoggedIn());
        assertNull(currentUserDto.getUser());
    }
}
