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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind.GeneratorWithInput;
import com.gwtplatform.dispatch.rest.rebind.GeneratorWithoutInput;
import com.gwtplatform.dispatch.rest.rebind.HasPriority;

public class Generators {
    private static final Comparator<HasPriority> COMPARATOR = new Comparator<HasPriority>() {
        @Override
        public int compare(HasPriority o1, HasPriority o2) {
            return o1.getPriority() - o2.getPriority();
        }
    };

    /**
     * Get the best suited generator for the given type.
     *
     * @throws UnableToCompleteException If no generators are found.
     */
    public static <T extends HasPriority & GeneratorWithInput<? super I, ?>, I> T getGenerator(
            Logger logger, Collection<T> generators, I input) throws UnableToCompleteException {
        T generator = findGenerator(generators, input);

        if (generator == null) {
            logger.die("Unable to find an appropriate generator for '%s'", input);
        }

        return generator;
    }

    /**
     * Find the best suited generator for the given type.
     *
     * @return the best suited generator for input or {@code null} if none are found.
     */
    public static <T extends HasPriority & GeneratorWithInput<? super I, ?>, I> T findGenerator(
            Collection<T> generators, I input) {
        List<T> sortedGenerators = sortGenerators(generators);

        for (T generator : sortedGenerators) {
            if (generator.canGenerate(input)) {
                return generator;
            }
        }

        return null;
    }

    /**
     * Find the best suited generator for the given type.
     *
     * @return the best suited generator or {@code null} if none are found.
     */
    public static <T extends HasPriority & GeneratorWithoutInput<?>> T findGeneratorWithoutInput(
            Collection<T> generators) {
        List<T> sortedGenerators = sortGenerators(generators);

        for (T generator : sortedGenerators) {
            if (generator.canGenerate()) {
                return generator;
            }
        }

        return null;
    }

    /**
     * Sort the provided generators by weight without modifying the original collection.
     */
    private static <T extends HasPriority> List<T> sortGenerators(Collection<T> generators) {
        List<T> sortedGenerators = Lists.newArrayList(generators);
        Collections.sort(sortedGenerators, COMPARATOR);

        return sortedGenerators;
    }
}
