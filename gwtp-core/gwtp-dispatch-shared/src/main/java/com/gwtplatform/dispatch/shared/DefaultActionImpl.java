/**
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
 * Default Action using the base path as dispatch/className.
 * The base abstract implementation is {@link com.gwtplatform.dispatch.shared.Action}.
 * 
 * {@link https://github.com/ArcBees/GWTP/wiki/RPC-Dispatch}
 * 
 * @param <R> The {@link com.gwtplatform.dispatch.shared.Result} type returned.
 */
public abstract class DefaultActionImpl<R extends Result> implements Action<R> {
    /**
     * Is object equal?
     */
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    /**
     * Default path dispatch/className.
     */
    @Override
    public String getServiceName() {
        String className = this.getClass().getName();
        int namePos = className.lastIndexOf(".") + 1;
        className = com.gwtplatform.dispatch.shared.ActionImpl.DEFAULT_SERVICE_NAME + className.substring(namePos);

        return className;
    }

    /**
     * Class hash.
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /**
     * Secure is off by default.
     * {@link https://github.com/ArcBees/GWTP/wiki/Action-Validators}
     */
    @Override
    public boolean isSecured() {
        return false;
    }
}
