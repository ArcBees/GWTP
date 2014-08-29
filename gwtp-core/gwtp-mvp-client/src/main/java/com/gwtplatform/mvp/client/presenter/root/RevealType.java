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
package com.gwtplatform.mvp.client.presenter.root;

import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;

/**
 * The RevealType define which event will be fired in the default {@link #revealInParent()}.
 * <p/>
 * Root will fire a {@link RevealRootContentEvent}.
 * RootLayout will fire a {@link RevealRootLayoutContentEvent}.
 * RootPopup will fire a {@link RevealRootPopupContentEvent}.
 */
public enum RevealType implements IRevealType {
    Root,
    RootLayout,
    RootPopup;

    @Override
    public boolean isRoot() {
        return this == Root;
    }

    @Override
    public boolean isRootLayout() {
        return this == RootLayout;
    }

    @Override
    public boolean isRootPopup() {
        return this == RootPopup;
    }
}
