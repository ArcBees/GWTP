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

package com.gwtplatform.dispatch.rpc.shared;

/**
 * Default Action using the base path as dispatch/className. The base abstract implementation is {@link Action}.
 * <p>
 * An {@link Action} that uses the standard service name {@code "dispatch"}. Actions inheriting from this are secured
 * against XSRF attacks, but you need to configure a {@link com.gwtplatform.dispatch.shared.SecurityCookie}.
 * <p>
 * Use {@link UnsecuredActionImpl} for secured set to off by default.
 * <p>
 * Visit <a href="https://github.com/ArcBees/GWTP/wiki/RPC-Dispatch">this page</a> for more documentation.
 *
 * @param <R> The {@link Result} type.
 */
public class ActionImpl<R extends Result> implements Action<R> {
    /**
     * Default Action using the base path as dispatch/className.
     */
    @Override
    public String getServiceName() {
        String className = this.getClass().getName();
        int namePos = className.lastIndexOf(".") + 1;
        return ActionImpl.DEFAULT_SERVICE_NAME + className.substring(namePos);
    }

    /**
     * Secure is on by default.
     */
    @Override
    public boolean isSecured() {
        return true;
    }
}
