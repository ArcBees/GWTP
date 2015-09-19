/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.shared;

/**
 * These are thrown by {@link com.gwtplatform.dispatch.server.Dispatch#execute(Action)} if there is a problem executing
 * a particular {@link Action}.
 */
public class ActionException extends Exception {

    private static final long serialVersionUID = -1423773155541528952L;

    TypedAction<?> action;

    public ActionException() {
    }

    public ActionException(TypedAction<?> action) {
    }

    public ActionException(TypedAction<?> action, String message) {
        super(message);
    }

    public ActionException(TypedAction<?> action, String message, Throwable cause) {
        super(message, cause);
    }

    public ActionException(TypedAction<?> action, Throwable cause) {
        super(cause.getMessage());
    }

    public TypedAction<?> getAction() {
        return action;
    }
}
