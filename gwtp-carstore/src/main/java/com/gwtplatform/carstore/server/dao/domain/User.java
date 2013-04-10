package com.gwtplatform.carstore.server.dao.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.gwtplatform.carstore.shared.domain.BaseEntity;
import com.gwtplatform.carstore.shared.dto.UserDto;

@Index
@Entity
public class User extends BaseEntity {
    public static UserDto createDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setId(user.getId());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        
        return userDto;
    }
    
    public static User create(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setHashPassword(userDto.getHashPassword());
        user.setId(userDto.getId());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        
        return user;
    }
    
    private String username;
    private String hashPassword;
    private String firstName;
    private String lastName;

    public User() {
        firstName = "";
        lastName = "";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    @Override
    public String toString() {
        String s = "{ User ";
        s += "id=" + id + " ";
        s += "username=" + username + " ";
        s += "hasPassword=" + hashPassword + " ";
        s += "firstName=" + firstName + " ";
        s += "lastName=" + lastName + " ";  
        s += "}";
        return s;
    }
}
