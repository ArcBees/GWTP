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

package com.gwtplatform.mvp.client.proxy;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Formats tokens from {@code String} values to {@link PlaceRequest} and
 * {@link PlaceRequest} hierarchies and vice-versa. The default implementation
 * uses:
 * <ul>
 * <li>{@code '/'} to separate {@link PlaceRequest}s in a hierarchy;</li>
 * <li>{@code ';'} to separate parameters in a {@link PlaceRequest};</li>
 * <li>{@code '='} to separate the parameter name from its value.</li>
 * </ul>
 * These symbols cannot be used in a name token. If one of the separating symbol
 * is encountered in a parameter or a value it is escaped by being duplicated.
 * <p />
 * For example, {@link ParameterTokenFormatter} would parse:
 * 
 * <pre>
 * nameToken1;param1.1=value1.1;param1.2=value1.2/nameToken2/nameToken3;param3.1=value//3==1
 * </pre>
 * Into the following hierarchy of {@link PlaceRequest}:
 * 
 * <pre>
 * { 
 *   { "nameToken1", { {"param1.1", "value1.1"}, {"parame1.2","value1.2"} },
 *     "nameToken2", {},
 *     "nameToken3", { {"param3.1", "value/3=1"} } }
 * }
 * </pre>
 * If you want to use different symbols as separator, use the
 * {@link #ParameterTokenFormatter(String, String, String)} constructor.
 * 
 * @author Philippe Beaudoin
 */
public final class ParameterTokenFormatter implements TokenFormatter {

  protected static final String DEFAULT_HIERARCHY_SEPARATOR = "/";
  protected static final String DEFAULT_PARAM_SEPARATOR = ";";
  protected static final String DEFAULT_VALUE_SEPARATOR = "=";

  private final String hierarchyEscape;
  private final String hierarchyPattern;
  private final String hierarchySeparator;
  private final String paramEscape;
  private final String paramPattern;
  private final String paramSeparator;
  private final String valueEscape;
  private final String valuePattern;
  private final String valueSeparator;

  /**
   * Builds a {@link ParameterTokenFormatter} using the default separators.
   */
  @Inject
  public ParameterTokenFormatter() {
    this(DEFAULT_HIERARCHY_SEPARATOR, DEFAULT_PARAM_SEPARATOR,
        DEFAULT_VALUE_SEPARATOR);
  }

  /**
   * This constructor makes it possible to use custom separators in your token
   * formatter. The separators must be 1-letter strings and they must all be
   * different from one another.
   * 
   * @param hierarchySeparator The symbol used to separate {@link PlaceRequest}
   *          in a hierarchy. Must be a 1-character string.
   * @param paramSeparator The symbol used to separate parameters in a
   *          {@link PlaceRequest}. Must be a 1-character string.
   * @param valueSeparator The symbol used to separate the parameter name from
   *          its value. Must be a 1-character string.
   */
  public ParameterTokenFormatter(String hierarchySeparator,
      String paramSeparator, String valueSeparator) {

    assert hierarchySeparator.length() == 1;
    assert paramSeparator.length() == 1;
    assert valueSeparator.length() == 1;
    assert !hierarchySeparator.equals(paramSeparator);
    assert !hierarchySeparator.equals(valueSeparator);
    assert !paramSeparator.equals(valueSeparator);
    this.hierarchySeparator = hierarchySeparator;
    this.paramSeparator = paramSeparator;
    this.valueSeparator = valueSeparator;

    hierarchyPattern = hierarchySeparator + "(?!" + hierarchySeparator + ")";
    hierarchyEscape = hierarchySeparator + hierarchySeparator;

    paramPattern = paramSeparator + "(?!" + paramSeparator + ")";
    paramEscape = paramSeparator + paramSeparator;

    valuePattern = valueSeparator + "(?!" + valueSeparator + ")";
    valueEscape = valueSeparator + valueSeparator;
  }

  @Override
  public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy)
      throws TokenFormatException {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < placeRequestHierarchy.size(); ++i) {
      if (i != 0) {
        out.append(hierarchySeparator);
      }
      out.append(toPlaceToken(placeRequestHierarchy.get(i)));
    }
    return out.toString();
  }

  @Override
  public PlaceRequest toPlaceRequest(String placeToken)
      throws TokenFormatException {
    PlaceRequest req = null;

    int split = placeToken.indexOf(paramSeparator);
    if (split == 0) {
      throw new TokenFormatException("Place history token is missing.");
    } else if (split == -1) {
      req = new PlaceRequest(placeToken);
    } else if (split >= 0) {
      req = new PlaceRequest(placeToken.substring(0, split));
      String paramsChunk = placeToken.substring(split + 1);
      String[] paramTokens = paramsChunk.split(paramPattern);
      String completeParamToken = "";
      for (String paramToken : paramTokens) {
        completeParamToken = completeParamToken + paramToken;

        // Will end with a separator if we had an escaped separator
        if (completeParamToken.endsWith(paramSeparator)) {
          continue;
        }

        String[] param = completeParamToken.split(valuePattern);
        completeParamToken = "";
        String key = "";
        int i = 0;
        for (; i < param.length; ++i) {
          key = key + param[i];

          // Will end with a separator if we had an escaped separator
          if (!key.endsWith(valueSeparator)) {
            break;
          }
        }
        ++i;
        String value = "";
        for (; i < param.length; ++i) {
          value = value + param[i];

          // Will end with a separator if we had an escaped separator
          if (!value.endsWith(valueSeparator)) {
            break;
          }
        }
        ++i;

        if (i != param.length) {
          throw new TokenFormatException(
              "Bad parameter: Parameters require a single '" + valueSeparator
                  + "' between the key and value.");
        }
        req = req.with(key, value);
      }
    }

    return req;
  }

  @Override
  public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken)
      throws TokenFormatException {

    List<String> placeTokens = toPlaceTokenList(historyToken);

    List<PlaceRequest> result = new ArrayList<PlaceRequest>();

    for (String placeToken : placeTokens) {
      result.add(toPlaceRequest(placeToken));
    }
      
    return result;
  }

  @Override
  public String toPlaceToken(PlaceRequest placeRequest) {
    StringBuilder out = new StringBuilder();
    out.append(placeRequest.getNameToken());

    Set<String> params = placeRequest.getParameterNames();
    if (params != null && params.size() > 0) {
      for (String name : params) {
        out.append(paramSeparator);
        out.append(escape(name)).append(valueSeparator).append(
            escape(placeRequest.getParameter(name, null)));
      }
    }
    return out.toString();
  }

  private String escape(String value) {
    return value.replaceAll(hierarchySeparator, hierarchyEscape).replaceAll(
        paramSeparator, paramEscape).replaceAll(valueSeparator, valueEscape);
  }

  private List<String> toPlaceTokenList(String historyToken)
      throws TokenFormatException {
    String[] placeTokens = historyToken.split(hierarchyPattern);
    List<String> result = new ArrayList<String>();
    String completePlaceToken = "";
    for (String placeToken : placeTokens) {
      completePlaceToken = completePlaceToken + placeToken;

      // Will end with a separator if we had an escaped separator
      if (completePlaceToken.endsWith(hierarchySeparator)) {
        continue;
      }

      result.add(completePlaceToken);
      completePlaceToken = "";
    }
    return result;
  }
}
