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
package com.gwtplatform.mvp.client.presenter.slots;

import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * A slot that can only take one presenter.
 * Once a presenter is in this slot it can never be removed.
 *
 * @param <T> - the type of presenter that this slot can take.
 */
public class PermanentSlot<T extends PresenterWidget<?>> implements IsSingleSlot<T> {
    @Override
    public boolean isPopup() {
        return false;
    }
    @Override
    public boolean isRemovable() {
        return false;
    }
    @Override
    public Object getRawSlot() {
        return this;
    }
}
