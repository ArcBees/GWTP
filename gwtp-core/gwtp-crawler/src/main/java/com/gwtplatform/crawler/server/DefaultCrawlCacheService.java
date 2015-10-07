package com.gwtplatform.crawler.server;

public class DefaultCrawlCacheService implements CrawlCacheService<DefaultCrawledPage> {
    @Override
    public DefaultCrawledPage createCrawledPage() {
        return new DefaultCrawledPage();
    }

    @Override
    public DefaultCrawledPage getCachedPage(String url) {
        return null;
    }

    @Override
    public void saveCachedPage(DefaultCrawledPage crawledPage) { }

    @Override
    public void deleteCachedPage(DefaultCrawledPage crawledPage) { }
}
