package com.gwtplatform.carstore.shared.dto;


public class CurrentUserDto implements Dto {
    Boolean loggedIn;
    UserDto user;

    protected CurrentUserDto() {
        // Needed for serialization
    }

    public CurrentUserDto(Boolean loggedIn, UserDto user) {
        this.loggedIn = loggedIn;
        this.user = user;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public UserDto getUser() {
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
