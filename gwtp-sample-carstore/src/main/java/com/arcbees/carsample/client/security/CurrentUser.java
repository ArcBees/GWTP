package com.arcbees.carsample.client.security;

import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.dto.CurrentUserDto;

public class CurrentUser {
    private Boolean loggedIn;
    private User user;

    public CurrentUser() {
        loggedIn = false;
    }

    public void fromCurrentUserDto(CurrentUserDto currentUserDto) {
        setLoggedIn(currentUserDto.isLoggedIn());
        setUser(currentUserDto.getUser());
    }

    public void reset() {
        setLoggedIn(false);
        setUser(null);
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
