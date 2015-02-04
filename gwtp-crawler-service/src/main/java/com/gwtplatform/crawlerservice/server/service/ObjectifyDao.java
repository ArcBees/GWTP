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

import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
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
    public ObjectifyDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T get(Key<T> key) {
        return ofy().load().key(key).now();
    }

    public Key<T> put(T entity) {
        return ofy().save().entity(entity).now();
    }

    public void delete(T entity) {
        ofy().delete().entity(entity);
    }

    public Map<Key<T>, T> get(Iterable<Key<T>> keys) {
        return ofy().load().keys(keys);
    }

    protected final Objectify ofy() {
        if (lazyOfy == null) {
            lazyOfy = OfyService.ofy();
        }
        return lazyOfy;
    }
}
