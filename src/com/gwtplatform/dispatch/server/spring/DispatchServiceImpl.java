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

package com.gwtplatform.dispatch.server.spring;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.AbstractDispatchServiceImpl;
import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.server.RequestProvider;

/**
 * @author Peter Simun
 */
public class DispatchServiceImpl extends AbstractDispatchServiceImpl {

  private static final long serialVersionUID = 136176741488585959L;

  @Autowired(required = false)
  protected String securityCookieName;

  @Autowired
  public DispatchServiceImpl(final Logger logger, final Dispatch dispatch, RequestProvider requestProvider) {
    super(logger, dispatch, requestProvider);
  }

  @Override
  public String getSecurityCookieName() {
    return securityCookieName;
  }
}