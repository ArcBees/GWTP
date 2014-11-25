/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind2.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind2.GeneratorWithInput;
import com.gwtplatform.dispatch.rest.rebind2.GeneratorWithoutInput;
import com.gwtplatform.dispatch.rest.rebind2.HasWeight;

public class Generators {
    private static final Comparator<HasWeight> COMPARATOR = new Comparator<HasWeight>() {
        @Override
        public int compare(HasWeight o1, HasWeight o2) {
            return o1.getWeight() - o2.getWeight();
        }
    };

    /**
     * Get the best suited generator for the given type.
     *
     * @throws UnableToCompleteException If no generators are found.
     */
    public static <T extends HasWeight & GeneratorWithInput<I>, I> T getFirstGeneratorByWeightAndInput(Logger logger,
            Collection<T> generators, I input) throws UnableToCompleteException {
        List<T> sortedGenerators = sortGenerators(generators);

        return getFirstGeneratorByInput(logger, sortedGenerators, input);
    }

    public static <T extends HasWeight & GeneratorWithInput<I>, I> T findFirstGeneratorByWeightAndInput(Logger logger,
            Collection<T> generators, I input) {
        List<T> sortedGenerators = sortGenerators(generators);

        return findFirstGeneratorByInput(logger, sortedGenerators, input);
    }

    public static <T extends GeneratorWithInput<I>, I> T getFirstGeneratorByInput(Logger logger,
            Collection<T> generators, I input) throws UnableToCompleteException {
        T generator = findFirstGeneratorByInput(logger, generators, input);

        if (generator != null) {
            return generator;
        }

        return logger.die("Unable to find an appropriate generator for '%s'", input);
    }

    public static <T extends GeneratorWithInput<I>, I> T findFirstGeneratorByInput(Logger logger,
            Collection<T> generators, I input) {
        for (T generator : generators) {
            try {
                if (generator.canGenerate(input)) {
                    return generator;
                }
            } catch (UnableToCompleteException e) {
                logger.warn("Unexpected exception", e);
            }
        }

        return null;
    }

    public static <T extends HasWeight & GeneratorWithoutInput> T getFirstGeneratorByWeight(Logger logger,
            Collection<T> generators) throws UnableToCompleteException {
        List<T> sortedGenerators = sortGenerators(generators);

        return getFirstGenerator(logger, sortedGenerators);
    }

    public static <T extends GeneratorWithoutInput> T getFirstGenerator(Logger logger, Collection<T> generators)
            throws UnableToCompleteException {
        for (T generator : generators) {
            try {
                if (generator.canGenerate()) {
                    return generator;
                }
            } catch (UnableToCompleteException e) {
                logger.warn("Unexpected exception", e);
            }
        }

        return logger.die("");
    }

    /**
     * Sort the provided generators by weight without modifying the original collection.
     */
    private static <T extends HasWeight> List<T> sortGenerators(Collection<T> generators) {
        List<T> sortedGenerators = Lists.newArrayList(generators);
        Collections.sort(sortedGenerators, COMPARATOR);

        return sortedGenerators;
    }
}
