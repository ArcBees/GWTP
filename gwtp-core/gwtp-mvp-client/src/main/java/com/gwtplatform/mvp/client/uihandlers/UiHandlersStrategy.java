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

import com.gwtplatform.mvp.client.UiHandlers;

/**
 * Description of what a UiHandlersStrategy is. Every class should give a way to
 * get our {@link com.gwtplatform.mvp.client.UiHandlers} and to set them if needed.
 *
 * @param <H> {@link com.gwtplatform.mvp.client.UiHandlers}'s type.
 */
public interface UiHandlersStrategy<H extends UiHandlers> {
    /**
     * This method let you set a ui handlers manually.
     *
     * @param uiHandlers {@link com.gwtplatform.mvp.client.UiHandlers} implementation to set.
     */
    void setUiHandlers(H uiHandlers);

    /**
     * This let you retrieve you {@link com.gwtplatform.mvp.client.UiHandlers} implementation.
     *
     * @return {@link com.gwtplatform.mvp.client.UiHandlers} implementation
     */
    H getUiHandlers();
}
