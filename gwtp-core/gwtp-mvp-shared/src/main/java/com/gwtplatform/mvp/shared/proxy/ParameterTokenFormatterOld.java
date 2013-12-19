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

package com.gwtplatform.mvp.shared.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

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
 * is encountered in a parameter or a value it is escaped by being query-string
 * encoded (see {@link URL#encodeQueryString(String)}). As a consequence, a custom
 * {@code ParameterTokenFormatterOld} must use separators that are modified when
 * passed to {@link URL#encodeQueryString(String)}.
 * <p/>
 * For example, {@link ParameterTokenFormatterOld} would parse:
 * <p/>
 * <pre>
 * nameToken1;param1.1=value1.1;param1.2=value1.2/nameToken2/nameToken3;param3.1=value%2f3%3d1
 * </pre>
 * Into the following hierarchy of {@link PlaceRequest}:
 * <p/>
 * <pre>
 * {
 *   { "nameToken1", { {"param1.1", "value1.1"}, {"parame1.2","value1.2"} },
 *     "nameToken2", {},
 *     "nameToken3", { {"param3.1", "value/3=1"} } }
 * }
 * </pre>
 * If you want to use different symbols as separator, use the
 * {@link #ParameterTokenFormatterOld(String, String, String)} constructor.
 *
 * @author Philippe Beaudoin
 * @author Yannis Gonianakis
 * @deprecated Use {@link ParameterTokenFormatter} instead.
 */
public final class ParameterTokenFormatterOld implements TokenFormatter {

    protected static final String DEFAULT_HIERARCHY_SEPARATOR = "/";
    protected static final String DEFAULT_PARAM_SEPARATOR = ";";
    protected static final String DEFAULT_VALUE_SEPARATOR = "=";

    private final String hierarchySeparator;
    private final String paramSeparator;
    private final String valueSeparator;
    private final UrlUtils urlUtils;

    /**
     * Builds a {@link ParameterTokenFormatterOld} using the default separators.
     */
    @Inject
    public ParameterTokenFormatterOld(UrlUtils urlUtils) {
        this(DEFAULT_HIERARCHY_SEPARATOR, DEFAULT_PARAM_SEPARATOR,
                DEFAULT_VALUE_SEPARATOR, urlUtils);
    }

    /**
     * This constructor makes it possible to use custom separators in your token
     * formatter. The separators must be 1-letter strings, they must all be
     * different from one another, and they must be encoded when ran through
     * {@link URL#encodeQueryString(String)})
     *
     * @param hierarchySeparator The symbol used to separate {@link PlaceRequest}
     *                           in a hierarchy. Must be a 1-character string and can't be {@code %}.
     * @param paramSeparator     The symbol used to separate parameters in a
     *                           {@link PlaceRequest}. Must be a 1-character string and can't be {@code %}.
     * @param valueSeparator     The symbol used to separate the parameter name from
     *                           its value. Must be a 1-character string and can't be {@code %}.
     */
    public ParameterTokenFormatterOld(String hierarchySeparator,
            String paramSeparator, String valueSeparator, UrlUtils urlUtils) {

        assert hierarchySeparator.length() == 1;
        assert paramSeparator.length() == 1;
        assert valueSeparator.length() == 1;
        assert !hierarchySeparator.equals(paramSeparator);
        assert !hierarchySeparator.equals(valueSeparator);
        assert !paramSeparator.equals(valueSeparator);
        assert !valueSeparator.equals(urlUtils.encodeQueryString(valueSeparator));
        assert !hierarchySeparator.equals(urlUtils.encodeQueryString(hierarchySeparator));
        assert !paramSeparator.equals(urlUtils.encodeQueryString(paramSeparator));
        assert !hierarchySeparator.equals("%");
        assert !paramSeparator.equals("%");
        assert !valueSeparator.equals("%");

        this.hierarchySeparator = hierarchySeparator;
        this.paramSeparator = paramSeparator;
        this.valueSeparator = valueSeparator;
        this.urlUtils = urlUtils;
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
        int split = placeToken.indexOf(paramSeparator);
        if (split == 0) {
            throw new TokenFormatException("Place history token is missing.");
        } else if (split == -1) {
            return new PlaceRequest.Builder().nameToken(placeToken).build();
        } else if (split >= 0) {
            PlaceRequest.Builder reqBuilder = new PlaceRequest.Builder().nameToken(placeToken.substring(0, split));
            String paramsChunk = placeToken.substring(split + 1);
            String[] paramTokens = paramsChunk.split(paramSeparator);
            for (String paramToken : paramTokens) {
                if (paramToken.isEmpty()) {
                    throw new TokenFormatException(
                            "Bad parameter: Successive parameters require a single '"
                                    + paramSeparator + "' between them.");
                }
                String[] param = splitParamToken(paramToken);
                String key = unescape(param[0]);
                String value = unescape(param[1]);
                reqBuilder = reqBuilder.with(key, value);
            }
            return reqBuilder.build();
        }
        return null;
    }

    @Override
    public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken)
            throws TokenFormatException {
        int split = historyToken.indexOf(hierarchySeparator);
        if (split == 0) {
            throw new TokenFormatException("Place history token is missing.");
        } else {
            List<PlaceRequest> result = new ArrayList<PlaceRequest>();
            if (split == -1) {
                result.add(toPlaceRequest(historyToken)); // History token consists of a single place token
            } else {
                String[] placeTokens = historyToken.split(hierarchySeparator);
                for (String placeToken : placeTokens) {
                    if (placeToken.isEmpty()) {
                        throw new TokenFormatException(
                                "Bad parameter: Successive place tokens require a single '"
                                        + hierarchySeparator + "' between them.");
                    }
                    result.add(toPlaceRequest(placeToken));
                }
            }
            return result;
        }
    }

    @Override
    public String toPlaceToken(PlaceRequest placeRequest) {
        StringBuilder out = new StringBuilder();
        out.append(placeRequest.getNameToken());

        Set<String> params = placeRequest.getParameterNames();
        if (params != null) {
            for (String name : params) {
                out.append(paramSeparator)
                        .append(escape(name)).append(valueSeparator)
                        .append(escape(placeRequest.getParameter(name, null)));
            }
        }
        return out.toString();
    }

    private String escape(String value) {
        return urlUtils.encodeQueryString(value);
    }

    private String unescape(String value) {
        return urlUtils.decodeQueryString(value);
    }

    private String[] splitParamToken(String paramToken) {
        String[] param = paramToken.split(valueSeparator, 2);
        if (param.length == 1                       // pattern didn't match
                || param[0].contains(valueSeparator)    // un-escaped separator encountered in the key
                || param[1].contains(valueSeparator)) { // un-escaped separator encountered in the value
            throw new TokenFormatException(
                    "Bad parameter: Parameters require a single '" + valueSeparator
                            + "' between the key and value.");
        }
        return param;
    }
}
