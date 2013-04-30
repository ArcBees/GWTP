package com.gwtplatform.carstore.server.authentication;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BCryptPasswordSecurityTest {
    private static final String VALID_PASSWORD = "a";
    private static final String INVALID_PASSWORD = "b";

    @Test
    public void checkingAValidPasswordShouldReturnTrue() {
        // Given
        PasswordSecurity passwordSecurity = new BCryptPasswordSecurity();
        String hashPassword = BCrypt.hashpw(VALID_PASSWORD, BCrypt.gensalt());

        // When
        Boolean check = passwordSecurity.check(VALID_PASSWORD, hashPassword);

        // Then
        assertTrue(check);
    }

    @Test
    public void checkingAnInvalidPasswordShouldReturnFalse() {
        // Given
        PasswordSecurity passwordSecurity = new BCryptPasswordSecurity();
        String hashPassword = BCrypt.hashpw(VALID_PASSWORD, BCrypt.gensalt());

        // When
        Boolean check = passwordSecurity.check(INVALID_PASSWORD, hashPassword);

        // Then
        assertFalse(check);
    }
}
