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

package com.gwtplatform.samples.tab.client;

/**
 * The central location of all name tokens for the application. All
 * {@link ProxyPlace} classes get their tokens from here. This class also makes
 * it easy to use name tokens as a resource within UIBinder xml files.
 * <p />
 * The String constants are used within the {@link NameToken} annotations, which can't
 * use a method. The static method corresponding to these constants are used within
 * UiBinder which can't access static fields.
 * <p />
 * Also note the exclamation mark in front of the tokens, this is the first step
 * towards search engine crawling support.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class NameTokens {
  public static final String dialogSamplePage = "!dialogPage";
  public static final String adminPage = "!adminPage";
  public static final String homeNewsPage = "!homeNewsPage";
  public static final String infoInfoPage = "!homeInfoPage";
  public static final String settingsPage = "!settingsPage";

  public static String getDialogSamplePage() {
    return dialogSamplePage;
  }

  public static String getAdminPage() {
    return adminPage;
  }

  public static String getHomeNewsPage() {
    return homeNewsPage;
  }

  public static String getHomeInfoPage() {
    return infoInfoPage;
  }

  public static String getSettingsPage() {
    return settingsPage;
  }

}