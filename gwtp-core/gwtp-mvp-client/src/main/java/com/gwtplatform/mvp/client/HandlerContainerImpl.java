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

import java.util.List;

import com.google.web.bindery.event.shared.HandlerRegistration;

import jakarta.inject.Inject;

/**
 * The implementation of {@link HandlerContainer}. Inherit from this class if
 * you want subclasses that can contain handlers.
 * <p>
 * Classes inheriting from {@link HandlerContainerImpl} and that participate in dependency
 * injection with Guice/GIN can use the automatic binding mechanism. See
 * {@link HandlerContainerImpl#HandlerContainerImpl()} and {@link #HandlerContainerImpl(boolean)}
 * for more details.
 */
public class HandlerContainerImpl implements HandlerContainer {

    /**
     * We use this static class instead of a boolean to make the {@code bound}
     * field final. This is done in order for it to not be persisted by objectify,
     * since objectify persists field maked as {@code transient}.
     */
    private static class BindMonitor {
        public boolean value;
    }

    private final transient boolean autoBind;
    private final transient BindMonitor bound = new BindMonitor();

    private final transient List<HandlerRegistration> handlerRegistrations = new java.util
            .ArrayList<>();

    /**
     * Creates a handler container class with automatic binding, unless
     * {@link AutobindDisable} is used to globally disable automatic
     * binding.
     * <p>
     * Autobinding requires the class to be instantiated by Guice/GIN.
     * If you are instantiating {@link HandlerContainerImpl} with {@code new},
     * autobinding will not work. It is recommended you document it by
     * using #HandlerContainerImpl(boolean) with {@code false} as a parameter.
     */
    @Inject
    public HandlerContainerImpl() {
        this(true);
    }

    /**
     * Creates a handler container class with or without automatic binding. If
     * automatic binding is requested, the {@link #bind()} method will be called
     * automatically after the class is instantiated through Guice/GIN dependency
     * injection mechanism, unless {@link AutobindDisable} is used to globally
     * disable automatic binding. Otherwise, the user is responsible for calling
     * {@link #bind()}.
     * <p>
     * Autobinding requires the class to be instantiated by Guice/GIN.
     * If you are instantiating {@link HandlerContainerImpl} with {@code new},
     * autobinding will not work. It is recommended you document it by
     * passing {@code false} to the {@code autoBind} parameter.
     *
     * @param autoBind {@code true} to request automatic binding, {@code false} otherwise.
     * @see #HandlerContainerImpl()
     */
    public HandlerContainerImpl(boolean autoBind) {
        super();
        this.autoBind = autoBind;
    }

    @Override
    public void bind() {
        if (!bound.value) {
            onBind();
            bound.value = true;
        }
    }

    @Override
    public final boolean isBound() {
        return bound.value;
    }

    @Override
    public void unbind() {
        if (bound.value) {
            bound.value = false;

            for (HandlerRegistration reg : handlerRegistrations) {
                reg.removeHandler();
            }
            handlerRegistrations.clear();

            onUnbind();
        }
    }

    /**
     * Lifecycle method called when binding the object.
     * <p>
     * <b>Important :</b> Make sure you call your parent class {@link #onBind()}. Also, do
     * not call directly, call {@link #bind()} instead.
     * <p>
     * Any event handler should be
     * initialised here rather than in the constructor. Also, it is good practice to
     * perform any costly initialisation here.
     * <p>
     * Handlers registered by calling
     * {@link #registerHandler(HandlerRegistration)} will be removed
     * when unbinding. Any other initialisation that takes place here (or as a
     * side-effect of what is done here) should be taken down in {@link #onUnbind()}.
     * <p>
     * This method will never be invoked more then once, or if it is, the second
     * time will necessarily be preceded by an invocation of {@link #onUnbind()}.
     */
    protected void onBind() {
    }

    /**
     * Lifecycle method called when unbinding the object.
     * <p>
     * <b>Important :</b> Make sure you call your parent class {@link #onUnbind()}.
     * Also, do not call directly, call {@link #unbind()} instead.
     * <p>
     * Any handler registration recorded with {@link #registerHandler (HandlerRegistration)}
     * will have
     * already been removed at this point. You should override this method to
     * take down any other initialisation that took place in {@link #onBind()}.
     * <p>
     * This method will never be invoked more then once, or if it is, the second
     * time will necessarily be preceded by an invocation of {@link #onBind()}.
     */
    protected void onUnbind() {
    }

    /**
     * Registers a handler so that it is automatically removed when
     * {@link #unbind()} is called. This provides an easy way to track event
     * handler registrations.
     *
     * @param handlerRegistration The registration of handler to track.
     */
    protected void registerHandler(HandlerRegistration handlerRegistration) {
        handlerRegistrations.add(handlerRegistration);
    }

    /**
     * Never call this directly. This method is used only by Guice/GIN dependency
     * injection mechanism so that {@link AutobindDisable} can be used to
     * globally turn off automatic binding.
     */
    @Inject
    final void automaticBind(AutobindDisable autobindDisable) {
        if (!autoBind || autobindDisable.disable()) {
            return;
        }
        bind();
    }

}
