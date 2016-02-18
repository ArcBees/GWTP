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

package com.gwtplatform.crawlerservice.server;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.googlecode.objectify.Key;
import com.gwtplatform.crawler.server.CrawlCacheService;
import com.gwtplatform.crawlerservice.server.domain.CachedPage;
import com.gwtplatform.crawlerservice.server.service.CachedPageDao;

/**
 * Objectify DAO Crawl Cache Service.
 * @author Ben Dol
 */
@Singleton
public class OfyCrawlCacheService implements CrawlCacheService<CachedPage> {

    private final CachedPageDao cachedPageDao;

    @Inject
    protected OfyCrawlCacheService(CachedPageDao cachedPageDao) {
        this.cachedPageDao = cachedPageDao;
    }

    @Override
    public CachedPage createCrawledPage() {
        return new CachedPage();
    }

    @Override
    public CachedPage getCachedPage(String url) {
        return cachedPageDao.get(Key.create(CachedPage.class, url));
    }

    @Override
    public void saveCachedPage(CachedPage cachedPage) {
        cachedPageDao.put(cachedPage);
    }

    @Override
    public void deleteCachedPage(CachedPage cachedPage) {
        cachedPageDao.delete(cachedPage);
    }
}
