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

package com.gwtplatform.mvp.shared.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gwtplatform.common.shared.UrlUtils;

import jakarta.inject.Inject;

/**
 * Formats tokens from {@code String} values to {@link PlaceRequest} and {@link PlaceRequest}
 * hierarchies and vice-versa. The default implementation
 * uses:
 * <ul>
 * <li>{@code '/'} to separate {@link PlaceRequest}s in a hierarchy;</li>
 * <li>{@code ';'} to separate parameters in a {@link PlaceRequest};</li>
 * <li>{@code '='} to separate the parameter name from its value;</li>
 * <li>{@code '\'} to escape separators inside parameters names and values in a
 * {@link PlaceRequest}.</li>
 * </ul>
 * These symbols cannot be used in a name token. If one of the separating symbol is encountered in a
 * parameter or a value it is escaped using the {@code '\'} character by replacing {@code '/'} with
 * {@code '\0'}, {@code ';'} with {@code '\1'}, {@code '='} with {@code '\2'} and {@code '\'} with
 * {@code '\3'}.
 * <p>
 * Before decoding a {@link String} URL fragment into a {@link PlaceRequest} or a
 * {@link PlaceRequest} hierarchy, {@link ParameterTokenFormatter} will first pass the
 * {@link String} through {@link UrlUtils#decodeQueryString(String)} so that if the URL was URL-encoded
 * by some user agent, like a mail user agent, it is still parsed correctly.
 * <p>
 * For example, {@link ParameterTokenFormatter} would parse any of the following:
 * <p>
 * <pre>
 * nameToken1%3Bparam1.1%3Dvalue1.1%3Bparam1.2%3Dvalue1.2%2FnameToken2%2FnameToken3%3Bparam3.1%3Dvalue%03%11
 * nameToken1;param1.1=value1.1;param1.2=value1.2/nameToken2/nameToken3;param3.1=value\03\21
 * </pre>
 * <p>
 * Into the following hierarchy of {@link PlaceRequest}:
 * <p>
 * <pre>
 * {
 *   { "nameToken1", { {"param1.1", "value1.1"}, {"parame1.2","value1.2"} },
 *     "nameToken2", {},
 *     "nameToken3", { {"param3.1", "value/3=1"} } }
 * }
 * </pre>
 * <p>
 * If you want to use different symbols as separator, use the
 * {@link #ParameterTokenFormatter(UrlUtils, String, String, String)} constructor.
 */
public class ParameterTokenFormatter implements TokenFormatter {

    protected static final String DEFAULT_HIERARCHY_SEPARATOR = "/";
    protected static final String DEFAULT_PARAM_SEPARATOR = ";";
    protected static final String DEFAULT_VALUE_SEPARATOR = "=";

    // Escaped versions of the above.
    protected static final char ESCAPE_CHARACTER = '\\';
    protected static final String ESCAPED_HIERARCHY_SEPARATOR = "\\0";
    protected static final String ESCAPED_PARAM_SEPARATOR = "\\1";
    protected static final String ESCAPED_VALUE_SEPARATOR = "\\2";
    protected static final String ESCAPED_ESCAPE_CHAR = "\\3";

    private final UrlUtils urlUtils;
    private final String hierarchySeparator;
    private final String paramSeparator;
    private final String valueSeparator;

    /**
     * Builds a {@link ParameterTokenFormatter} using the default separators and escape character.
     */
    @Inject
    public ParameterTokenFormatter(UrlUtils urlUtils) {
        this(urlUtils, DEFAULT_HIERARCHY_SEPARATOR, DEFAULT_PARAM_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    /**
     * This constructor makes it possible to use custom separators in your token formatter. The
     * separators must be 1-letter strings, they must all be different from one another, and they
     * must be encoded when ran through {@link UrlUtils#encodeQueryString(String)}).
     *
     * @param hierarchySeparator The symbol used to separate {@link PlaceRequest} in a hierarchy.
     *                           Must be a 1-character string and can't be {@code %}.
     * @param paramSeparator     The symbol used to separate parameters in a {@link PlaceRequest}. Must
     *                           be a 1-character string and can't be {@code %}.
     * @param valueSeparator     The symbol used to separate the parameter name from its value. Must be
     *                           a 1-character string and can't be {@code %}.
     */
    public ParameterTokenFormatter(UrlUtils urlUtils,
                                   String hierarchySeparator,
                                   String paramSeparator,
                                   String valueSeparator) {
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

        this.urlUtils = urlUtils;
        this.hierarchySeparator = hierarchySeparator;
        this.paramSeparator = paramSeparator;
        this.valueSeparator = valueSeparator;
    }

    @Override
    public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy)
            throws TokenFormatException {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < placeRequestHierarchy.size(); ++i) {
            if (i != 0) {
                out.append(hierarchySeparator);
            }
            out.append(placeTokenToUnescapedString(placeRequestHierarchy.get(i)));
        }

        return out.toString();
    }

    @Override
    public PlaceRequest toPlaceRequest(String placeToken) throws TokenFormatException {
        return unescapedStringToPlaceRequest(urlUtils.decodeQueryString(placeToken));
    }

    /**
     * Converts an unescaped string to a place request. To unescape the hash fragment you must run it
     * through {@link UrlUtils#decodeQueryString(String)}.
     *
     * @param unescapedPlaceToken The unescaped string to convert to a place request.
     * @return The place request.
     * @throws TokenFormatException if there is an error converting.
     */
    private PlaceRequest unescapedStringToPlaceRequest(String unescapedPlaceToken)
            throws TokenFormatException {
        int split = unescapedPlaceToken.indexOf(paramSeparator);
        if (split == 0) {
            throw new TokenFormatException("Place history token is missing.");
        } else if (split == -1) {
            // No parameters.
            return new PlaceRequest.Builder().nameToken(customUnescape(unescapedPlaceToken)).build();
        } else if (split >= 0) {
            PlaceRequest.Builder reqBuilder = new PlaceRequest.Builder().nameToken(customUnescape(unescapedPlaceToken
                    .substring(0, split)));
            String paramsChunk = unescapedPlaceToken.substring(split + 1);
            String[] paramTokens = paramsChunk.split(paramSeparator);
            for (String paramToken : paramTokens) {
                if (paramToken.isEmpty()) {
                    throw new TokenFormatException("Bad parameter: Successive parameters require a single '" +
                            paramSeparator + "' between them.");
                }
                String[] param = paramToken.split(valueSeparator);
                if (param.length == 1) {
                    // If there is only one parameter, then we need an '=' at the last position.
                    if (paramToken.charAt(paramToken.length() - 1) != valueSeparator.charAt(0)) {
                        throw new TokenFormatException("Bad parameter: Need exactly one key and one value.");
                    }
                } else if (param.length == 2) {
                    // If there are two parameters, then there must not be a '=' at the last position.
                    if (paramToken.charAt(paramToken.length() - 1) == valueSeparator.charAt(0)) {
                        throw new TokenFormatException("Bad parameter: Need exactly one key and one value.");
                    }
                } else {
                    throw new TokenFormatException("Bad parameter: Need exactly one key and one value.");
                }
                String key = customUnescape(param[0]);
                String value = param.length == 2 ? customUnescape(param[1]) : "";
                reqBuilder = reqBuilder.with(key, value);
            }
            return reqBuilder.build();
        }
        return null;
    }

    @Override
    public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken) throws TokenFormatException {
        String unescapedHistoryToken = urlUtils.decodeQueryString(historyToken);

        int split = unescapedHistoryToken.indexOf(hierarchySeparator);
        List<PlaceRequest> result = new ArrayList<PlaceRequest>();
        if (split == -1) {
            // History token consists of a single place token.
            result.add(unescapedStringToPlaceRequest(unescapedHistoryToken));
        } else {
            String[] unescapedPlaceTokens = unescapedHistoryToken.split(hierarchySeparator);
            if (unescapedPlaceTokens.length == 0) {
                throw new TokenFormatException("Bad parameter: nothing in the history token.");
            }
            for (String unescapedPlaceToken : unescapedPlaceTokens) {
                if (unescapedPlaceToken.isEmpty()) {
                    throw new TokenFormatException("Bad parameter: Successive place tokens require a single '"
                            + hierarchySeparator + "' between them.");
                }
                result.add(unescapedStringToPlaceRequest(unescapedPlaceToken));
            }
        }
        return result;
    }

    @Override
    public String toPlaceToken(PlaceRequest placeRequest) throws TokenFormatException {
        return placeTokenToUnescapedString(placeRequest);
    }

    /**
     * Converts a place token to an unescaped string. If the name token or the parameters contain any
     * of the separator symbols, they will be escaped with our custom escaping mechanism.
     *
     * @param placeRequest The place request to convert.
     * @return The unescaped string for the place token corresponding to that place request.
     * @throws TokenFormatException if there is an error converting.
     */
    private String placeTokenToUnescapedString(PlaceRequest placeRequest)
            throws TokenFormatException {
        StringBuilder out = new StringBuilder();
        out.append(customEscape(placeRequest.getNameToken()));
        Set<String> params = placeRequest.getParameterNames();
        if (params != null) {
            for (String name : params) {
                out.append(paramSeparator).append(customEscape(name)).append(valueSeparator).append(
                        customEscape(placeRequest.getParameter(name, null)));
            }
        }

        return out.toString();
    }

    /**
     * Use our custom escaping mechanism to escape the provided string. This should be used on the
     * name token, and the parameter keys and values, before they are attached with the various
     * separators. The string will also be passed through {@link UrlUtils#encodeQueryString}.
     * Visible for testing.
     *
     * @param string The string to escape.
     * @return The escaped string.
     */
    String customEscape(String string) {
        StringBuilder builder = new StringBuilder();
        int len = string.length();

        char hierarchyChar = hierarchySeparator.charAt(0);
        char paramChar = paramSeparator.charAt(0);
        char valueChar = valueSeparator.charAt(0);

        for (int i = 0; i < len; i++) {
            char ch = string.charAt(i);
            if (ch == ESCAPE_CHARACTER) {
                builder.append(ESCAPED_ESCAPE_CHAR);
            } else if (ch == hierarchyChar) {
                builder.append(ESCAPED_HIERARCHY_SEPARATOR);
            } else if (ch == paramChar) {
                builder.append(ESCAPED_PARAM_SEPARATOR);
            } else if (ch == valueChar) {
                builder.append(ESCAPED_VALUE_SEPARATOR);
            } else {
                builder.append(ch);
            }
        }

        return urlUtils.encodeQueryString(builder.toString());
    }

    /**
     * Use our custom escaping mechanism to unescape the provided string. This should be used on the
     * name token, and the parameter keys and values, after they have been split using the various
     * separators. The input string is expected to already be sent through
     * {@link UrlUtils#decodeQueryString}.
     *
     * @param string The string to unescape, must have passed through {@link UrlUtils#decodeQueryString}.
     * @return The unescaped string.
     * @throws TokenFormatException if there is an error converting.
     */
    private String customUnescape(String string) throws TokenFormatException {
        StringBuilder builder = new StringBuilder();
        int len = string.length();

        char hierarchyNum = ESCAPED_HIERARCHY_SEPARATOR.charAt(1);
        char paramNum = ESCAPED_PARAM_SEPARATOR.charAt(1);
        char valueNum = ESCAPED_VALUE_SEPARATOR.charAt(1);
        char escapeNum = ESCAPED_ESCAPE_CHAR.charAt(1);

        int i = 0;
        while (i < len - 1) {
            char ch = string.charAt(i);
            if (ch == ESCAPE_CHARACTER) {
                i++;
                char ch2 = string.charAt(i);
                if (ch2 == hierarchyNum) {
                    builder.append(hierarchySeparator);
                } else if (ch2 == paramNum) {
                    builder.append(paramSeparator);
                } else if (ch2 == valueNum) {
                    builder.append(valueSeparator);
                } else if (ch2 == escapeNum) {
                    builder.append(ESCAPE_CHARACTER);
                }
            } else {
                builder.append(ch);
            }
            i++;
        }
        if (i == len - 1) {
            char ch = string.charAt(i);
            if (ch == ESCAPE_CHARACTER) {
                throw new TokenFormatException("Last character of string being unescaped cannot be '" +
                        ESCAPE_CHARACTER + "'.");
            }
            builder.append(ch);
        }
        return builder.toString();
    }
}
