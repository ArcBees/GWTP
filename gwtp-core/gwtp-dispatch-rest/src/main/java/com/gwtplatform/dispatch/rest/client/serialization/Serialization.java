/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import java.util.List;

import com.gwtplatform.dispatch.rest.shared.ContentType;

/**
 * Offers basic serialization methods. The results depends on the implementation used.
 *
 * @see JsonSerialization
 */
public interface Serialization {
    // Storing the parameterized type as a string works for limited use cases.
    // Guice's TypeLiteral has very limited uses client-side (.equals() doesn't work).
    // We may create a custom TypeLiteral that fits our need when it is required.

    /**
     * Verify if the given parameterized type can be serialized.
     *
     * @param parameterizedType the parameterized type.
     * @param contentTypes a list of content acceptable {@link ContentType content types} for the serialized output.
     * Usage of wildcards is allowed (eg.: application/*).
     *
     * @return {@code true} if <code>parameterizedType</code> can be serialized, otherwise {@code false}.
     */
    boolean canSerialize(String parameterizedType, List<ContentType> contentTypes);

    /**
     * Serializes the object as a type represented by <code>parameterizedType</code>.
     *
     * @param <T> the type of the object.
     * @param parameterizedType the parameterized type of the object to serialize.
     * @param contentTypes the {@link ContentType content types} allowed for the serialized value. Usage of wildcards is
     * allowed (eg.: application/*).
     * @param object the object to serialized.
     *
     * @return the {@link SerializedValue} resulting from the serialization of <code>object</code>.
     */
    <T> SerializedValue serialize(String parameterizedType, List<ContentType> contentTypes, T object);

    /**
     * Verify if the given parameterized type can be deserialized.
     *
     * @param parameterizedType the parameterized type.
     * @param contentType the {@link ContentType content type} of the checked input. Usage of wildcards is <b>NOT</b>
     * allowed (eg.: application/*).
     *
     * @return {@code true} if an object of type <code>parameterizedType</code> with <code>contentType</code> can be
     * deserialized, otherwise {@code false}.
     */
    boolean canDeserialize(String parameterizedType, ContentType contentType);

    /**
     * Deserializes the object to the type represented by <code>parameterizedType</code>.
     *
     * @param <T> the return type.
     * @param parameterizedType the parameterized type of the expected return type.
     * @param contentType the {@link ContentType content type} of <code>serializedObject</code>. Usage of wildcards is
     * <b>NOT</b> allowed (eg.: application/*).
     * @param serializedObject the String representing the serialized object.
     *
     * @return The deserialized object of type <code>T</code>.
     */
    <T> T deserialize(String parameterizedType, ContentType contentType, String serializedObject);
}
