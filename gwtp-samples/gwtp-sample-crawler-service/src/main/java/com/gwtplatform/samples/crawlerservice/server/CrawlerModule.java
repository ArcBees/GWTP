/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.samples.crawlerservice.server;

import com.google.inject.AbstractModule;
import com.gwtplatform.crawlerservice.server.ServiceKey;
import com.gwtplatform.crawlerservice.server.guice.CrawlServiceModule;

/**
 * Guice module for the crawler service.
 */
public class CrawlerModule extends AbstractModule {
  @Override
  protected void configure() {
    bindConstant().annotatedWith(ServiceKey.class).to("123456");
    install(new CrawlServiceModule());
  }
}
