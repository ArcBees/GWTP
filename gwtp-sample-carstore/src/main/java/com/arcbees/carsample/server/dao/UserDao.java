package com.arcbees.carsample.server.dao;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.arcbees.carsample.shared.domain.User;

public class UserDao extends BaseDao<User> {
    @Inject
    public UserDao(Provider<EntityManager> entityManagerProvider) {
        super(User.class);
    }

    public User findByUsername(String username) {
        return ofy().query(User.class).filter("username", username).first().getValue();
    }
}
