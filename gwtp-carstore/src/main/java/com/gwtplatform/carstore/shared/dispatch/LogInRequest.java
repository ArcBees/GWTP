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

package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ActionType;

public class LogInRequest {
    ActionType actionType;
    String username;
    String password;
    String loggedInCookie;

    protected LogInRequest() {
    }

    public LogInRequest(String username,
                        String password) {
        actionType = ActionType.VIA_CREDENTIALS;
        this.password = password;
        this.username = username;
    }

    public LogInRequest(String loggedInCookie) {
        actionType = ActionType.VIA_COOKIE;
        this.loggedInCookie = loggedInCookie;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLoggedInCookie() {
        return loggedInCookie;
    }
}
