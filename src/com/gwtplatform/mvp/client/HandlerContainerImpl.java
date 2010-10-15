/**
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

package com.gwtplatform.mvp.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import java.util.List;

/**
 * The implementation of {@link HandlerContainer}. Inherit from this class if
 * you want subclasses that can contain handlers.
 * 
 * @author Philippe Beaudoin
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

  private final transient List<HandlerRegistration> handlerRegistrations = new java.util.ArrayList<HandlerRegistration>();

  /**
   * Creates a handler container class with automatic binding.
   * 
   * @see #HandlerContainer(boolean autoBind )
   */
  @Inject
  public HandlerContainerImpl() {
    this(true);
  }

  /**
   * Creates a handler container class with or without automatic binding. If
   * automatic binding is requested, the {@link #bind()} method will be called
   * automatically after the class is instantiated through Guice/GIN dependency
   * injection mechanism. Otherwise, the user is responsible for calling
   * {@link #bind()}.
   * 
   * @param autoBind True to request automatic binding, false otherwise.
   */
  public HandlerContainerImpl(boolean autoBind) {
    super();
    this.autoBind = autoBind;
  }

  @Override
  public final void bind() {
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
  public final void unbind() {
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
   * <b>Important :</b> Make sure you call your parent class onBind(). Also, do
   * not call directly, call {@link bind()} instead.
   * <p />
   * This method is called when binding the object. Any event handlers should be
   * initialised here rather than in the constructor. Other costly
   * initialisation should be done here too, in order to speed-up construction.
   * <p />
   * Handlers registered by calling
   * {@link #registerHandler (HandlerRegistration)} will be removed
   * automatically. Any other initialisation that takes place here (or as a
   * side-effect of what is done here) should be taken down in {@link #unbind()}.
   * <p />
   * This method will never be invoked more then once, or if it is, the second
   * time will necessarily be preceded by an invocation of {@link #unbind()}.
   */
  protected void onBind() {
  }

  /**
   * <b>Important :</b> Make sure you call your parent class onUnbind(). Also,
   * do not call directly, call {@link unbind()} instead.
   * <p />
   * This method is called when unbinding the object. Any handler registrations
   * recorded with {@link #registerHandler (HandlerRegistration)} will have
   * already been removed at this point. You should take down any other
   * initialisation that took place in {@link #unbind()}.
   * <p />
   * This method will never be invoked more then once, or if it is, the second
   * time will necessarily be preceded by an invocation of {@link #bind()}.
   */
  protected void onUnbind() {
  }

  /**
   * Any {@link HandlerRegistration}s added will be removed when
   * {@link #unbind()} is called. This provides a handy way to track event
   * handler registrations when binding and unbinding.
   * 
   * @param handlerRegistration The registration.
   */
  protected void registerHandler(HandlerRegistration handlerRegistration) {
    handlerRegistrations.add(handlerRegistration);
  }

  /**
   * Never call this directly. This method is used only by Guice/GIN dependency
   * injection mechanism.
   */
  @Inject
  final void automaticBind(AutobindDisable autobindDisable) {
    if (!autoBind || autobindDisable.disable()) {
      return;
    }
    bind();
  }

}