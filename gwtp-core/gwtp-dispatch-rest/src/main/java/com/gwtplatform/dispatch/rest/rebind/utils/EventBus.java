/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    public interface Event<L> {
        void dispatch(L listener);
    }

    public class ListenerRegistration {
        private final List<Object> collection;
        private final Object listener;

        public ListenerRegistration(
                List<Object> collection,
                Object listener) {
            this.collection = collection;
            this.listener = listener;
        }

        public boolean unregister() {
            return collection.remove(listener);
        }
    }

    private final Map<Class<?>, List<Object>> listenersByEvent = new HashMap<Class<?>, List<Object>>();

    public <E extends Event<L>, L> ListenerRegistration register(Class<E> eventClass, L listener) {
        List<Object> listeners = listenersByEvent.get(eventClass);
        if (listeners == null) {
            listeners = new ArrayList<Object>();
            listenersByEvent.put(eventClass, listeners);
        }

        listeners.add(listener);

        return new ListenerRegistration(listeners, listener);
    }

    @SuppressWarnings("unchecked")
    public <L> void post(Event<L> event) {
        Class<?> eventClass = event.getClass();
        if (listenersByEvent.containsKey(eventClass)) {
            for (Object listener : listenersByEvent.get(eventClass)) {
                event.dispatch((L) listener);
            }
        }
    }
}
