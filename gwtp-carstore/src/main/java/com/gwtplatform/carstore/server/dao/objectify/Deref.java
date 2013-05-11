/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.server.dao.objectify;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;

public class Deref {
    public static class Func<T> implements Function<Ref<T>, T> {
        public static Func<Object> INSTANCE = new Func<Object>();

        @Override
        public T apply(Ref<T> ref) {
            return deref(ref);
        }
    }

    public static <T> T deref(Ref<T> ref) {
        return ref == null ? null : ref.get();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> deref(List<Ref<T>> reflist) {
        if (reflist == null) {
            return null;
        }

        return Lists.transform(reflist, (Func) Func.INSTANCE);
    }
}
