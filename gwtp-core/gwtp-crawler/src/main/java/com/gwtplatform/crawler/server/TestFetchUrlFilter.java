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

package com.gwtplatform.crawler.server;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.gwtplatform.dispatch.server.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>Important!</b> For testing purposes only, this class shouldn't be included
 * in official releases.
 *
 * This class demonstrates a problem with App Engine.
 * <p />
 * If you activate this filter and navigate to: <br />
 * {@code http://myapp.appspot.com?_url_=http://www.google.com} <br />
 * you will see the HTML code of the google search page.
 * <p />
 * If you navigate to: <br />
 * {@code http://myapp.appspot.com?_url_=http://myapp.appspot.com/staticPage.html}
 * <br />
 * you will see the HTML code of your static page.
 * <p />
 * If you navigate to: <br />
 * {@code http://myapp.appspot.com?_url_=http://myapp.appspot.com/dynamicPage.jsp}
 * <br />
 * the you will not see any HTML code.
 * <p />
 * Analyzing the App Engine logs (provided you configure your app to log
 * {@code Info} messages, in your {@code logging.properties} file) will show
 * that the request for {@code http://myapp.appspot.com/dynamicPage.jsp} is
 * processed <i>after</i> the request for {@code http://myapp.appspot.com} has
 * terminated (with an exception). This seems to hint at the fact that
 * AppEngine's servlet container does not allocate a new thread for the second
 * request.
 *
 * @author Philippe Beaudoin
 */
@Singleton
public final class TestFetchUrlFilter implements Filter {

  /**
   * Special URL token that gets passed from the crawler to the servlet filter.
   * This token is used in case there are already existing query parameters.
   */
  private static final String REQUEST_URL_FORMAT = "_url_=";
  private static final int REQUEST_URL_LENGTH = REQUEST_URL_FORMAT.length();

  private final Logger log;

  @Inject
  public TestFetchUrlFilter(Logger log) {
    this.log = log;
  }

  /**
   * Destroys the filter configuration.
   */
  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    log.info("Test fetch url filter starting.");

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    String queryString = req.getQueryString();

    // Does this request contain an _escaped_fragment_?
    if ((queryString != null) && (queryString.contains(REQUEST_URL_FORMAT))) {
      String pageName = queryString.substring(queryString.indexOf(REQUEST_URL_FORMAT)
          + REQUEST_URL_LENGTH);

      log.info("Test fetch url filter will open: " + pageName);

      res.setContentType("text/html;charset=UTF-8");
      PrintWriter out = res.getWriter();
      out.println("<hr />");
      out.println("<center><h3>Following is the content of the page fetched by the 'test fetch url' filter.</h3></center>");
      out.println("<hr />");
      out.println("");

      try {
        URL url = new URL(pageName);
        URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
        Future<HTTPResponse> fetchResponse = fetchService.fetchAsync(url);

        String result = new String(fetchResponse.get().getContent());
        result = result.replace("<", "&lt;").replace(">", "&gt;");
        out.println(result);

      } catch (MalformedURLException e) {
        log.severe("MalformedURLException");
        Utils.logStackTrace(log, e);
      } catch (CancellationException e) {
        log.severe("CancellationException");
        Utils.logStackTrace(log, e);
      } catch (InterruptedException e) {
        log.severe("InterruptedException");
        Utils.logStackTrace(log, e);
      } catch (ExecutionException e) {
        log.severe("ExecutionException");
        Utils.logStackTrace(log, e);
      }

      out.close();
      log.info("Test fetch url filter exiting, no chaining.");
    } else {
      // No escaped fragment, chain other filters.
      log.info("Test fetch url filter did not encounter its query parameter, chaining.");
      chain.doFilter(request, response);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

}