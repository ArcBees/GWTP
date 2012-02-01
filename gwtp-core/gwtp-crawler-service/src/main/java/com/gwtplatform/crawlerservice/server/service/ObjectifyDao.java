/**
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

package com.gwtplatform.crawlerservice.server.service;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;
import com.gwtplatform.crawlerservice.server.domain.CachedPage;

/**
 * Generic DAO for use with Objectify.
 *
 * @author David M. Chandler
 *
 * @param <T>
 */
public class ObjectifyDao<T> extends DAOBase {

  static final int BAD_MODIFIERS = Modifier.FINAL | Modifier.STATIC
  | Modifier.TRANSIENT;

  static
  {
    ObjectifyService.register(CachedPage.class);
  }

  protected Class<T> clazz;

  @SuppressWarnings("unchecked")
  public ObjectifyDao() {
    clazz = (Class<T>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public Key<T> put(T entity) {
    return ofy().put(entity);
  }

  public Map<Key<T>, T> putAll(Iterable<T> entities) {
    return ofy().put(entities);
  }

  public void delete(T entity) {
    ofy().delete(entity);
  }

  public void deleteKey(Key<T> entityKey) {
    ofy().delete(entityKey);
  }

  public void deleteAll(Iterable<T> entities) {
    ofy().delete(entities);
  }

  public void deleteKeys(Iterable<Key<T>> keys) {
    ofy().delete(keys);
  }

  public T get(Long id) throws EntityNotFoundException {
    return ofy().get(this.clazz, id);
  }

  public T get(Key<T> key) throws EntityNotFoundException {
    return ofy().get(key);
  }

  public Map<Key<T>, T> get(Iterable<Key<T>> keys) {
    return ofy().get(keys);
  }

  public List<T> listAll(int start, int length) {
    Query<T> q = ofy().query(clazz).offset(start).limit(length);
    return q.list();
  }

  public int countAll() {
    return ofy().query(clazz).count();
  }

   public List<T> listByProperty(String propName, Object propValue) {
     Query<T> q = ofy().query(clazz);
     q.filter(propName, propValue);
     return q.list();
   }

   public List<Key<T>> listKeysByProperty(String propName, Object propValue) {
     Query<T> q = ofy().query(clazz);
     q.filter(propName, propValue);
     return q.listKeys();
   }

   public Key<T> getKey(Long id) {
     return new Key<T>(this.clazz, id);
   }

   public Key<T> key(T obj) {
     return ObjectifyService.factory().getKey(obj);
   }

   public List<T> listChildren(Object parent) {
     return ofy().query(clazz).ancestor(parent).list();
   }

   public List<Key<T>> listChildKeys(Object parent) {
     return ofy().query(clazz).ancestor(parent).listKeys();
   }
}
