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
 * Annotation to generate simple Data Transfer Object (DTO) classes solely for
 * transferring data between the client and the server.
 * <p>
 * If you type:
 * <p>
 * <pre>
 * <code>
 * {@literal}@GenDto
 * public class LineItem {
 *   Key&lt;Product&gt; productKey;
 *   int quantity;
 * }
 * </code>
 * </pre>
 * <p>
 * gwt-platform will generate a {@code LineItemDto} class.
 * <p>
 * {@code LineItemDto} will have fields, getters, and a constructor that takes
 * {@code productKey} and quantity plus equals, hashCode, toString etc,
 * <p>
 * {@code LineItemDto} could be used when creating an invoice.
 * <p>
 * <pre>
 * <code>
 * {@literal}@GenDispatch
 * public class CreateInvoice {
 *   {@literal}@In Key&lt;Customer&gt; customerKey
 *   {@literal}@In LineItemDto[] lineItems
 *   {@literal}@Out Invoice invoice;
 * }
 * </code>
 * </pre>
 * <p>
 * The alternative to using Dto classes would be to construct persistable
 * {@code LineItem} objects on the client and send them as part of the gwt-rpc call.
 * Using Dto classes is a better choice because:
 * <ul>
 * <li>Lower bandwidth.</li>
 * <li>The client cannot be trusted. The {@code LineItem} persistable object will
 * probably have a price field. The server should ignore the price as it may
 * have been tampered with.</li>
 * </ul>
 * <p>
 * <p>
 * Notes:
 * <p>
 * There is no naming requirement for your class name. It will be appended with
 * Dto
 * <p>
 * <p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GenDto {
}
