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
 * A class that can contain handlers. Handlers can be registered when
 * the object is being bound, or at any time while it is bound. They
 * will be automatically unregistered when the class is unbound.
 * <p>
 * For details on the autobinding mechanism, see {@link HandlerContainerImpl}.
 */
public interface HandlerContainer {

    /**
     * Call this method after the object is constructed in order to bind all its
     * handlers. You should never call {@link #bind()} from the constructor
     * of a non-leaf class since it is meant to be called after the object has
     * been entirely constructed.
     * <p>
     * When automatic binding is used (see {@link HandlerContainerImpl}), this will
     * be called immediately after the object is constructed through Guice/GIN dependency
     * injection mechanism.
     * <p>
     * If you are not using automatic binding, or if you later call
     * {@link #unbind()} on this object, you will have to call {@link #bind()}
     * manually.
     * <p>
     * Multiple call to bind will not fail, the class will be bound once.
     */
    void bind();

    /**
     * Returns true if the {@link HandlerContainer} is currently bound.
     * That is, the {@link #bind()} method has completed and {@link #unbind()} has not
     * been called.
     *
     * @return {@code true} if bound, {@code false} otherwise.
     */
    boolean isBound();

    /**
     * Call this method when you want to release the object and its handlers are
     * not needed anymore. You will have to call {@link #bind} again manually
     * if you ever want to reuse the object.
     */
    void unbind();
}
