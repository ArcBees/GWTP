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
 * Inherit from this class to define a gatekeeper that locks access to your
 * {@link Place} in specific situation. For example:
 * <p>
 * <pre>
 * public class AdminGatekeeper implements Gatekeeper {
 *
 *   private final CurrentUser currentUser;
 *
 *  {@code @}Inject
 *   public AdminGatekeeper( CurrentUser currentUser ) {
 *     this.currentUser = currentUser;
 *   }
 *
 *  {@code @}Override
 *   public boolean canReveal() {
 *     return currentUser.isAdministrator();
 *   }
 *
 * }
 * </pre>
 * <p>
 * You must also make sure that your custom Ginjector provides a {@code get}
 * method returning this {@link Gatekeeper} if you want to use it with the
 * {@link com.gwtplatform.mvp.client.annotations.UseGatekeeper} annotation.
 * <p>
 * You should usually bind your {@link Gatekeeper} as a singleton.
 */
public interface Gatekeeper {
    /**
     * Checks whether or not the {@link Place} controlled by this gatekeeper can
     * be revealed.
     *
     * @return {@code true} if the {@link Place} can be revealed, {@code false}
     *         otherwise.
     */
    boolean canReveal();
}
