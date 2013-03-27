package com.arcbees.carsample.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.domain.UserSession;

public class UserSessionDao extends BaseDao<UserSession> {
    private static final int TWO_WEEKS_AGO_IN_DAYS = -14;

    private final Logger logger;
    private final UserDao userDao;

    @Inject
    public UserSessionDao(Logger logger, UserDao userDao) {
        super(UserSession.class);

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

        UserSession userSession = ofy().query(UserSession.class).filter("cookie", loggedInCookie).filter(
                "dateCreated > ", twoWeeksAgo).first().getValue();
        Long userId = userSession.getId();

        User user = null;
        if (userId != null) {
            user = userDao.get(userId);
        }

        return user;
    }

    private Date getTwoWeeksAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, TWO_WEEKS_AGO_IN_DAYS);

        return calendar.getTime();
    }

    private UserSession findUserSession(Long userId) {
        return ofy().query(UserSession.class).filter("userId", userId).first().getValue();
    }
}
