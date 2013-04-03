package com.gwtplatform.carstore.shared.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.gwtplatform.carstore.shared.dto.UserDto;

@Index
@Entity
public class UserSession extends BaseEntity {
    private Long userId;
    private String cookie;
    private Date dateCreated;

    public UserSession() {
    }

    public UserSession(UserDto userDto, String cookie) {
        super();
        
        this.userId = userDto.getId();
        this.cookie = cookie;
        this.dateCreated = new Date();
    }

    public String getCookie() {
        return cookie;
    }
}
