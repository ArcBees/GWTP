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

package com.gwtplatform.dispatch.rpc.server.spring;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

import com.gwtplatform.dispatch.rpc.server.AbstractDispatchServiceImpl;
import com.gwtplatform.dispatch.rpc.server.Dispatch;
import com.gwtplatform.dispatch.rpc.server.RequestProvider;
import com.gwtplatform.dispatch.rpc.server.logger.DispatchServiceLogHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Dispatch request to the handler.
 */
@Component("dispatch")
public class DispatchServiceImpl extends AbstractDispatchServiceImpl implements HttpRequestHandler,
        ServletContextAware {
    private static final long serialVersionUID = 136176741488585959L;

    @Value("${securityCookieName:JSESSIONID}")
    protected String securityCookieName;

    private ServletContext servletContext;

    @Autowired
    public DispatchServiceImpl(DispatchServiceLogHandler logger, Dispatch dispatch, RequestProvider requestProvider) {
        super(logger, dispatch, requestProvider);
    }

    @Override
    public String getSecurityCookieName() {
        return securityCookieName;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
