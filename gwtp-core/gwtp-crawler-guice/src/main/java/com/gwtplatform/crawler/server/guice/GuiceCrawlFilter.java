/*
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

package com.gwtplatform.crawler.server.guice;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.gwtplatform.crawler.server.CrawlFilter;

/**
 * Guice implementation for the {@link CrawlFilter}.
 */
@Singleton
public final class GuiceCrawlFilter extends CrawlFilter {

    @Inject
    GuiceCrawlFilter(@ServiceUrl String serviceUrl,
                     @ServiceKey String key,
                     Logger log) {
        super(serviceUrl, key, log);
    }
}
