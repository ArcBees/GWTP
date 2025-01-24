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

/**
 * Defines a class that will be executed after injection happens. Your bootstrapper must contain a default, empty
 * constructor or an {@linkplain jakarta.inject.Inject @Inject}ed constructor.
 * <p>
 * This class is responsible for calling {@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealCurrentPlace
 * PlaceManager#revealCurrentPlace}. This is particularly useful when you need to do initialisation steps before the
 * first page is shown to the user.
 * <p>
 * Your bootstrapper must be configured by annotating a
 * {@link com.gwtplatform.common.client.annotations.GwtpApp @GwtpApp}-annotated class with
 * {@link com.gwtplatform.mvp.client.annotations.UseBootstrapper @UseBootstrapper}. Only
 * one such class is authorized in an application.
 *
 * @see DefaultBootstrapper
 */
public interface Bootstrapper {
    void onBootstrap();
}
