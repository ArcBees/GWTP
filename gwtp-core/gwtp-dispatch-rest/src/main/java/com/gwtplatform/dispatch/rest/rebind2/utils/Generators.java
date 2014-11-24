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
import com.gwtplatform.dispatch.rest.rebind2.Generator;

public class Generators {
    private static final Comparator<Generator> COMPARATOR = new Comparator<Generator>() {
        @Override
        public int compare(Generator o1, Generator o2) {
            return o1.getWeight() - o2.getWeight();
        }
    };

    /**
     * Get the best suited generator for the given type.
     *
     * @throws UnableToCompleteException If no generators are found.
     */
    public static <T extends Generator> T getGeneratorForType(Logger logger, Collection<T> generators, String typeName)
            throws UnableToCompleteException {
        List<T> sortedGenerators = sortGenerators(generators);

        for (T generator : sortedGenerators) {
            try {
                if (generator.canGenerate(typeName)) {
                    return generator;
                }
            } catch (UnableToCompleteException e) {
                logger.warn("Unexpected error while find a generator for '%s'", e, typeName);
            }
        }

        return logger.die("Unable to find an appropriate generator for '%s'", typeName);
    }

    /**
     * Sort the provided generators by weight without modifying the original collection.
     */
    private static <T extends Generator> List<T> sortGenerators(Collection<T> generators) {
        List<T> sortedGenerators = Lists.newArrayList(generators);
        Collections.sort(sortedGenerators, COMPARATOR);

        return sortedGenerators;
    }
}
