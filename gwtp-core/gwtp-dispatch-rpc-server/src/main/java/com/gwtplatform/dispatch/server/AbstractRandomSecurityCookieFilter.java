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

package com.gwtplatform.dispatch.server;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter will automatically inject a security cookie inside the request the first time the page is loaded. This
 * security cookie is a simple randomly generated number, and might be slightly less secure than
 * {@link HttpSessionSecurityCookieFilter}, although it will work even if you don't have access to an
 * {@link javax.servlet.http.HttpSession}. To setup this filter, add the following line at before any other
 * {@code serve} call in your own {@link com.google.inject.servlet.ServletModule#configureServlets}:
 * <p/>
 * <pre>
 * filter(&quot;*.jsp&quot;).through(HttpSessionSecurityCookieFilter.class);
 * </pre>
 * <p/>
 * You also have to use a {@code .jsp} file instead of a {@code .html} as your main GWT file.
 *
 * @author Philippe Beaudoin
 *
 * @deprecated Please use {@link com.gwtplatform.dispatch.rpc.server.AbstractRandomSecurityCookieFilter}.
 */
@Deprecated
public abstract class AbstractRandomSecurityCookieFilter implements Filter {

    protected final SecureRandom random;
    protected final String securityCookieName;

    protected AbstractRandomSecurityCookieFilter(String securityCookieName, SecureRandom random) {
        this.securityCookieName = securityCookieName;
        this.random = random;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            Cookie securityCookie = new Cookie(securityCookieName, new BigInteger(130, random).toString(32));
            securityCookie.setMaxAge(-1);
            securityCookie.setPath("/");
            httpResponse.addCookie(securityCookie);
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

}
