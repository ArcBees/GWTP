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

package com.gwtplatform.crawlerservice.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
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

  private static final String CHAR_ENCODING = "UTF-8";

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

    PrintWriter out = null;
    try {
      resp.setCharacterEncoding(CHAR_ENCODING);
      resp.setHeader("Content-Type", "text/plain; charset=" + CHAR_ENCODING);

      out = resp.getWriter();

      String receivedKey = URLDecoder.decode(req.getParameter("key"), CHAR_ENCODING);
      if (!key.equals(receivedKey)) {
        out.println("<h3>The service key received does not match the desired key.</h3>");
      } else {
        String url = URLDecoder.decode(req.getParameter("url"), CHAR_ENCODING);

        List<Key<CachedPage>> keys = cachedPageDao.listKeysByProperty("url", url);
        Map<Key<CachedPage>, CachedPage> deprecatedPages = cachedPageDao.get(keys);

        Date currDate = new Date();

        CachedPage matchingPage = extractMatchingPage(deprecatedPages, currDate);
        cachedPageDao.deleteKeys(deprecatedPages.keySet());

        if (needToFetchPage(matchingPage, currDate, out)) {
          CachedPage cachedPage = createPlaceholderPage(url, currDate);
          StringBuilder renderedHtml = renderPage(url);
          storeFetchedPage(cachedPage, renderedHtml);
          out.println(renderedHtml.toString());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  private void storeFetchedPage(CachedPage cachedPage,
      StringBuilder stringBuilder) {
    cachedPage.setContent(stringBuilder.toString());
    cachedPage.setFetchInProgress(false);
    cachedPageDao.put(cachedPage);
  }

  /**
   * Checks if the page {@link matchingPage} needs to be fetched. If it does not need to be fetched,
   * but a fetch is already in progress, then it prints out {@code FETCH_IN_PROGRESS} to the
   * specified {@link PrintWriter}.
   *
   * @param matchingPage The matching page, can be {@code null} if no page matches.
   * @param currDate The current date.
   * @param out The {@link PrintWriter} to write to, if needed.
   * @return {@code true} if the page needs to be fetched, {@code false} otherwise.
   */
  private boolean needToFetchPage(CachedPage matchingPage,
      Date currDate, PrintWriter out) {

    if (matchingPage == null) {
      return true;
    }

    if (matchingPage.isFetchInProgress()) {
      // If fetch is in progress since more than 60 seconds, we consider something went wrong and fetch again.
      if (currDate.getTime() > matchingPage.getFetchDate().getTime() + 60000) {
        cachedPageDao.delete(matchingPage);
        return true;
      } else {
        out.println("FETCH_IN_PROGRESS");
        return false;
      }
    } else {
      out.println(matchingPage.getContent());
      return false;
    }
  }

  /**
   * Creates a placeholder page for the given {@code url} and stores it in the datastore.
   *
   * @param url The URL of the page for which to create a placeholder.
   * @param currDate The current date, to mark the page.
   * @return The newly created placeholder page.
   */
  private CachedPage createPlaceholderPage(String url, Date currDate) {
    CachedPage result = new CachedPage();
    result.setUrl(url);
    result.setFetchDate(currDate);
    result.setFetchInProgress(true);
    cachedPageDao.put(result);
    return result;
  }

  /**
   * Fetches the page at {@code url} and renders the page in a {@link StringBuilder}. The rendered
   * page is prefixed with a message indicating this is a non-interactive version.
   *
   * @param url The URL of the page to render.
   * @return The rendered page, in a {@link StringBuilder}.
   * @throws IOException
   * @throws MalformedURLException
   */
  private StringBuilder renderPage(String url) throws IOException,
      MalformedURLException {
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
    return stringBuilder;
  }

  /**
   * Checks if there is a page from {@code deprecatedPages} that is not expired. If there is
   * more than one, choose the most recent. If one is found it is removed from the
   * {@code deprecatedPages} list.
   *
   * @param deprecatedPages The list of pages that match the URL but that are expected to be.
   * @param currDate The current date, to check for expiration.
   * @return The non-expired matching page if found, {@code null} otherwise.
   */
  private CachedPage extractMatchingPage(Map<Key<CachedPage>, CachedPage> deprecatedPages,
      Date currDate) {
    CachedPage matchingPage = findMostRecentPage(deprecatedPages);

    // Keep the matching page only if it has not expired
    if (matchingPage == null ||
        currDate.getTime() >
            matchingPage.getFetchDate().getTime() + cachedPageTimeoutSec * 1000) {
      matchingPage = null;
    } else {
      deprecatedPages.remove(new Key<CachedPage>(CachedPage.class, matchingPage.getId()));
    }

    return matchingPage;
  }

  private CachedPage findMostRecentPage(Map<Key<CachedPage>, CachedPage> pages) {
    CachedPage result = null;
    for (CachedPage page : pages.values()) {
      if (result == null ||
          page.getFetchDate().after(result.getFetchDate())) {
        result = page;
      }
    }
    return result;
  }

}
