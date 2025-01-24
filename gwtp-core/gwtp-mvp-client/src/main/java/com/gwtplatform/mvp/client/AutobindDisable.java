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

package com.gwtplatform.mvp.client;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Bind this class to indicate that you wish to globally disable automatic binding in
 * classes derived from {@link HandlerContainerImpl}, such as {@link PresenterWidget}
 * or {@link Presenter}. Simply bind it like this:
 * <pre>
 * bind(AutobindDisable.class).toInstance(new AutobindDisable(true));
 * </pre>
 * If you do not bind this class then autobinding is controlled on a case-by-case
 * basis using the {@link HandlerContainerImpl}'s constructors.
 * <p>
 * Disabling automatic binding can be useful in unit tests, for example.
 */
@Singleton
public class AutobindDisable {
    private final boolean disable;

    @Inject
    AutobindDisable() {
        disable = false;
    }

    public AutobindDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean disable() {
        return disable;
    }
}
