/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.shared.dto;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserDto extends BaseEntity {
    private String username;
    private String hashPassword;
    private String firstName;
    private String lastName;

    public UserDto() {
        firstName = "";
        lastName = "";
    }

    public UserDto(String username,
                   String hashPassword,
                   String firstName,
                   String lastName) {
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
