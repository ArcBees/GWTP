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

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.WebApplicationInitializer;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gwtplatform.crawler.server.AbstractCrawlServiceServlet;
import com.gwtplatform.crawler.server.CrawlCacheService;
import com.gwtplatform.crawler.server.CrawledPage;

/**
 * Spring Crawl Service Servlet.<br/>
 * Required bean dependencies are:
 * <ul>
 *     <li>webClient ({@link WebClient}): HTML Unit virtual web client.</li>
 *     <li>crawlCacheService ({@link CrawlCacheService}): Crawled page cache service.</li>
 *     <li>crawlKey (String): Unique key for the crawler service.</li>
 *     <li>crawlLogger (Logger): Logger for the crawl filter.</li>
 *     <li>timeoutMillis (long:5000): The HTML Unit Timeout in milliseconds.</li>
 *     <li>cachedPageTimeoutSec (long:900): Cache timeout period before {@link CrawledPage}'s are invalidated.</li>
 * </ul>
 * Extend the {@link AbstractCrawlServiceModule} with
 * {@link org.springframework.beans.factory.annotation.Configurable} class.
 * <br/>
 * Then register in web.xml like so:
 * <pre>
 * {@code
 *  <-- First ensure you have the ContextLoaderListener -->
 *     <listener>
 *         <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
 *     </listener>
 *
 *     <servlet>
 *          <servlet-name>crawlServiceServlet</servlet-name>
 *          <servlet-class>org.springframework.web.context.support.HttpRequestHandlerServlet</servlet-class>
 *     </servlet>
 *
 *     <servlet-mapping>
 *         <servlet-name>crawlServiceServlet</filter-name>
 *         <url-pattern>/*</url-pattern>
 *     </servlet-mapping>}
 * </pre>
 * or using {@link WebApplicationInitializer}:
 * <pre>
 *   // Ensure you have registered the ContextLoaderListener
 *   servletContext.addListener(new ContextLoaderListener(context));
 *
 *   // Register the new servlet as a HttpRequestHandlerServlet
 *   servletContext.addServlet("crawlServiceServlet", new HttpRequestHandlerServlet()).addMapping("/*");
 * </pre>
 *
 * @author Ben Dol
 */
@Component
public class CrawlServiceServlet extends AbstractCrawlServiceServlet implements HttpRequestHandler {

    @Value("${timeoutMillis:5000}")
    private long timeoutMillis;

    @Value("${cachedPageTimeoutSec:900}")
    private long cachedPageTimeoutSec;

    private final WebClient webClient;

    @Autowired
    protected CrawlServiceServlet(
            WebClient webClient,
            CrawlCacheService crawlCacheService,
            String crawlKey,
            Logger crawlLogger) {
        super(crawlLogger, crawlKey, crawlCacheService);

        this.webClient = webClient;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected WebClient getWebClient() {
        return webClient;
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
