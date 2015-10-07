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

package com.gwtplatform.crawler.server.guice.service;

import java.util.logging.Logger;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.inject.Inject;
import com.gwtplatform.crawler.server.AbstractCrawlServiceServlet;
import com.gwtplatform.crawler.server.CrawlCacheService;
import com.gwtplatform.crawler.server.guice.ServiceKey;

/**
 * Guice Crawl Service Servlet.
 * @author Ben Dol
 */
@Singleton
public class CrawlServiceServlet extends AbstractCrawlServiceServlet {

    @Inject(optional = true)
    @HtmlUnitTimeoutMillis
    private long timeoutMillis = 5000;

    @Inject(optional = true)
    @CachedPageTimeoutSec
    private long cachedPageTimeoutSec = 15 * 60;

    private final Provider<WebClient> webClientProvider;

    @Inject
    protected CrawlServiceServlet(
            Provider<WebClient> webClientProvider,
            Logger log,
            CrawlCacheService crawlCacheService,
            @ServiceKey String key) {
        super(log, key, crawlCacheService);

        this.webClientProvider = webClientProvider;
    }

    @Override
    protected WebClient getWebClient() {
        return webClientProvider.get();
    }

    @Override
    public long getCachedPageTimeoutSec() {
        return cachedPageTimeoutSec;
    }

    @Override
    public long getTimeoutMillis() {
        return timeoutMillis;
    }
}
