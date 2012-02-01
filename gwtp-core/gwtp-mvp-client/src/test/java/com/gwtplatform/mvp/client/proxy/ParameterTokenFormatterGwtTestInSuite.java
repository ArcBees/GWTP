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

package com.gwtplatform.mvp.client.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.google.gwt.http.client.URL;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Unit tests for {@link ParameterTokenFormatter}.
 *
 * @author Yannis Gonianakis
 * @author Philippe Beaudoin
 */
public class ParameterTokenFormatterGwtTestInSuite extends GWTTestCase {

  ParameterTokenFormatter tokenFormatter;

  @Override
  public String getModuleName() {
    return "com.gwtplatform.mvp.MvpTest";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    tokenFormatter = new ParameterTokenFormatter();
  }

  //------------------------------------------
  // Utility methods
  //------------------------------------------
  private String urlEncodeEverything(String input) {
    return URL.encodeQueryString(URL.decodeQueryString(input));
  }

  //------------------------------------------
  // Tests for toPlaceRequest
  //------------------------------------------

  public void testToPlaceRequestEmptyPlaceToken() {
    // Given
    String placeToken = "";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertEquals(0, placeRequest.getParameterNames().size());
  }

  public void testToPlaceRequestMissingNameToken() {
    try {
      tokenFormatter.toPlaceRequest(";key1=value1;key2=value2");
      Assert.fail("TokenFormatException (Place history token is missing) was expected");
    } catch (TokenFormatException e) {
    }
  }

  public void testToPlaceRequestRegularPlaceToken() {
    // Given
    String placeToken = "nameToken;key1=value1;key2=value2";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals("value1", placeRequest.getParameter("key1", null));
    assertEquals("value2", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestPlaceTokenWithSameKeysKeepsLastValue() {
    // Given
    String placeToken = "nameToken;key1=value1;key1=value2";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(1, placeRequest.getParameterNames().size());
    assertEquals("value2", placeRequest.getParameter("key1", null));
  }

  public void testToPlaceRequestPlaceTokenWithSameValues() {
    // Given
    String placeToken = "nameToken;key1=value1;key2=value1";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals("value1", placeRequest.getParameter("key1", null));
    assertEquals("value1", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestPlaceTokenWithEmptyValue() {
    // Given
    String placeToken = "nameToken;key1=;key2=value2";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals("", placeRequest.getParameter("key1", null));
    assertEquals("value2", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestPlaceTokenWithEmptyTrailingValue() {
    // Given
    String placeToken = "nameToken;key1=value1;key2=";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals("value1", placeRequest.getParameter("key1", null));
    assertEquals("", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestPlaceTokenWithEmptyParamValues() {
    // Given
    String placeToken = "nameToken;key1=;key2=";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertTrue(placeRequest.matchesNameToken("nameToken"));
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals("", placeRequest.getParameter("key1", null));
    assertEquals("", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestPlaceTokenWithEmptyKey() {
    // Given
    String placeToken = "nameToken;=value1;key2=value2";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertEquals(2, placeRequest.getParameterNames().size());
    assertEquals(null, placeRequest.getParameter("key1", null));
    assertEquals("value1", placeRequest.getParameter("", null));
    assertEquals("value2", placeRequest.getParameter("key2", null));
  }

  public void testToPlaceRequestDiscardsTrailingParamSeparators() {
    // Given
    String placeToken = "token;a=b;;";

    // When
    PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

    // Then
    assertEquals(1, placeRequest.getParameterNames().size());
  }

  public void testToPlaceRequestPlaceTokenKeyMissingValue() {
    // Given
    String placeToken = "nameToken;key1;key2=value2";

    try {
      // When
      tokenFormatter.toPlaceRequest(placeToken);
      Assert.fail("TokenFormatException (Bad parameter) was expected.");
    } catch (TokenFormatException e) {
      // Then
    }
  }

  public void testToPlaceRequestPlaceTokenWithUnescapedValueSeparators() {
    // Given
    String[] placeTokens = {"token;=a=b", "token;a==b", "token;a=b="};

    for (String placeToken : placeTokens) {
      try {
        // When
        tokenFormatter.toPlaceRequest(placeToken);
        Assert.fail("TokenFormatException was expected for '" + placeToken + "'");
      } catch (Exception ex) {
        // Then
      }
    }
  }

  public void testToPlaceRequestPlaceTokenWithUnescapedParamSeparators() {
    // Given
    String[] placeTokens = {"token;;a=b", "token;a=b;;c=d"};

    for (String placeToken : placeTokens) {
      try {
        // When
        tokenFormatter.toPlaceRequest(placeToken);
        Assert.fail("TokenFormatException was expected for '" + placeToken + "'");
      } catch (Exception ex) {
        // Then
      }
    }
  }

  public void testToPlaceRequestIsReverseOfToPlaceToken() {

    PlaceRequest[] testCases = {
      new PlaceRequest("token").with("=a=b=", "=c=d=").with("x", "y"),
      new PlaceRequest("token").with("==a==b==", "==c==d==").with("x", "y"),
      new PlaceRequest("token").with(";a;b;", ";c;d;").with("x", "y"),
      new PlaceRequest("token").with(";;a;;b;;", ";;c;;d;;").with("x", "y"),
      new PlaceRequest("token").with("/a/b/", "/c/d/").with("x", "y"),
      new PlaceRequest("token").with("//a//b//", "//c//d//").with("x", "y"),
      new PlaceRequest("token").with("\\a\\b\\", "\\c\\d\\").with("x", "y"),
      new PlaceRequest("token").with("\\\\a\\\\b\\\\", "\\\\c\\\\d\\\\").with("x", "y"),
    };

    for (PlaceRequest placeRequestA : testCases) {
      PlaceRequest placeRequestB = tokenFormatter.toPlaceRequest(
          tokenFormatter.toPlaceToken(placeRequestA));

      for (String key : placeRequestB.getParameterNames()) {
        assertEquals(placeRequestA.getParameter(key, null), placeRequestB.getParameter(key, null));
      }

      assertEquals(placeRequestA.getParameterNames().size(),
          placeRequestB.getParameterNames().size());
    }
  }

  public void testToPlaceRequestIsReverseOfToPlaceTokenAfterFullUrlEncode() {

    PlaceRequest[] testCases = {
      new PlaceRequest("token").with("=a=b=", "=c=d=").with("x", "y"),
      new PlaceRequest("token").with("==a==b==", "==c==d==").with("x", "y"),
      new PlaceRequest("token").with(";a;b;", ";c;d;").with("x", "y"),
      new PlaceRequest("token").with(";;a;;b;;", ";;c;;d;;").with("x", "y"),
      new PlaceRequest("token").with("/a/b/", "/c/d/").with("x", "y"),
      new PlaceRequest("token").with("//a//b//", "//c//d//").with("x", "y"),
      new PlaceRequest("token").with("\\a\\b\\", "\\c\\d\\").with("x", "y"),
      new PlaceRequest("token").with("\\\\a\\\\b\\\\", "\\\\c\\\\d\\\\").with("x", "y"),
    };

    for (PlaceRequest placeRequestA : testCases) {
      PlaceRequest placeRequestB = tokenFormatter.toPlaceRequest(
          urlEncodeEverything(tokenFormatter.toPlaceToken(placeRequestA)));

      for (String key : placeRequestB.getParameterNames()) {
        assertEquals(placeRequestA.getParameter(key, null), placeRequestB.getParameter(key, null));
      }

      assertEquals(placeRequestA.getParameterNames().size(),
          placeRequestB.getParameterNames().size());
    }
  }
  //------------------------------------------
  // Tests for toPlaceToken
  //------------------------------------------

  public void testToPlaceTokenValidPlaceRequest() {
    // Given
    String expectedPlaceToken = "token;a=b";
    PlaceRequest placeRequest = new PlaceRequest("token").with("a", "b");

    // When
    String placeToken = tokenFormatter.toPlaceToken(placeRequest);

    // Then
    assertEquals(expectedPlaceToken, placeToken);
  }

  public void testToPlaceTokenShouldEscapeSeparators() {
    // Given
    String expectedPlaceToken = "token;%5C1c%5C2=%5C2d;%5C1a%5C2=%5C2b%5C1";
    PlaceRequest placeRequest = new PlaceRequest("token").with(";a=", "=b;").with(";c=","=d");

    // When
    String placeToken = tokenFormatter.toPlaceToken(placeRequest);

    // Then
    assertEquals(expectedPlaceToken, placeToken);
  }

  public void testToPlaceTokenIsReverseOfToPlaceRequest() {
    // Setup
    Map<String, String> params = new HashMap<String, String>();
    params.put(" a b ", " c d ");
    params.put("a", "b=c=d");
    params.put("a=b", "c=d");
    params.put("a=b=c", "d");
    params.put("=a", "b");
    params.put("a=", "b");
    params.put("a", "b=");
    params.put("a", "=b");
    params.put("a", "b;c;d");
    params.put("a;b", "c;d");
    params.put("a;b;c", "d");
    params.put(";;a", "b");
    params.put("a", "b;;");
    params.put("a", "b//c//d");
    params.put("a//b", "c//d");
    params.put("a//b//c", "d");
    params.put("//a", "d");
    params.put("a", "d//");
    params.put("a", "b\\\\c\\\\d");
    params.put("a\\\\b", "c\\\\d");
    params.put("a\\\\b\\\\c", "d");
    params.put("\\\\a", "d");
    params.put("a", "d\\\\");

    // Given
    ArrayList<String> testPlaceTokens = new ArrayList<String>();

    for (String key : params.keySet()) {
      // Escape separators
      String placeToken = "token;"
          + tokenFormatter.customEscape(key)
          + "="
          + tokenFormatter.customEscape(params.get(key));

      testPlaceTokens.add(placeToken);
    }

    for (String placeRequestA : testPlaceTokens) {
      // When
      String placeRequestB = tokenFormatter.toPlaceToken(
          tokenFormatter.toPlaceRequest(placeRequestA));

      // Then
      assertEquals(placeRequestA, placeRequestB);
    }
  }

  //------------------------------------------
  // Tests for toPlaceRequestHierarchy
  //------------------------------------------

  public void testToPlaceRequestHierarchyEmptyHistoryToken() {
    // Given
    String historyToken = "";

    // When
    List<PlaceRequest> placeRequests = tokenFormatter
        .toPlaceRequestHierarchy(historyToken);

    // Then
    assertEquals(1, placeRequests.size());
    assertEquals("", placeRequests.get(0).getNameToken());
    assertEquals(0, placeRequests.get(0).getParameterNames().size());
  }

  public void testToPlaceRequestHierarchyHistoryTokenWithEmptyPlaceToken() {
    // Given
    String[] historyTokens = {"/", "/t1", "t1//t2"};

    for (String historyToken : historyTokens) {
      try {
        // When
        tokenFormatter.toPlaceRequestHierarchy(historyToken);
        Assert.fail("TokenFormatException was expected for '" + historyToken + "'");
      } catch (TokenFormatException e) {
        // Then
      }
    }
  }

  public void testToPlaceRequestHierarchyValidHistoryToken() {
    // Given
    String historyToken = "t1/t2;a=b;c=d/t3;c=d";

    // When
    List<PlaceRequest> placeRequests = tokenFormatter
        .toPlaceRequestHierarchy(historyToken);

    // Then
    assertEquals(3, placeRequests.size());
    assertEquals("t1", placeRequests.get(0).getNameToken());
    assertEquals(0, placeRequests.get(0).getParameterNames().size());
    assertEquals("t2", placeRequests.get(1).getNameToken());
    assertEquals(2, placeRequests.get(1).getParameterNames().size());
    assertEquals("t3", placeRequests.get(2).getNameToken());
    assertEquals(1, placeRequests.get(2).getParameterNames().size());
  }

  public void testToPlaceRequestDiscardsTrailingHierarchySeparators() {
    // Given
    String historyToken = "t1;a=b//";

    // When
    List<PlaceRequest> placeRequests = tokenFormatter
        .toPlaceRequestHierarchy(historyToken);

    // Then
    assertEquals(1, placeRequests.size());
    assertEquals("t1", placeRequests.get(0).getNameToken());
    assertEquals(1, placeRequests.get(0).getParameterNames().size());
  }

  public void testToPlaceRequestHierarchyUnescapedHierarchySeparators() {
    // Given
    String historyToken = "t1//t2";

    try {
      // When
      tokenFormatter.toPlaceRequestHierarchy(historyToken);
      Assert.fail("TokenFormatException (Bad parameter) was expected");
    } catch (TokenFormatException e) {
      // Then
    }
  }

  public void testToPlaceRequestHierarchyIsReverseOfToHistoryToken() {
    // Given
    List<PlaceRequest> placeRequestHierarchyA = new ArrayList<PlaceRequest>();
    placeRequestHierarchyA.add(new PlaceRequest("t1\\/=;").with("a", "b"));
    placeRequestHierarchyA.add(new PlaceRequest("t2").with("c\\/=;", "d"));
    placeRequestHierarchyA.add(new PlaceRequest("t3").with("c", "d\\/=;"));
    placeRequestHierarchyA.add(new PlaceRequest("t3"));

    // When
    List<PlaceRequest> placeRequestHierarchyB = tokenFormatter.toPlaceRequestHierarchy(
        tokenFormatter.toHistoryToken(placeRequestHierarchyA));

    // Then
    assertEquals(placeRequestHierarchyA, placeRequestHierarchyB);
  }

  public void testToPlaceRequestHierarchyIsReverseOfToHistoryTokenAfterFullUrlEncode() {
    // Given
    List<PlaceRequest> placeRequestHierarchyA = new ArrayList<PlaceRequest>();
    placeRequestHierarchyA.add(new PlaceRequest("t1\\/=;").with("a", "b"));
    placeRequestHierarchyA.add(new PlaceRequest("t2").with("c\\/=;", "d"));
    placeRequestHierarchyA.add(new PlaceRequest("t3").with("c", "d\\/=;"));
    placeRequestHierarchyA.add(new PlaceRequest("t3"));

    // When
    List<PlaceRequest> placeRequestHierarchyB = tokenFormatter.toPlaceRequestHierarchy(
        urlEncodeEverything(tokenFormatter.toHistoryToken(placeRequestHierarchyA)));

    // Then
    assertEquals(placeRequestHierarchyA, placeRequestHierarchyB);
  }

  //------------------------------------------
  // Tests for toHistoryToken
  //------------------------------------------

  public void testToHistoryTokenEmptyRequestHierarchy() {
    // Given
    String expectedHistoryToken = "";
    List<PlaceRequest> placeRequestHierarchy = new ArrayList<PlaceRequest>();

    // When
    String historyToken = tokenFormatter.toHistoryToken(placeRequestHierarchy);

    // Then
    assertEquals(expectedHistoryToken, historyToken);
  }

  public void testToHistoryTokenSingleRequestInRequestHierarchy() {
    // Given
    String expectedHistoryToken = "";
    List<PlaceRequest> placeRequestHierarchy = new ArrayList<PlaceRequest>();
    placeRequestHierarchy.add(new PlaceRequest(""));

    // When
    String historyToken = tokenFormatter.toHistoryToken(placeRequestHierarchy);

    // Then
    assertEquals(expectedHistoryToken, historyToken);
  }

  public void testToHistoryTokenValidRequestHierarchy() {
    // Given
    String expectedHistoryToken = "t1;a=b/t2;c=d/t3";
    List<PlaceRequest> placeRequestHierarchy = new ArrayList<PlaceRequest>();
    placeRequestHierarchy.add(new PlaceRequest("t1").with("a", "b"));
    placeRequestHierarchy.add(new PlaceRequest("t2").with("c", "d"));
    placeRequestHierarchy.add(new PlaceRequest("t3"));

    // When
    String historyToken = tokenFormatter.toHistoryToken(placeRequestHierarchy);

    // Then
    assertEquals(expectedHistoryToken, historyToken);
  }

  public void testToHistoryTokenIsReverseOfToPlaceRequestHierarchy() {
    // Given
    String stringA = "t1;a=b/t2;c=d/t3";

    // When
    String stringB = tokenFormatter.toHistoryToken(
        tokenFormatter.toPlaceRequestHierarchy(stringA));

    // Then
    assertEquals(stringA, stringB);
  }
}