package com.arcbees.carsample.shared.domain;

import javax.persistence.Entity;

@Entity
public class User extends BaseEntity {
    private String username;

    private String hashPassword;

    private String firstName;

    private String lastName;

    public User() {
        firstName = "";
        lastName = "";
    }

    public User(String username, String hashPassword, String firstName, String lastName) {
        this.username = username;
        this.hashPassword = hashPassword;
        this.firstName = firstName;
        this.lastName = lastName;
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
        String s = " { User ";
        s += "id=" + id + " ";
        s += "username=" + username + " ";
        s += "hasPassword=" + hashPassword + " ";
        s += "firstName=" + firstName + " ";
        s += "lastName=" + lastName + " ";  
        s += " User } ";
        return s;
    }
}
