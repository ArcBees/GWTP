package com.arcbees.carsample.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.domain.UserSession;

public class UserSessionDao extends BaseDao<UserSession> {
    private static final int TWO_WEEKS_AGO_IN_DAYS = -14;
    private final Logger logger;
    private final UserDao userDao;

    @Inject
    public UserSessionDao(Provider<EntityManager> entityManagerProvider, final Logger logger, final UserDao userDao) {
        super(UserSession.class, entityManagerProvider);

        this.logger = logger;
        this.userDao = userDao;
    }

    public String createLoggedInCookie(User user) {
        String cookie = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(user, cookie);
        put(userSession);

        logger.info("UserSessionDao.createLoggedInCookie(user) user=" + user + " userSessionCookie="
                + userSession.getCookie());

        return userSession.getCookie();
    }

    public void removeLoggedInCookie(User user) {
        UserSession userSession = findUserSession(user.getId());
        if (userSession != null) {
            delete(userSession);
        }

        logger.info("UserSessionDao.removeLoggedInCookie(user): Cookie is removed from database.");
    }

    public User getUserFromCookie(String loggedInCookie) {
        Date twoWeeksAgo = getTwoWeeksAgo();

        String queryStr = "select o from " + UserSession.class.getName() + " o where "
                + "o.cookie = :loggedInCookie and o.dateCreated > :twoWeeksAgo";

        Query query = entityManager().createQuery(queryStr);
        query.setParameter("twoWeeksAgo", twoWeeksAgo);
        query.setParameter("loggedInCookie", loggedInCookie);

        logger.info("UserSessionDao.getUserFromCookie(): query=" + query.toString());

        Integer userId = null;
        try {
            UserSession userSession = (UserSession) query.getSingleResult();
            userId = userSession.getId();
        } catch (Exception e) {
            logger.info("UserSessionDao.getUserFromCookie(): Couldn't find user with cookie.");
        }

        User user = null;
        if (userId != null) {
            user = userDao.find(userId);
        }

        return user;
    }

    private Date getTwoWeeksAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, TWO_WEEKS_AGO_IN_DAYS);

        return calendar.getTime();
    }

    private UserSession findUserSession(Integer userId) {
        String queryStr = "select o from " + UserSession.class.getName() + " o where o.userId = :userId";

        Query query = entityManager().createQuery(queryStr);
        query.setParameter("userId", userId);

        logger.info("UserSessionDao.getUserFromCookie(): query=" + query.toString());

        UserSession userSession = null;
        try {
            userSession = (UserSession) query.getSingleResult();
        } catch (Exception e) {
            logger.info("UserSessionDao.findUserSession(userId): User session doesn't exist yet.");
        }

        return userSession;
    }
}
