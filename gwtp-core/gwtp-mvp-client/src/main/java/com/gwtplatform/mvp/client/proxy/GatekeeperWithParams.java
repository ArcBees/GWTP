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

package com.gwtplatform.mvp.client.proxy;

/**
 * Specialized {@link Gatekeeper} which needs additional parameters
 * in order to find out if the protected {@link Place} can be revealed.
 * <p>
 * Example of use:
 * <pre>
 * public class HasAllRolesGatekeeper implements GatekeeperWithParams {
 *
 * private final CurrentUser currentUser;
 * private String[] requiredRoles;
 *
 * {@code @}Inject
 * public HasAllRolesGatekeeper( CurrentUser currentUser ) {
 * this.currentUser = currentUser;
 * }
 *
 * {@code @}Override
 * public GatekeeperWithParams withParams(String[] params) {
 * requiredRoles = params;
 * return this;
 * }
 *
 * {@code @}Override
 * public boolean canReveal() {
 * return currentUser.getRoles().containsAll(Arrays.asList(requiredRoles);
 * }
 * }
 * </pre>
 * <p>
 * You must also make sure that your custom Ginjector provides a {@code get}
 * method returning this {@link GatekeeperWithParams} if you want to use it
 * with the {@link com.gwtplatform.mvp.client.annotations.GatekeeperParams}
 * annotation.
 * <p>
 * You should usually bind your {@link GatekeeperWithParams} as a singleton.
 */
public interface GatekeeperWithParams extends Gatekeeper {

    /**
     * Sets the parameters required by this {@link GatekeeperWithParams}
     * in order to decide if {@link Place} can be revealed.
     *
     * @param params array of parameters
     * @return a reference to itself for chaining with the call to the inherited
     *         canReveal method.
     */
    GatekeeperWithParams withParams(String[] params);
}
