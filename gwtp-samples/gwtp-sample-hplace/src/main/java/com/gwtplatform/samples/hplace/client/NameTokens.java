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

package com.gwtplatform.samples.hplace.client;

/**
 * The central location of all name tokens for the application. All
 * {@link ProxyPlace} classes get their tokens from here. This class also makes
 * it easy to use name tokens as a resource within UIBinder xml files.
 * <p />
 * The public static final String is used within the annotation
 * {@link NameToken}, which can't use a method and the method associated with
 * this field is used within UiBinder which can't access static fields.
 * <p />
 * Also note the exclamation mark in front of the tokens, this is used for
 * search engine crawling support.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class NameTokens {
  public static final String homePage = "!homePage";

  public static final String product = "!product";

  public static final String productList = "!productList";

  public static String getHomePage() {
    return homePage;
  }

  public static String getProductListPage() {
    return productList;
  }

  public static String getProductPage() {
    return product;
  }
}