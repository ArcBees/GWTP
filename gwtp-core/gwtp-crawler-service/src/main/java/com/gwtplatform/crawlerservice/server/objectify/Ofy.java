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

package com.gwtplatform.crawlerservice.server.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;

/**
 */
public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory> {
    public Ofy(Objectify base) {
        super(base);
    }

    public <T> LoadType<T> query(Class<T> clazz) {
        return load().type(clazz);
    }

    public <T> T get(Key<T> key) {
        return load().key(key).get();
    }

    public <T> T get(Class<T> clazz, long id) {
        return load().type(clazz).id(id).get();
    }
}
