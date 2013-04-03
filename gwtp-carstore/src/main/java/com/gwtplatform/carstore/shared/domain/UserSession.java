package com.gwtplatform.carstore.shared.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Index
@Entity
public class UserSession extends BaseEntity {
    private Long userId;
    private String cookie;
    private Date dateCreated;

    public UserSession() {
    }

    public UserSession(User user, String cookie) {
        super();
        
        this.userId = user.getId();
        this.cookie = cookie;
        this.dateCreated = new Date();
    }

    public String getCookie() {
        return cookie;
    }
}
