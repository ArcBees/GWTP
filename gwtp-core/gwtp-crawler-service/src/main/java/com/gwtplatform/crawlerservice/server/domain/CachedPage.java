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

/**
 * Stores a cached version of a page.
 *
 * @author Philippe Beaudoin
 */
public class CachedPage extends DatastoreObject {
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
    this.fetchDate = fetchDate;
  }

  public Date getFetchDate() {
    return fetchDate;
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
