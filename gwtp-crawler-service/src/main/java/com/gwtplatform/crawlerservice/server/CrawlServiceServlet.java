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

package com.gwtplatform.crawlerservice.server;

import java.util.logging.Logger;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.gwtplatform.crawler.server.AbstractCrawlServiceServlet;
import com.gwtplatform.crawlerservice.server.domain.CachedPage;
import com.gwtplatform.crawlerservice.server.service.CachedPageDao;

/**
 * Guice crawl servlet implementation.
 */
@Singleton
public class CrawlServiceServlet extends AbstractCrawlServiceServlet<CachedPage> {

    @Inject(optional = true)
    @HtmlUnitTimeoutMillis
    private long timeoutMillis = 5000;

    @Inject(optional = true)
    @CachedPageTimeoutSec
    private long cachedPageTimeoutSec = 15 * 60;

    private final Provider<WebClient> webClientProvider;

    private final CachedPageDao cachedPageDao;

    @Inject
    protected CrawlServiceServlet(
            Provider<WebClient> webClientProvider,
            Logger log,
            CachedPageDao cachedPageDao,
            @ServiceKey String key) {
        super(log, key);

        this.webClientProvider = webClientProvider;
        this.cachedPageDao = cachedPageDao;
    }

    @Override
    protected CachedPage createCrawledPage() {
        return new CachedPage();
    }

    @Override
    protected CachedPage getCachedPage(String url) {
        return cachedPageDao.get(Key.create(CachedPage.class, url));
    }

    @Override
    protected void saveCachedPage(CachedPage cachedPage) {
        cachedPageDao.put(cachedPage);
    }

    @Override
    protected void deleteCachedPage(CachedPage cachedPage) {
        cachedPageDao.delete(cachedPage);
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
