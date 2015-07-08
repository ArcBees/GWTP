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

package com.gwtplatform.dispatch.rest.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

public class ContextProcessors {
    private static class Loaders {
        private static Loaders instance;

        private final Map<Class<?>, ServiceLoader<? extends ContextProcessor<?, ?>>> serviceLoaders;

        private Loaders() {
            this.serviceLoaders = new HashMap<>();
        }

        public static synchronized Loaders get() {
            if (instance == null) {
                instance = new Loaders();
            }

            return instance;
        }

        @SuppressWarnings("unchecked")
        public synchronized <C extends ContextProcessor<?, ?>> ServiceLoader<C> get(Class<C> clazz) {
            if (!serviceLoaders.containsKey(clazz)) {
                // In the processor context, the current thread class loader can't load SPIs
                serviceLoaders.put(clazz, ServiceLoader.load(clazz, getClass().getClassLoader()));
            }

            return (ServiceLoader<C>) serviceLoaders.get(clazz);
        }
    }

    private final ProcessingEnvironment processingEnv;
    private final Logger logger;
    private final Loaders loaders;

    public ContextProcessors(
            ProcessingEnvironment processingEnv,
            Logger logger) {
        this.processingEnv = processingEnv;
        this.logger = logger;
        this.loaders = Loaders.get();
    }

    public <P extends ContextProcessor<I, ?>, I> P getProcessor(Class<P> clazz, final I input) {
        Optional<P> processor = getOptionalProcessor(clazz, input);

        if (!processor.isPresent()) {
            logger.error("Can not find a `%s` for input `%s`.", clazz.getSimpleName(), input);
            throw new UnableToProcessException();
        }

        return processor.get();
    }

    public <P extends ContextProcessor<I, ?>, I> Optional<P> getOptionalProcessor(Class<P> clazz, I input) {
        return FluentIterable.from(getProcessors(clazz, input)).first();
    }

    public <P extends ContextProcessor<I, ?>, I> Iterable<P> getProcessors(Class<P> clazz, final I input) {
        return FluentIterable.from(getProcessors(clazz))
                .filter(new Predicate<P>() {
                    @Override
                    public boolean apply(P processor) {
                        return canProcess(processor, input);
                    }
                })
                .toSortedList(ContextProcessor.COMPARATOR);
    }

    public <P extends ContextProcessor<?, ?>> Iterable<P> getProcessors(Class<P> clazz) {
        return loaders.get(clazz);
    }

    private <P extends ContextProcessor<I, ?>, I> boolean canProcess(P processor, I input) {
        ensureInitialized(processor);

        return processor.canProcess(input);
    }

    private synchronized  <P extends ContextProcessor<?, ?>> void ensureInitialized(P processor) {
        if (!processor.isInitialized()) {
            processor.init(processingEnv);
        }
    }
}
