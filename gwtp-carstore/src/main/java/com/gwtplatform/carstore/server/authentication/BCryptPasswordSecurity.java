package com.gwtplatform.carstore.server.authentication;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordSecurity implements PasswordSecurity {
    @Override
    public Boolean check(String password, String hashPassword) {
        return BCrypt.checkpw(password, hashPassword);
    }

    @Override
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
