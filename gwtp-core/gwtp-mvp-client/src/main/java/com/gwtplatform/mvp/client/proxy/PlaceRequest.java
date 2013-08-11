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

package com.gwtplatform.mvp.client.proxy;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a 'request' for a place location. It includes the 'id'
 * of the place as well as any parameter values. It can convert from and to
 * String tokens for use with the GWT History.
 * <p/>
 * <p/>
 * Place request tokens are formatted like this:
 * <p/>
 * <code>#nameToken(;key=value)*</code>
 * <p/>
 * <p/>
 * There is a mandatory 'nameToken' value, followed by 0 or more key/value
 * pairs, separated by semi-colons (';'). A few examples follow:
 * <p/>
 * <ul>
 * <li> <code>#users</code></li>
 * <li> <code>#user;name=j.blogs</code></li>
 * <li> <code>#user-email;name=j.blogs;type=home</code></li>
 * </ul>
 * The separators (';' and '=') can be modified in
 * {@link com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter}.
 */
public class PlaceRequest {
    private final String nameToken;
    private final Map<String, String> params;

    /**
     * Builds a request without any name token and without parameters. You should
     * typically use {@link #PlaceRequest(String)} and specify the name token.
     * However, this version is acceptable when calling
     * {@link com.gwtplatform.mvp.client.proxy.Proxy#reveal(com.gwtplatform.mvp.client.proxy.PlaceRequest)}.
     */
    public PlaceRequest() {
        this.nameToken = null;
        // Note: No parameter map attached.
        // Calling PlaceRequest#with(String, String) will
        // invoke the other constructor and instantiate a map.
        // This choice makes it efficient to instantiate
        // parameter-less PlaceRequest and slightly more
        // costly to instantiate PlaceRequest with parameters.
        this.params = null;
    }

    /**
     * Builds a request with the specified name token and without parameters. You
     * can later add parameters by doing:
     * <p/>
     * <pre>
     *   PlaceRequest request = newRequest.with(key1, param1)
     *                                    .with(key2, param2);
     * </pre>
     *
     * @param nameToken The name token for the request.
     * @deprecated Please use {@link com.gwtplatform.mvp.client.proxy.PlaceRequest.Builder#nameToken(String)}
     *             instead
     */
    @Deprecated
    public PlaceRequest(String nameToken) {
        this.nameToken = nameToken;
        // Note: No parameter map attached.
        // Calling PlaceRequest#with(String, String) will
        // invoke the other constructor and instantiate a map.
        // This choice makes it efficient to instantiate
        // parameter-less PlaceRequest and slightly more
        // costly to instantiate PlaceRequest with parameters.
        this.params = null;
    }

    /**
     * Builds a place request with the specified name token and with an existing parameter map.
     *
     * @param nameToken The name token for the request.
     * @param params    Existing parameter map.
     */
    private PlaceRequest(String nameToken, Map<String, String> params) {
        this.nameToken = nameToken;
        this.params = params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlaceRequest) {
            PlaceRequest req = (PlaceRequest) obj;
            if (nameToken == null || req.nameToken == null) {
                return false;
            }
            if (!nameToken.equals(req.nameToken)) {
                return false;
            }

            if (params == null) {
                return req.params == null;
            } else {
                return params.equals(req.params);
            }
        }
        return false;
    }

    public String getNameToken() {
        return nameToken;
    }

    /**
     * Extracts a given parameter from the {@link com.gwtplatform.mvp.client.proxy.PlaceRequest}.
     *
     * @param key          The name of the parameter.
     * @param defaultValue The value returned if the parameter is not found.
     * @return The value of the parameter if found, the {@code defaultValue} otherwise.
     */
    public String getParameter(String key, String defaultValue) {
        String value = null;

        if (params != null) {
            value = params.get(key);
        }

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Retrieves all the parameters available with the request.
     *
     * @return A {@link java.util.Set} containing all the parameter names.
     */
    public Set<String> getParameterNames() {
        if (params != null) {
            return params.keySet();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public int hashCode() {
        if (nameToken == null) {
            throw new RuntimeException(
                    "Cannot compute hashcode of PlaceRequest with a null nameToken");
        }
        return 11 * (nameToken.hashCode() + (params == null ? 0 : params.hashCode()));
    }

    /**
     * Checks if this place request has the same name token as the one passed in.
     *
     * @param other The {@link com.gwtplatform.mvp.client.proxy.PlaceRequest} to check against.
     * @return <code>true</code> if both requests share the same name token. <code>false</code> otherwise.
     */
    public boolean hasSameNameToken(PlaceRequest other) {
        if (nameToken == null || other.nameToken == null) {
            return false;
        }
        return nameToken.equals(other.nameToken);
    }

    /**
     * Checks if this place request matches the name token passed.
     *
     * @param nameToken The name token to match.
     * @return <code>true</code> if the request matches. <code>false</code> otherwise.
     */
    public boolean matchesNameToken(String nameToken) {
        if (this.nameToken == null || nameToken == null) {
            return false;
        }
        return this.nameToken.equals(nameToken);
    }

    /**
     * Returns a new instance of the request with the specified parameter name and
     * value. If a parameter with the same name was previously specified, the new
     * request contains the new value.
     *
     * @param name  The new parameter name.
     * @param value The new parameter value.
     * @return The new place request instance.
     * @deprecated Please use {@link com.gwtplatform.mvp.client.proxy.PlaceRequest.Builder#with(String, String)}
     *             instead
     */
    @Deprecated
    public PlaceRequest with(String name, String value) {
        // Note: Copying everything to a new PlaceRequest is slightly
        // less efficient than modifying the current request, but
        // it reduces unexpected side-effects. Moreover, it lets
        // us instantiate the parameter map only when needed.
        // (See the PlaceRequest constructors.)
        Builder b = new Builder().nameToken(nameToken);
        b.with(params);
        b.with(name, value);
        return b.build();
    }

    /**
     * Class for constructing {@link com.gwtplatform.mvp.client.proxy.PlaceRequest}s. This class supports all currently
     * existing constructors and the {@link com.gwtplatform.mvp.client.proxy.PlaceRequest#with(String, String)} method.
     * It is not checked if any of the builder parameters are set when calling the {@link #build()} method as is
     * currently also possible to construct an empty {@link com.gwtplatform.mvp.client.proxy.PlaceRequest}
     * (see {@link PlaceRequest#PlaceRequest()})
     */
    public static final class Builder {
        private String nameToken;
        private Map<String, String> params;

        public Builder() {
        }

        /**
         * 'Copy' constructor for initializing a new <code>Builder</code> with data from an existing request.
         */
        public Builder(PlaceRequest request) {
            nameToken = request.nameToken;
            params = request.params;
        }

        public Builder nameToken(String nameToken) {
            this.nameToken = nameToken;

            return this;
        }

        public Builder with(String name, String value) {
            lazyInitializeParamMap();
            if (value != null) {
                this.params.put(name, value);
            }

            return this;
        }

        public Builder with(Map<String, String> params) {
            lazyInitializeParamMap();
            if (params != null) {
                this.params.putAll(params);
            }

            return this;
        }

        private void lazyInitializeParamMap() {
            if (this.params == null) {
                this.params = new LinkedHashMap<String, String>();
            }
        }

        public PlaceRequest build() {
            return new PlaceRequest(nameToken, params);
        }
    }
}
