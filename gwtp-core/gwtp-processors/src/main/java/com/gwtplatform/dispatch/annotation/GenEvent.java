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

package com.gwtplatform.dispatch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to generate Event and Handler classes.
 * <p>
 * If you type:
 * <p>
 * <pre>
 * <code>
 * {@literal @}GenEvent
 * public class FooChanged {
 *  Foo foo;
 *  boolean originator;
 * }
 * </code>
 * </pre>
 * <p>
 * gwt-platform will generate two classes, {@code FooChangedEvent} and {@code FooChangedHandler}.
 * <p>
 * {@code FooChangedEvent} will have fields, getters, and a constructor for foo and
 * originator, plus static {@code getType()}, instance dispatch, etc., for it to
 * function correctly as a {@link com.google.gwt.event.shared.GwtEvent}.
 * <p>
 * {@code FooChangedHandler} will be an interface with a {@code onFooChanged} method that takes
 * a {@code FooChangedEvent} parameter.
 * <p>
 * <b>Notes:</b>
 * <p>
 * There is no naming requirement for your class name. It will be appended with
 * Event and Handler.
 * <p>
 * <b>Using @{@link Order}:</b>
 * <p>
 * The order the the fields can be optionally specified using the @{@link Order}
 * annotation. If @{@link Order} is not used, then the order of the parameters to the
 * constructor and to fire() is undefined.
 * <p>
 * If you type:
 * <p>
 * <pre>
 * <code>
 * {@literal @}GenEvent
 * public class FooChanged {
 *   {@literal @}Order(1) Foo foo;
 *   {@literal @}Order(2) int bar;
 *   {@literal @}Order(3) boolean originator;
 * }
 * </code>
 * </pre>
 * The following constructor and {@code fire} methods will be generated:
 * <p>
 * <pre>
 * <code>
 *  ...
 *  FooChangedEvent(Foo foo, int bar, boolean originator)
 *  ...
 *  public static void fire(HasEventBus source, Foo foo, int bar, boolean originator)
 *  ...
 * </code>
 * </pre>
 * Omitting the @{@link Order} annotations would yield:
 * <p>
 * <pre>
 * <code>
 *  ...
 *  FooChangedEvent(int bar, Foo foo, boolean originator)
 *  ...
 *  public static void fire(HasEventBus source, int bar, Foo foo, boolean originator)
 *  ...
 * </code>
 * </pre>
 * <p>
 * <p>
 * <b>Using @{@link Optional}:</b>
 * <p>
 * If @{@link Optional} is used together with @{@link GenEvent}, an additional fire method is generated.
 * If you type:
 * <p>
 * <pre>
 * <code>
 * {@literal @}GenEvent
 * public class FooChanged {
 *  {@literal @}Optional @Order(1) Foo foo;
 *  {@literal @}Order(2) int bar;
 *  {@literal @}Optional @Order(3) boolean originator;
 * }
 * </code>
 * </pre>
 * The following constructors and {@code fire} methods will be generated:
 * <p>
 * <pre>
 * <code>
 *  ...
 *  FooChangedEvent(int bar)
 *  FooChangedEvent(Foo foo, int bar, boolean originator)
 *  ...
 *  public static void fire(HasEventBus source, int bar)
 *  public static void fire(HasEventBus source, Foo foo, int bar, boolean originator)
 *  ...
 * </code>
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface GenEvent {
}
