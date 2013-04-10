package com.gwtplatform.carstore.shared.dto;


public class CurrentUserDto implements Dto {
    private Boolean loggedIn;
    private UserDto userDto;

    protected CurrentUserDto() {
        // Needed for serialization
    }

    public CurrentUserDto(Boolean loggedIn, UserDto userDto) {
        this.loggedIn = loggedIn;
        this.userDto = userDto;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public UserDto getUser() {
        return userDto;
    }
    
    @Override
    public String toString() {
        String s = " { CurrentUserDto ";
        s += "loggedIn=" + loggedIn + " ";
        s += "user=" + userDto + " ";
        s += " CurrentUserDto }";
        return s;
    }
}
