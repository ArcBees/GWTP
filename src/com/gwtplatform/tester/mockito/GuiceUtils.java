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

package com.gwtplatform.tester.mockito;

import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A number of useful static methods to manipulate Guice object. Most are
 * taken from the source code of Guice, but cannot be accessed in there.
 * 
 * @author Philippe Beaudoin
 */
public class GuiceUtils {

  public static boolean isProvider(Key<?> key) {
    return key.getTypeLiteral().getRawType().equals(Provider.class);
  }

  @SuppressWarnings("unchecked")
  public static <T> TypeLiteral<T> getProvidedType(TypeLiteral<? extends Provider<? extends T>> providerTypeLiteral, 
      Errors errors) throws ErrorsException {
    Type providerType = providerTypeLiteral.getType();

    // If the Provider has no type parameter (raw Provider)...
    if (!(providerType instanceof ParameterizedType)) {
      throw errors.cannotInjectRawProvider().toException();
    }

    Type entryType = ((ParameterizedType) providerType).getActualTypeArguments()[0];
    return (TypeLiteral<T>) TypeLiteral.get(entryType);
  }

  public static <T> Key<T> getProvidedKey(Key<Provider<T>> key, Errors errors) throws ErrorsException {
    TypeLiteral<T> providedType = getProvidedType(key.getTypeLiteral(), errors);

    Key<T> providedKey;
    if (key.getAnnotation() == null) {
      providedKey = (Key<T>) Key.get(providedType);
    } else {
      providedKey = (Key<T>) Key.get(providedType, key.getAnnotation());
    }
    return providedKey;
  }

  @SuppressWarnings("unchecked")
  public static <T> Key<T> ensureProvidedKey(Key<T> key, Errors errors) {
    try {
      return isProvider(key) ? getProvidedKey((Key<Provider<T>>) key, errors) : key;
    } catch (ConfigurationException e) {
      errors.merge(e.getErrorMessages());
    } catch (ErrorsException e) {
      errors.merge(e.getErrors());
    }
    return null;
  }

  public static List<Key<?>> getMethodKeys(Method method, Errors errors) {
    Annotation allParameterAnnotations[][] = method.getParameterAnnotations();
    List<Key<?>> result = new ArrayList<Key<?>>(allParameterAnnotations.length);
    Iterator<Annotation[]> annotationsIterator = Arrays.asList(allParameterAnnotations).iterator();
    TypeLiteral<?> type = TypeLiteral.get(method.getDeclaringClass());
    for (TypeLiteral<?> parameterType : type.getParameterTypes(method)) {
      try {
        Annotation[] parameterAnnotations = annotationsIterator.next();
        result.add(Annotations.getKey(parameterType, method, parameterAnnotations, errors));            
      } catch (ConfigurationException e) {
        errors.merge(e.getErrorMessages());
      } catch (ErrorsException e) {
        errors.merge(e.getErrors());
      }   
    }
    return result;
  }
  
}
