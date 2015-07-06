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
import com.google.common.collect.Ordering;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessor;
import com.gwtplatform.dispatch.rest.rebind.HasPriority;

public class ContextProcessors {
    private static class Loaders {
        private static Loaders instance;

        private final Map<Class<?>, ServiceLoader<? extends ContextProcessor<?, ?>>> serviceLoaders;

        private Loaders() {
            this.serviceLoaders = new HashMap<>();
        }

        public static Loaders get() {
            if (instance == null) {
                instance = new Loaders();
            }

            return instance;
        }

        @SuppressWarnings("unchecked")
        public <C extends ContextProcessor<?, ?>> ServiceLoader<C> get(Class<C> clazz) {
            if (!serviceLoaders.containsKey(clazz)) {
                // In the processor context, the current thread class loader can't load SPIs
                serviceLoaders.put(clazz, ServiceLoader.load(clazz, getClass().getClassLoader()));
            }

            return (ServiceLoader<C>) serviceLoaders.get(clazz);
        }
    }

    private final ProcessingEnvironment processingEnvironment;
    private final Logger logger;
    private final Loaders loaders;

    public ContextProcessors(ProcessingEnvironment processingEnv) {
        this.processingEnvironment = processingEnv;
        this.logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        this.loaders = Loaders.get();
    }

    public ResourceMethodProcessor getResourceMethodProcessor(ResourceMethodContext context) {
        return getProcessor(ResourceMethodProcessor.class, context);
    }

    public <P extends ContextProcessor<I, ?> & HasPriority, I> P getProcessor(Class<P> clazz, final I input) {
        ServiceLoader<P> processors = loaders.get(clazz);
        Optional<P> processor = FluentIterable.from(sort(processors))
                .firstMatch(new Predicate<P>() {
                    @Override
                    public boolean apply(P processor) {
                        return canProcess(processor, input);
                    }
                });

        if (!processor.isPresent()) {
            logger.error("Can not find a `%s` for input `%s`.", clazz.getSimpleName(), input);
        }
        return processor.orNull();
    }

    private <P extends HasPriority> Iterable<P> sort(Iterable<P> items) {
        return Ordering.from(HasPriority.COMPARATOR).sortedCopy(items);
    }

    private <P extends ContextProcessor<I, ?>, I> boolean canProcess(P processor, I input) {
        if (!processor.isInitialized()) {
            processor.init(processingEnvironment);
        }

        return processor.canProcess(input);
    }
}
