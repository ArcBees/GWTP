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

package com.gwtplatform.samples.hplace.server.guice;

import com.gwtplatform.crawler.server.CrawlFilter;
import com.gwtplatform.crawler.server.ServiceKey;
import com.gwtplatform.crawler.server.ServiceUrl;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.shared.ActionImpl;

import com.google.inject.servlet.ServletModule;

/**
 * @author Philippe Beaudoin
 */
public class DispatchServletModule extends ServletModule {

  @Override
  public void configureServlets() {
    bindConstant().annotatedWith(ServiceKey.class).to("123456");
    bindConstant().annotatedWith(ServiceUrl.class).to("http://crawlservice.appspot.com/");
    filter("/*").through(CrawlFilter.class);
    serve("/" + ActionImpl.DEFAULT_SERVICE_NAME + "*").with(
        DispatchServiceImpl.class);
  }

}
