/*
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.mvp.client.uihandlers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.UiHandlers;

/**
 * This strategy is meant to be use with a {@link com.google.inject.Provider}. Sometimes this
 * strategy can't be used and we must set manually the {@link com.gwtplatform.mvp.client.UiHandlers}
 * implementation. In those cases, use {@link SetterUiHandlersStrategy}.
 *
 * @param <H> {@link com.gwtplatform.mvp.client.UiHandlers}'s type.
 */
public class ProviderUiHandlersStrategy<H extends UiHandlers> implements UiHandlersStrategy<H> {
    private final Provider<H> uiHandlersProvider;

    @Inject
    public ProviderUiHandlersStrategy(final Provider<H> uiHandlersProvider) {
        this.uiHandlersProvider = uiHandlersProvider;
    }

    @Override
    public void setUiHandlers(H uiHandlers) {
    }

    @Override
    public H getUiHandlers() {
        return uiHandlersProvider.get();
    }
}
