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

package com.gwtplatform.crawler.server.spring.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gwtplatform.crawler.server.CrawlCacheService;
import com.gwtplatform.crawler.server.DefaultCrawlCacheService;
import com.gwtplatform.crawler.server.spring.AbstractCrawlerModule;

/**
 * Abstract crawl service module for {@link @Configuration} setup.
 * @author Ben Dol
 */
@ComponentScan(basePackages = {
        "com.gwtplatform.crawler.server.spring.service"
        })
public abstract class AbstractCrawlServiceModule extends AbstractCrawlerModule {
    @Bean
    protected WebClient webClient() {
        return new WebClient(BrowserVersion.CHROME);
    }

    @Bean
    protected CrawlCacheService crawlCacheService() {
        return new DefaultCrawlCacheService();
    }
}
