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

package com.gwtplatform.crawler.server.spring.filter;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

import com.gwtplatform.crawler.server.CrawlFilter;

/**
 * Spring implementation for the {@link CrawlFilter}.<br/>
 * Required bean dependencies are:
 * <ul>
 *     <li>serviceUrl (String): Url for the crawler service.</li>
 *     <li>crawlKey (String): Unique key for the crawler service.</li>
 *     <li>crawlLogger (Logger): Logger for the crawl filter.</li>
 * </ul>
 * Extend the {@link AbstractCrawlFilterModule} with
 * {@link org.springframework.beans.factory.annotation.Configurable} class.
 * <br/>
 * Then register inside web.xml like so:
 * <pre>
 * {@code
 * <filter>
 *          <filter-name>crawlFilter</filter-name>
 *          <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
 *     </filter>
 *     <filter-mapping>
 *         <filter-name>crawlFilter</filter-name>
 *         <url-pattern>/*</url-pattern>
 *     </filter-mapping>}
 * </pre>
 * or instead using {@link WebApplicationInitializer}:
 * <pre>
 *   servletContext.addFilter("crawlFilter", new DelegatingFilterProxy())
 *      .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
 * </pre>
 *
 * @author Ben Dol
 */
@Component("crawlFilter")
public final class SpringCrawlFilter extends CrawlFilter {

    @Autowired
    SpringCrawlFilter(String serviceUrl, String crawlKey, Logger crawlLogger) {
        super(serviceUrl, crawlKey, crawlLogger);
    }
}
