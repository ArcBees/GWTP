package com.arcbees.carsample.server.dao;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.arcbees.carsample.shared.domain.User;

public class UserDao extends BaseDao<User> {
    @Inject
    public UserDao(Provider<EntityManager> entityManagerProvider) {
        super(User.class);
    }

    // TODO
    public User findByUsername(String username) {
        Query query = entityManager().createQuery("select u from User u where u.username = :username");
        query.setParameter("username", username);

        return (User) query.getSingleResult();
    }
}
