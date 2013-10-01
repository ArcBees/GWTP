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

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.gwtplatform.crawlerservice.server.objectify.OfyService;

/**
 * Generic DAO for use with Objectify.
 *
 * @param <T> The type of the entity handled by this DAO.
 */
public abstract class ObjectifyDao<T> {
    protected final Class<T> clazz;

    private Objectify lazyOfy;

    @SuppressWarnings("unchecked")
    public ObjectifyDao() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public LoadType<T> load() {
        return ofy().load().type(clazz);
    }

    public Key<T> put(T entity) {
        return ofy().save().entity(entity).now();
    }

    public Map<Key<T>, T> putAll(Iterable<T> entities) {
        return ofy().save().entities(entities).now();
    }

    public void delete(T entity) {
        ofy().delete().entity(entity);
    }

    public void deleteKey(Key<T> entityKey) {
        ofy().delete().entity(entityKey);
    }

    public void deleteAll(Iterable<T> entities) {
        ofy().delete().entities(entities);
    }

    public void deleteKeys(Iterable<Key<T>> keys) {
        ofy().delete().keys(keys);
    }

    public T get(Long id) {
        return load().id(id).now();
    }

    public T get(Key<T> key) {
        return ofy().load().key(key).now();
    }

    public Map<Key<T>, T> get(Iterable<Key<T>> keys) {
        return ofy().load().keys(keys);
    }

    public List<T> listAll(int start, int length) {
        Query<T> q = load().offset(start).limit(length);
        return q.list();
    }

    public int countAll() {
        return load().count();
    }

    public List<T> listByProperty(String propName, Object propValue) {
        Query<T> q = load();
        q.filter(propName, propValue);
        return q.list();
    }

    public List<Key<T>> listKeysByProperty(String propName, Object propValue) {
        Query<T> q = load();
        q.filter(propName, propValue);
        return q.keys().list();
    }

    public Key<T> getKey(Long id) {
        return Key.create(clazz, id);
    }

    public List<T> listChildren(Object parent) {
        return load().ancestor(parent).list();
    }

    public List<Key<T>> listChildKeys(Object parent) {
        return load().ancestor(parent).keys().list();
    }

    protected Objectify ofy() {
        if (lazyOfy == null) {
            lazyOfy = OfyService.ofy();
        }
        return lazyOfy;
    }
}
