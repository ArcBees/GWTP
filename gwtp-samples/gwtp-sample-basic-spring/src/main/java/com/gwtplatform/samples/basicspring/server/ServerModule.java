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

package com.gwtplatform.samples.basicspring.server;

import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.server.spring.HandlerModule;
import com.gwtplatform.dispatch.server.spring.actionvalidator.DefaultActionValidator;
import com.gwtplatform.dispatch.server.spring.configuration.DefaultModule;
import com.gwtplatform.samples.basicspring.shared.SendTextToServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Module which binds the handlers and configurations.

 * @author Philippe Beaudoin
 */
@Configuration
@Import(DefaultModule.class)
public class ServerModule extends HandlerModule {

  public ServerModule() {
  }

  @Bean
  public SendTextToServerHandler getSendTextToServerHandler() {
    return new SendTextToServerHandler();
  }

  @Bean
  public ActionValidator getDefaultActionValidator() {
    return new DefaultActionValidator();
  }

  protected void configureHandlers() {
    bindHandler(SendTextToServer.class, SendTextToServerHandler.class);
  }
}
