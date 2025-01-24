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

import com.google.web.bindery.requestfactory.shared.Locator;

/**
 * Annotation to generate
 * {@link com.google.web.bindery.requestfactory.shared.EntityProxy EntityProxy}
 * and {@link com.google.web.bindery.requestfactory.shared.ValueProxy
 * ValueProxy} classes.
 * <p>
 * Simply annotate your entity with {@code GenProxy} and gwt-platform will
 * generate you a {@link com.google.web.bindery.requestfactory.shared.ProxyFor
 * ProxyFor} interface. The generated interface equals the given examples on <a
 * href=
 * "https://developers.google.com/web-toolkit/doc/latest/DevGuideRequestFactory"
 * >DevGuideRequestFactory.</a>
 * </p>
 * <br/>
 * <p>
 * <b>Generate </b>
 * </p>
 * <br/>
 * <p>
 * <b>Fields as proxy:</b>
 * <br/>You can declare fields as proxy using {@link UseProxy}
 * or {@link UseProxyName}.
 * </p>
 * <p>If you type:</p>
 * <pre>
 * <code>
 * {@literal @}GenProxy
 * public class Person {
 *   {@literal @}UseProxyName("com.gwtplatform.dispatch.annotation.AddressProxy")
 *   Address address;
 *   {@literal @}UseProxy(DetailProxy.class)
 *   Detail detail;
 * }
 * </code>
 * </pre>
 * <p>gwt-platform will generate the following EntityProxy.</p>
 * <pre>
 * <code>
 * {@literal @}ProxyFor(com.gwtplatform.dispatch.annotation.Person.class)
 * public interface PersonProxy extends EntityProxy {
 *   EntityProxyId{@literal <}PersonProxy{@literal >} stableId();
 *
 *   com.gwtplatform.dispatch.annotation.AddressProxy getAddress();
 *   com.gwtplatform.dispatch.annotation.DetailProxy getDetail();
 *
 *   void setAddress(com.gwtplatform.dispatch.annotation.AddressProxy address);
 *   void setDetail(com.gwtplatform.dispatch.annotation.DetailProxy detail);
 * }
 * </code>
 * </pre>
 * <p>
 * <b>Using Value Proxies:</b> Set {@link #isEmbeddedType()} to true.
 * </p>
 * <br/>
 * <p>
 * <b>Note:</b> If your package name contains ".server", the processor
 * automatically replaces this with ".shared". To avoid this behaviour, set the
 * target package manual using {@link GenProxy#targetPackage()}.
 * </p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface GenProxy {
    /**
     * Defines the output package.
     * <p>
     * <p>
     * By default the output is generated to the same source folder.
     * </p>
     * <p>
     * <p>
     * If you are not using a custom target package, the processor will
     * automatically replace ".server" in your package name with ".shared".
     * </p>
     * <p>
     * <p>
     * <code>
     * {@code @GenProxy}(targetPackage = "com.example.package.server")
     * </code>
     * </p>
     */
    String targetPackage() default "";

    /**
     * Add a property name to avoid the generation of a getter for this property.
     * <p>
     * <code>
     * {@code @GenProxy}(filterGetter = {"id", "customer"})
     * </code>
     * </p>
     * The properties "id" and "customer" will have no getters.
     */
    String[] filterGetter() default {};

    /**
     * Add a property name to avoid the generation of a setter for this property.
     * <p>
     * <code>
     * {@code @GenProxy}(filterSetter = {"id", "customer"})
     * </code>
     * </p>
     * The properties "id" and "customer" will have no setters.
     */
    String[] filterSetter() default {};

    /**
     * An embedded type results in a ValueProxy instead of an EntityProxy. By
     * default, an EntityProxy is generated.
     */
    boolean isEmbeddedType() default false;

    /**
     * see ProxyFor#locator()
     */
    @SuppressWarnings("rawtypes") Class<? extends Locator> locator() default Locator.class;
}
