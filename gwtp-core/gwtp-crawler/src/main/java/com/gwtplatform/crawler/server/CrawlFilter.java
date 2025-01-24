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

package com.gwtplatform.crawler.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet that makes this application crawlable.
 */
@Singleton
public final class CrawlFilter implements Filter {
    private static final String CHAR_ENCODING = "UTF-8";

    /**
     * Special URL token that gets passed from the crawler to the servlet filter.
     * This token is used in case there are already existing query parameters.
     */
    private static final String ESCAPED_FRAGMENT_FORMAT1 = "_escaped_fragment_=";

    /**
     * Special URL token that gets passed from the crawler to the servlet filter.
     * This token is used in case there are not already existing query parameters.
     */
    private static final String ESCAPED_FRAGMENT_FORMAT2 = "&" + ESCAPED_FRAGMENT_FORMAT1;
    private static final int ESCAPED_FRAGMENT_LENGTH1 = ESCAPED_FRAGMENT_FORMAT1.length();
    private static final int ESCAPED_FRAGMENT_LENGTH2 = ESCAPED_FRAGMENT_FORMAT2.length();

    private final String serviceUrl;
    private final String key;
    private final Logger log;

    @Inject
    CrawlFilter(@ServiceUrl String serviceUrl,
                @ServiceKey String key,
                Logger log) {
        this.serviceUrl = serviceUrl;
        this.key = key;
        this.log = log;
    }

    /**
     * Maps from the query string that contains _escaped_fragment_ to one that
     * doesn't, but is instead followed by a hash fragment. It also unescapes any
     * characters that were escaped by the crawler. If the query string does not
     * contain _escaped_fragment_, it is not modified.
     *
     * @param queryString query string
     * @return A modified query string followed by a hash fragment if applicable.
     * The non-modified query string otherwise.
     * @throws UnsupportedEncodingException
     */
    private static String rewriteQueryString(String queryString)
            throws UnsupportedEncodingException {
        int index = queryString.indexOf(ESCAPED_FRAGMENT_FORMAT2);
        int length = ESCAPED_FRAGMENT_LENGTH2;
        if (index == -1) {
            index = queryString.indexOf(ESCAPED_FRAGMENT_FORMAT1);
            length = ESCAPED_FRAGMENT_LENGTH1;
        }
        if (index != -1) {
            StringBuilder queryStringSb = new StringBuilder();
            if (index > 0) {
                queryStringSb.append("?");
                queryStringSb.append(queryString.substring(0, index));
            }
            String hashFragmentWithoutBang = URLDecoder.decode(
                    queryString.substring(index + length, queryString.length()), CHAR_ENCODING).trim();

            if (hashFragmentWithoutBang.length() > 0) {
                queryStringSb.append("#!");
                queryStringSb.append(hashFragmentWithoutBang);
            }
            return queryStringSb.toString();
        }
        return queryString;
    }

    /**
     * Destroys the filter configuration.
     */
    @Override
    public void destroy() {
    }

    /**
     * Filters all requests and invokes the external service if necessary.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String queryString = req.getQueryString();

        // Only process calls to the main HTML page, and the empty one if desired
        final String requestURI = req.getRequestURI();

        // Does this request contain an _escaped_fragment_?
        if (queryString != null && queryString.contains(ESCAPED_FRAGMENT_FORMAT1)) {
            res.setHeader("Content-Type", "text/html; charset=" + CHAR_ENCODING);
            res.setCharacterEncoding(CHAR_ENCODING);

            PrintWriter writer = res.getWriter();
            try {
                StringBuilder pageNameSb = new StringBuilder(req.getScheme() + "://");
                pageNameSb.append(req.getServerName());
                if (req.getServerPort() != 0) {
                    pageNameSb.append(":");
                    pageNameSb.append(req.getServerPort());
                }
                pageNameSb.append(requestURI);
                queryString = rewriteQueryString(queryString);
                pageNameSb.append(queryString);
                String pageName = pageNameSb.toString();

                log.info("Crawl filter encountered escaped fragment, will open: " + pageName);

                String serviceRequest = serviceUrl + "?key=" + URLEncoder.encode(key, CHAR_ENCODING)
                                        + "&url=" + URLEncoder.encode(pageName, CHAR_ENCODING);

                log.info("Full service request: " + serviceRequest);

                // Retry until we're cut off
                while (true) {
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(serviceRequest);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);
                        connection.setRequestProperty("charset", CHAR_ENCODING);
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHAR_ENCODING));
                        String line;
                        line = reader.readLine();
                        if (!"FETCH_IN_PROGRESS".equals(line)) {
                            writer.println(line);
                            while ((line = reader.readLine()) != null) {
                                writer.println(line);
                            }
                            break;
                        }
                    } catch (IOException exception) {
                        if (!exception.getMessage().contains("Timeout")) {
                            throw exception;
                        }
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }

            log.info("Crawl filter exiting, no chaining.");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
