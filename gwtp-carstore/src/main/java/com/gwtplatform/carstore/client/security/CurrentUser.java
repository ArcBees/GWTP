package com.gwtplatform.carstore.client.security;

import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

public class CurrentUser {
    private Boolean loggedIn;
    private UserDto userDto;

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

    public UserDto getUser() {
        return userDto;
    }

    public void setUser(UserDto userDto) {
        this.userDto = userDto;
    }
}
