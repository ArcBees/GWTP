package com.gwtplatform.carstore.server.dao;

import com.gwtplatform.carstore.shared.domain.User;

public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(User.class);
    }

    public User findByUsername(String username) {
        return ofy().query(User.class).filter("username", username).first().getValue();
    }
}
