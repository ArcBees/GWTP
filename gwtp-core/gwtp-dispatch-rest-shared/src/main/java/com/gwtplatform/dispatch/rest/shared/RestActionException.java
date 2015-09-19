/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.shared;

import com.gwtplatform.dispatch.shared.ActionException;

public class RestActionException extends ActionException {

    public RestActionException() {
    }

    public RestActionException(RestAction<?> action) {
        super(action);
    }

    public RestActionException(RestAction<?> action, String message) {
        super(action, message);
    }

    public RestActionException(RestAction<?> action, String message, Throwable cause) {
        super(action, message, cause);
    }

    public RestActionException(RestAction<?> action, Throwable cause) {
        super(action, cause);
    }

    @Override
    public RestAction<?> getAction() {
        return (RestAction<?>) super.getAction();
    }
}
