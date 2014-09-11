/**
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

import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class TypeSlot<T extends Presenter<?,?>> extends GwtEvent.Type<RevealContentHandler<?>>
    implements ISingleSlot<T> {
    @Override
    public boolean isPopup() {
        return false;
    }
    @Override
    public boolean isRemovable() {
        return true;
    }
}
