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

package com.gwtplatform.crawler.server;

import java.util.Date;

/**
 * Default crawled page implementation.
 * @author Ben Dol
 */
public class DefaultCrawledPage implements CrawledPage {
    private String url;
    private Date fetchDate;
    private boolean fetchInProgress;
    private String content;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setFetchDate(Date fetchDate) {
        this.fetchDate = new Date(fetchDate.getTime());
    }

    @Override
    public Date getFetchDate() {
        return new Date(fetchDate.getTime());
    }

    @Override
    public void setFetchInProgress(boolean fetchInProgress) {
        this.fetchInProgress = fetchInProgress;
    }

    @Override
    public boolean isFetchInProgress() {
        return fetchInProgress;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean isExpired(long cachedPageTimeoutSec) {
        return new Date().getTime() > fetchDate.getTime() + cachedPageTimeoutSec * 1000;
    }
}
