package com.gwtplatform.carstore.server.dao;

import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.carstore.shared.dto.UserDto;

public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(User.class);
    }

    public UserDto findByUsername(String username) {
        return ofy().query(UserDto.class).filter("username", username).first().getValue();
    }
}
