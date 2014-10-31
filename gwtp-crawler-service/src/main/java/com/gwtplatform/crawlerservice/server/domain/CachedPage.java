/**
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

package com.gwtplatform.crawlerservice.server.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Stores a cached version of a page.
 */
@Entity
public class CachedPage extends DatastoreObject {
    @Index
    private String url;
    private Date fetchDate;
    private boolean fetchInProgress;
    private String content;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setFetchDate(Date fetchDate) {
        this.fetchDate = new Date(fetchDate.getTime());
    }

    public Date getFetchDate() {
        return new Date(fetchDate.getTime());
    }

    public void setFetchInProgress(boolean fetchInProgress) {
        this.fetchInProgress = fetchInProgress;
    }

    public boolean isFetchInProgress() {
        return fetchInProgress;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
