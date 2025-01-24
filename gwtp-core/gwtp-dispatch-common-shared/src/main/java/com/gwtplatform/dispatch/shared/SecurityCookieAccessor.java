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
 * Provides access to the security cookie stored on the client. The goal of this security cookie is to prevent XSRF
 * attack. For more details see <a href="http://www.gwtproject.org/articles/security_for_gwt_applications.html#xsrf">
 *     this document</a>.
 * <p>
 * Notice that the default implementation is {@code com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor} which
 * does not prevent XSRF attacks if {@literal @}{@link SecurityCookie} is not bound.
 */
public interface SecurityCookieAccessor {
    /**
     * Gets the current content of the security cookie, using javascript.
     *
     * @return The current content of the security cookie, or {@code null} if no protection again XSRF is needed.
     */
    String getCookieContent();
}
