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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this to annotate an event-handling method that should be registered in
 * the proxy rather than being registered in the presenter. Handlers annotated
 * in this way should not be registered in the Presenter.
 * <p>
 * The presenter will be instantiated as soon as the proxy intercepts the event, so
 * the presenter will handle the event even if it was not yet initialized.
 * <p>
 * Methods annotated by {@code @ProxyEvent} must return {@code void} and accept
 * a single parameter derived from {@link com.google.gwt.event.shared.GwtEvent GwtEvent}.
 * This event class must have a static {@code getType} method returning a type
 * derived from {@link com.google.gwt.event.shared.GwtEvent.Type Type}.
 */
@Target(ElementType.METHOD)
public @interface ProxyEvent {
}
