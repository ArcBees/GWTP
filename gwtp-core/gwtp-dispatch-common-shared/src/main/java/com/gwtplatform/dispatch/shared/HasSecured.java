/*
 * Copyright 2014 ArcBees Inc.
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
 * Indicates that the XSRF protection for an action can be toggled.
 */
public interface HasSecured {
    /**
     * Verifies if the action is secured. Secured actions perform a number of extra security checks, such as validating
     * the {@link com.gwtplatform.dispatch.shared.SecurityCookie} to foil XSRF attacks.
     * <p>
     * <b>Important!</b> Make sure your method returns a value that does not depend on client-side information,
     * otherwise it could be tampered with to turn a secure action into an insecure one. An example of a bad practice
     * would be to store a {@code boolean secured} member and return that. Since this field is serialized, the user
     * could change it on his side. A simple and good practice is simply to {@code return true;} or
     * {@code return false;}.
     *
     * @return {@code true} if the action should be secured against XSRF attacks, {@code false} otherwise.
     */
    boolean isSecured();
}
