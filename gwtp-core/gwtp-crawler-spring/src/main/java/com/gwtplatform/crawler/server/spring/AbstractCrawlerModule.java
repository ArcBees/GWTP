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

package com.gwtplatform.crawler.server.spring;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;

/**
 * Abstract crawler module for {@link @Configuration} setup.
 * @author Ben Dol
 */
public abstract class AbstractCrawlerModule {
    @Bean
    protected Logger crawlLogger() {
        return Logger.getAnonymousLogger();
    }

    @Bean
    protected abstract String crawlKey();
}
