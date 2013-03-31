package com.arcbees.carsample.shared.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Index;

@Index
@Entity
public class UserSession extends BaseEntity {
    @Id
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
