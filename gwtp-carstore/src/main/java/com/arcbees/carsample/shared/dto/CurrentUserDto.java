package com.arcbees.carsample.shared.dto;

import com.arcbees.carsample.shared.domain.User;

public class CurrentUserDto implements Dto {
    private Boolean loggedIn;
    private User user;

    protected CurrentUserDto() {
        // Needed for serialization
    }

    public CurrentUserDto(Boolean loggedIn, User user) {
        this.loggedIn = loggedIn;
        this.user = user;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public User getUser() {
        return user;
    }
    
    @Override
    public String toString() {
        String s = " { CurrentUserDto ";
        s += "loggedIn=" + loggedIn + " ";
        s += "user=" + user + " ";
        s += " CurrentUserDto }";
        return s;
    }
}
