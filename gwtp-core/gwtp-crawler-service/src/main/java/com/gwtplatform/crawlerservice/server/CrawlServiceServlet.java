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

package com.gwtplatform.crawlerservice.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.gwtplatform.crawlerservice.server.domain.CachedPage;
import com.gwtplatform.crawlerservice.server.service.CachedPageDao;

/**
 * Servlet that makes it possible to fetch an external page, renders it using HTMLUnit and returns
 * the HTML page.
 */
@Singleton
public class CrawlServiceServlet extends HttpServlet {

  private final String charEncoding = "UTF-8";

  private static final long serialVersionUID = -6129110224710383122L;

  @Inject(optional = true)
  @HtmlUnitTimeoutMillis
  private long timeoutMillis = 12000;

  @Inject(optional = true)
  @CachedPageTimeoutSec
  private long cachedPageTimeoutSec = 15 * 60;

  private final Provider<WebClient> webClientProvider;

  private final String key;

  private final CachedPageDao cachedPageDao;

  @Inject
  CrawlServiceServlet(final Provider<WebClient> webClientProvider,
      @ServiceKey String key,
      CachedPageDao cachedPageDao) {
    this.webClientProvider = webClientProvider;
    this.key = key;
    this.cachedPageDao = cachedPageDao;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

    try {
      resp.setCharacterEncoding(charEncoding);
      resp.setHeader("Content-Type", "text/plain; charset=utf-8");

      PrintWriter out = resp.getWriter();

      String receivedKey = URLDecoder.decode(req.getParameter("key"), charEncoding);
      if (!key.equals(receivedKey)) {
        out.println("<h3>The service key received does not match the desired key.</h3>");
      } else {
        String url = URLDecoder.decode(req.getParameter("url"), charEncoding);

        List<Key<CachedPage>> keys = cachedPageDao.listKeysByProperty("url", url);
        Map<Key<CachedPage>, CachedPage> deprecatedPages = cachedPageDao.get(keys);
        CachedPage matchingPage = null;

        // There is at most one deprecated page that matches
        for (CachedPage page : deprecatedPages.values()) {
          if (matchingPage == null ||
              page.getFetchDate().after(matchingPage.getFetchDate())) {
            matchingPage = page;
          }
        }

        // Keep the matching page only if it has not expired
        Date currDate = new Date();
        if (matchingPage == null ||
            currDate.getTime() >
                matchingPage.getFetchDate().getTime() + cachedPageTimeoutSec * 1000) {
          matchingPage = null;
        } else {
          deprecatedPages.remove(new Key<CachedPage>(CachedPage.class, matchingPage.getId()));
        }

        // Delete all deprecated cached pages
        cachedPageDao.deleteKeys(deprecatedPages.keySet());

        // If there is a matching page...
        if (matchingPage != null) {
          if (matchingPage.isFetchInProgress()) {
            // In case something went wrong during fetch, we'll consider it failed if it was more
            // than 60 seconds ago
            if (currDate.getTime() > matchingPage.getFetchDate().getTime() + 60000) {
              cachedPageDao.delete(matchingPage);
              matchingPage = null;
              // Fall-back to the regular page fetching
            } else {
              out.println("FETCH_IN_PROGRESS");
              out.close();
              return;
            }
          } else {
            out.println(matchingPage.getContent());
            out.close();
            return;
          }
        }

        // The page is not there, start fetching it

        // Store a placeholder in the datastore
        CachedPage cachedPage = new CachedPage();
        cachedPage.setUrl(url);
        cachedPage.setFetchDate(currDate);
        cachedPage.setFetchInProgress(true);
        cachedPageDao.put(cachedPage);

        // Fetch the content
        WebClient webClient = webClientProvider.get();

        webClient.setCssEnabled(false);
        webClient.setJavaScriptTimeout(0);
        webClient.setJavaScriptTimeout(0);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setJavaScriptEnabled(true);
        HtmlPage page = webClient.getPage(url);
        webClient.getJavaScriptEngine().pumpEventLoop(timeoutMillis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<hr />\n");
        stringBuilder.append("<center><h3>You are viewing a non-interactive page that is intended for the crawler.  ");
        stringBuilder.append("You probably want to see this page: <a href=\"" + url + "\">" + url + "</a></h3></center>\n");
        stringBuilder.append("<hr />\n");

        stringBuilder.append(page.asXml());
        webClient.closeAllWindows();

        // Store it in the datastore
        cachedPage.setContent(stringBuilder.toString());
        cachedPage.setFetchInProgress(false);
        cachedPageDao.put(cachedPage);

        // Send it to the client
        out.println(stringBuilder.toString());
        out.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
