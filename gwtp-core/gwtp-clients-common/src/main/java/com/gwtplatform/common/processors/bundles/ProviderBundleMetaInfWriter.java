/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.common.processors.bundles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;

import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;

import static com.gwtplatform.processors.tools.outputter.OutputType.META_INF;

class ProviderBundleMetaInfWriter {
    private final Logger logger;
    private final Outputter outputter;
    private final Map<String, BufferedWriter> writers = new HashMap<>();

    ProviderBundleMetaInfWriter(
            Logger logger,
            Outputter outputter) {
        this.logger = logger;
        this.outputter = outputter;
    }

    public void addBundle(String name, String type) {
        try {
            BufferedWriter writer = getOrCreateWriter(name);
            writer.write(type);
            writer.newLine();
        } catch (IOException e) {
            logger.error().throwable(e).log("Can't append bundle '%s' to meta information.", name);
            throw new UnableToProcessException();
        }
    }

    private BufferedWriter getOrCreateWriter(String bundleName) throws IOException {
        BufferedWriter writer;

        if (writers.containsKey(bundleName)) {
            writer = writers.get(bundleName);
        } else {
            FileObject file = outputter.prepareSourceFile(new Type("", "gwtp/bundles/" + bundleName), META_INF);
            writer = new BufferedWriter(file.openWriter());
            writers.put(bundleName, writer);
        }

        return writer;
    }

    public void close() {
        for (BufferedWriter writer : writers.values()) {
            try {
                writer.close();
            } catch (IOException ignore) {
            }
        }
    }
}
