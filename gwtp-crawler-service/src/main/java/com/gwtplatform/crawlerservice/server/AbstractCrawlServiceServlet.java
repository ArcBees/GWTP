/*
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.base.Strings;

/**
 * Servlet that makes it possible to fetch an external page,
 * renders it using HTMLUnit and returns the HTML page.
 */
public abstract class AbstractCrawlServiceServlet<T extends CrawledPage> extends HttpServlet {

    private static class SyncAllAjaxController extends NicelyResynchronizingAjaxController {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean processSynchron(HtmlPage page, WebRequest request, boolean async) {
            return true;
        }
    }

    protected static final String CHAR_ENCODING = "UTF-8";

    private static final long serialVersionUID = -6129110224710383122L;

    protected final Logger log;
    protected final String key;

    protected AbstractCrawlServiceServlet(Logger log, String key) {
        this.log = log;
        this.key = key;
    }

    protected abstract T createCrawledPage();

    protected abstract T getCachedPage(String url);

    protected abstract void saveCachedPage(T crawledPage);

    protected abstract void deleteCachedPage(T crawledPage);

    protected abstract WebClient getWebClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            // Encoding needs to be set BEFORE calling response.getWriter()
            response.setCharacterEncoding(CHAR_ENCODING);
            response.setHeader("Content-Type", "text/plain; charset=" + CHAR_ENCODING);

            out = response.getWriter();
            validateKey(request);

            String url = request.getParameter("url");
            if (!Strings.isNullOrEmpty(url)) {
                url = URLDecoder.decode(url, CHAR_ENCODING);

                T crawledPage = getCachedPage(url);

                Date currDate = new Date();

                if (needToFetchPage(crawledPage, currDate, out)) {
                    crawledPage = createPlaceholderPage(url, currDate);
                    String renderedHtml = renderPage(url);
                    storeFetchedPage(crawledPage, renderedHtml);
                    out.println(renderedHtml);
                }
            }
        } catch (InvalidKeyException invalidKeyException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(invalidKeyException.getMessage());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void validateKey(HttpServletRequest request)
            throws InvalidKeyException, UnsupportedEncodingException {
        String receivedKey = request.getParameter("key");

        if (Strings.isNullOrEmpty(receivedKey)) {
            throw new InvalidKeyException("No service key attached to the request.");
        } else {
            String decodedKey = URLDecoder.decode(receivedKey, CHAR_ENCODING);

            if (!key.equals(decodedKey)) {
                throw new InvalidKeyException("The service key received does not match the desired key.");
            }
        }
    }

    private void storeFetchedPage(T crawledPage, String stringBuilder) {
        crawledPage.setContent(stringBuilder);
        crawledPage.setFetchInProgress(false);
        saveCachedPage(crawledPage);
    }

    /**
     * Checks if the page needs to be fetched. If it does not need to be fetched, but a fetch is
     * already in progress, then it prints out {@code FETCH_IN_PROGRESS} to the specified {@link PrintWriter}.
     *
     * @param matchingPage The matching page, can be {@code null} if no page matches.
     * @param currDate     The current date.
     * @param out          The {@link PrintWriter} to write to, if needed.
     * @return {@code true} if the page needs to be fetched, {@code false} otherwise.
     */
    private boolean needToFetchPage(T matchingPage, Date currDate, PrintWriter out) {
        if (matchingPage == null || matchingPage.isExpired(getCachedPageTimeoutSec())) {
            return true;
        }

        if (matchingPage.isFetchInProgress()) {
            // If fetch is in progress since more than 60 seconds, we consider something went wrong and fetch again.
            if (currDate.getTime() > matchingPage.getFetchDate().getTime() + 60000) {
                deleteCachedPage(matchingPage);
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
     * @param url      The URL of the page for which to create a placeholder.
     * @param currDate The current date, to mark the page.
     * @return The newly created placeholder page.
     */
    private T createPlaceholderPage(String url, Date currDate) {
        T result = createCrawledPage();
        result.setUrl(url);
        result.setFetchDate(currDate);
        result.setFetchInProgress(true);
        saveCachedPage(result);
        return result;
    }

    /**
     * Fetches the page at {@code url} and renders the page in a {@link StringBuilder}. The rendered page is prefixed
     * with a message indicating this is a non-interactive version.
     *
     * @param url The URL of the page to render.
     * @return The rendered page, in a {@link StringBuilder}.
     */
    private String renderPage(String url) throws IOException {
        WebClient webClient = getWebClient();

        webClient.getCache().clear();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new SyncAllAjaxController());
        webClient.setCssErrorHandler(new SilentCssErrorHandler());

        WebRequest webRequest = new WebRequest(new URL(url), "text/html");
        HtmlPage page = webClient.getPage(webRequest);
        webClient.getJavaScriptEngine().pumpEventLoop(getTimeoutMillis());

        long jsTimeoutMillis = getJsTimeoutMillis();
        int waitForBackgroundJavaScript = webClient.waitForBackgroundJavaScript(jsTimeoutMillis);
        int loopCount = 0;

        while (waitForBackgroundJavaScript > 0 && loopCount < getMaxLoopChecks()) {
            ++loopCount;
            waitForBackgroundJavaScript = webClient.waitForBackgroundJavaScript(jsTimeoutMillis);

            if (waitForBackgroundJavaScript == 0) {
                log.fine("HtmlUnit exits background javascript at loop counter " + loopCount);
                break;
            }

            synchronized (page) {
                log.fine("HtmlUnit waits for background javascript at loop counter " + loopCount);
                try {
                    page.wait(getPageWaitMillis());
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "HtmlUnit ERROR on page.wait at loop counter " + loopCount, e);
                }
            }
        }

        webClient.closeAllWindows();

        return Pattern.compile("<style>.*?</style>", Pattern.DOTALL)
                .matcher(page.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""))
                .replaceAll("");
    }

    /**
     * The HTML Unit Timeout in milliseconds.
     */
    public long getTimeoutMillis() {
        return 5000;
    }

    /**
     * The JavaScript load timeout in milliseconds.
     */
    public long getJsTimeoutMillis() {
        return 2000;
    }

    /**
     * Max page wait time in milliseconds.
     */
    public long getPageWaitMillis() {
        return 100;
    }

    /**
     * Max loop check value.
     */
    public long getMaxLoopChecks() {
        return 2;
    }

    /**
     * Cache timeout period before {@link CrawledPage}'s are invalidated.
     * @return timeout period in seconds.
     */
    public long getCachedPageTimeoutSec() {
        return 15 * 60;
    }
}
