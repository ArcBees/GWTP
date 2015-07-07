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

import java.util.Comparator;

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.dispatch.rest.rebind.HasPriority;

public interface ContextProcessor<I, O> {
    Comparator<ContextProcessor<?, ?>> COMPARATOR = new Comparator<ContextProcessor<?, ?>>() {
        @Override
        public int compare(ContextProcessor<?, ?> p1, ContextProcessor<?, ?> p2) {
            if (p1 instanceof HasPriority && p2 instanceof HasPriority) {
                return HasPriority.COMPARATOR.compare((HasPriority) p1, (HasPriority) p2);
            }
            return 0;
        }
    };

    void init(ProcessingEnvironment processingEnv);

    boolean isInitialized();

    boolean canProcess(I context);

    O process(I context);
}
