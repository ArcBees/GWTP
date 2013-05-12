/**
 * Copyright 2013 ArcBees Inc.
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

    public UserSession(Long userId,
                       String cookie) {
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
