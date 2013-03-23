package com.arcbees.carsample.shared.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_session")
public class UserSession implements BaseEntity {
    @Id
    private Integer userId;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String cookie;

    @Column(nullable = false, columnDefinition = "Timestamp")
    private Date dateCreated;

    public UserSession() {
    }

    public UserSession(User user, String cookie) {
        super();
        userId = user.getId();
        this.cookie = cookie;
        dateCreated = new Date();
    }

    @Override
    public Integer getId() {
        return userId;
    }

    public String getCookie() {
        return cookie;
    }
}
