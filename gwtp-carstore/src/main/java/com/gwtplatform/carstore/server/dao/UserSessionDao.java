package com.gwtplatform.carstore.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.domain.User;
import com.gwtplatform.carstore.server.dao.domain.UserSession;
import com.gwtplatform.carstore.shared.dto.UserDto;

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

    public String createLoggedInCookie(UserDto userDto) {
        String cookie = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(userDto, cookie);
        put(userSession);

        logger.info("UserSessionDao.createLoggedInCookie(user) user=" + userDto + " userSessionCookie="
                + userSession.getCookie());

        return userSession.getCookie();
    }

    public void removeLoggedInCookie(UserDto userDto) {
        UserSession userSession = findUserSession(userDto.getId());
        if (userSession != null) {
            delete(userSession);
        }

        logger.info("UserSessionDao.removeLoggedInCookie(user): Cookie is removed from database.");
    }

    public UserDto getUserFromCookie(String loggedInCookie) {
        Date twoWeeksAgo = getTwoWeeksAgo();

        UserSession userSession = ofy().query(UserSession.class).filter("cookie", loggedInCookie).filter(
                "dateCreated > ", twoWeeksAgo).first().getValue();
        Long userId = userSession.getId();

        UserDto userDto = null;
        if (userId != null) {
            userDto = User.createDto(userDao.get(userId));
        }

        return userDto;
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
