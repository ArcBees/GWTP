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
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.shared.Result;

public class LogInResult implements Result {
    ActionType actionType;
    CurrentUserDto currentUserDto;
    String loggedInCookie;

    protected LogInResult() {
        // Needed for serialization
    }

    public LogInResult(ActionType actionType,
                       CurrentUserDto currentUserDto,
                       String loggedInCookie) {
        this.actionType = actionType;
        this.currentUserDto = currentUserDto;
        this.loggedInCookie = loggedInCookie;
    }

    public CurrentUserDto getCurrentUserDto() {
        return currentUserDto;
    }

    public String getLoggedInCookie() {
        return loggedInCookie;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
