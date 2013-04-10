package com.gwtplatform.carstore.server.dao.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.gwtplatform.carstore.shared.dto.Dto;

@Index
@Entity
public class UserSession implements Dto {
    @Id
    private Long userId;
    private String cookie;
    private Date dateCreated;

    public UserSession() {
    }

    public UserSession(Long userId, String cookie) {
        super();
        
        this.userId = userId;
        this.cookie = cookie;
        this.dateCreated = new Date();
    }

    public String getCookie() {
        return cookie;
    }

    public Long getUserId() {
        return userId;
    }
}
