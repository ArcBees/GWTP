/*
 * Copyright 2013 ArcBees Inc.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.gwtplatform.common.shared.UrlUtils;

import jakarta.inject.Inject;

/**
 * Implementation of {@link TokenFormatter} with support for route like place names.
 * <p>
 * Instead of wiring a hierarchy of several places bound to multiple presenters this implements a flat structure where
 * every history token is bound to a single presenter.
 * </p>
 * <p>
 * Usage:
 * </p>
 * <p>
 * Replace the default binding to {@link ParameterTokenFormatter} with {@link RouteTokenFormatter}. In case you use
 * GWTPs {@code com.gwtplatform.mvp.client.gin.DefaultModule}:
 * </p>
 * <pre>
 * install(new DefaultModule(DefaultPlaceManager.class, RouteTokenFormatter.class));
 * </pre>
 * <p>
 * Now all @NameToken's are treated as routes. Routes are expected to start with an '/' and can contain path parameters
 * as well as query parameters.
 * </p>
 * <pre>
 * {@code @NameToken("/user/{userId}/privacy") // Token for PrivacyPresenter}
 * {@code @NameToken("/user/{userId}/privacy/profile") // Token for PrivacyProfilePresenter}
 * {@code @NameToken("/user/{userId}/privacy/photos") // Token for PrivacyPhotosPresenter}
 * </pre>
 * <p>
 * Static-parts of an route tie stronger than parameter-parts. This way following works:
 * </p>
 * <pre>
 * {@code @NameToken("/{vanityId}") // Token for VanityUrlPresenter}
 * {@code @NameToken("/privacy") // Token for PrivacyPresenter}
 * </pre>
 * <p>
 * Note: For the moment this is implemented on top of the hierarchical-place API to not an big structural changes prior
 * 1.0 release.
 * </p>
 */
public class RouteTokenFormatter implements TokenFormatter {
    /**
     * Helper class to store matches to routes in {@link #toPlaceRequest(String)}.
     */
    private static class RouteMatch implements Comparable<RouteMatch> {
        /**
         * Route/place-token associated to the match.
         */
        final String route;

        /**
         * Number of static matches in this route.
         */
        final int staticMatches;

        /**
         * Parsed parameters of this route.
         */
        Map<String, String> parameters;

        /**
         * Construct a new {@link RouteMatch}.
         *
         * @param route         Place token associated to the match.
         * @param staticMatches Number of static matches in this route.
         * @param parameters    Parsed parameters of this route.
         */
        RouteMatch(String route,
                   int staticMatches,
                   Map<String, String> parameters) {
            this.route = route;
            this.staticMatches = staticMatches;
            this.parameters = parameters;
        }

        /**
         * Sort {@link RouteMatch}s by the amount of {@link #staticMatches}.
         */
        @Override
        public int compareTo(RouteMatch other) {
            return Integer.valueOf(staticMatches).compareTo(other.staticMatches);
        }
    }

    /**
     * Helper class for parsing and matching place-tokens against routes.
     */
    private class RouteMatcher {
        /**
         * All matching routes of the place-token.
         * <p>
         * Sorted in ascending order by the number of static matches.
         */
        final TreeSet<RouteMatch> allMatches;

        final String[] placeParts;

        /**
         * Parse and match the given place-token.
         *
         * @param placeToken The place-token.
         */
        RouteMatcher(String placeToken) {
            assert placeTokenIsValid(placeToken) : "Place-token should start with a '/' or '!/'";
            assert placeToken.indexOf('?') == -1 : "No Query string expected here";

            this.allMatches = new TreeSet<>();
            this.placeParts = placeToken.split("/");

            for (String route : allRegisteredPlaceTokens.getAllPlaceTokens()) {
                RouteMatch match = matchRoute(route);
                if (match != null) {
                    allMatches.add(match);
                }
            }
        }

        /**
         * Check if the current place-token matches the given route and return the associated {@link RouteMatch}.
         *
         * @param route The route to check.
         * @return Associated {@link RouteMatch} or <code>null</code> if the place-token doesn't match the given route.
         */
        RouteMatch matchRoute(String route) {
            String[] routeParts = route.split("/");

            if (placeParts.length != routeParts.length) {
                return null;
            }

            if (placeParts.length == 0) {
                assert routeIsEmpty(route);
                return new RouteMatch(route, 0, null);
            }

            Map<String, String> recordedParameters = new HashMap<>();
            int staticMatches = 0;
            for (int i = 0; i < placeParts.length; i++) {
                if (placeParts[i].equals(routeParts[i])) {
                    staticMatches++;
                } else if (routeParts[i].matches("\\{.*\\}")) {
                    String parameterName = routeParts[i].substring(1, routeParts[i].length() - 1);
                    recordedParameters.put(parameterName, placeParts[i]);
                } else {
                    return null;
                }
            }

            return new RouteMatch(route, staticMatches, recordedParameters);
        }

        private boolean routeIsEmpty(String route) {
            return "/".equals(route) || "!/".equals(route);
        }
    }

    private final UrlUtils urlUtils;
    private final PlaceTokenRegistry allRegisteredPlaceTokens;

    @Inject
    RouteTokenFormatter(UrlUtils urlUtils,
                        PlaceTokenRegistry tokenRegistry) {
        this.urlUtils = urlUtils;
        this.allRegisteredPlaceTokens = tokenRegistry;
    }

    @Override
    public String toPlaceToken(PlaceRequest placeRequest) throws TokenFormatException {
        String placeToken = placeRequest.getNameToken();
        StringBuilder queryStringBuilder = new StringBuilder();
        String querySeparator = "";

        for (String parameterName : placeRequest.getParameterNames()) {
            String parameterValue = placeRequest.getParameter(parameterName, null);
            if (parameterValue != null) {
                String encodedParameterValue = urlUtils.encodeQueryString(parameterValue);

                if (placeToken.contains("/{" + parameterName + "}")) {
                    // route parameter
                    placeToken = placeToken.replace("{" + parameterName + "}", encodedParameterValue);
                } else {
                    // query parameter
                    queryStringBuilder.append(querySeparator).append(parameterName).append("=")
                        .append(encodedParameterValue);
                    querySeparator = "&";
                }
            }
        }

        String queryString = queryStringBuilder.toString();
        if (!queryString.isEmpty()) {
            placeToken = placeToken + "?" + queryString;
        }

        return placeToken;
    }

    @Override
    public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy) throws TokenFormatException {
        assert !placeRequestHierarchy.isEmpty() : "Expected a place hierarchy with one or more places.";

        return toPlaceToken(placeRequestHierarchy.get(placeRequestHierarchy.size() - 1));
    }

    @Override
    public PlaceRequest toPlaceRequest(String placeToken) throws TokenFormatException {
        /*
         * To support the native GWT history as well as HTML pushstate a slash is added when needed.
         */
        if (!placeTokenIsValid(placeToken)) {
            return toPlaceRequest("/" + placeToken);
        }

        int split = placeToken.indexOf('?');
        String place = (split != -1) ? placeToken.substring(0, split) : placeToken;
        String query = (split != -1) ? placeToken.substring(split + 1) : "";

        RouteMatcher matcher = new RouteMatcher(place);
        RouteMatch match = (!matcher.allMatches.isEmpty()) ? matcher.allMatches.last() : new RouteMatch(place, 0, null);

        match.parameters = decodeEmbeddedParams(match.parameters);
        match.parameters = parseQueryString(query, match.parameters);

        return new PlaceRequest.Builder().nameToken(match.route).with(match.parameters).build();
    }

    private Map<String, String> decodeEmbeddedParams(Map<String, String> parameters) {
        if (parameters != null) {
            for (Entry<String, String> entry : parameters.entrySet()) {
                entry.setValue(urlUtils.decodeQueryString(entry.getValue()));
            }
        }
        return parameters;
    }

    @Override
    public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken) throws TokenFormatException {
        List<PlaceRequest> result = new ArrayList<>();
        result.add(toPlaceRequest(historyToken));

        return result;
    }

    /**
     * Parse the given query-string and store all parameters into a map.
     *
     * @param queryString The query-string.
     * @param into        The map to use. If the given map is <code>null</code> a new map will be created.
     * @return A map containing all keys value pairs of the query-string.
     */
    Map<String, String> parseQueryString(String queryString, Map<String, String> into) {
        Map<String, String> result = (into != null) ? into : new HashMap<>();

        if (queryString != null && !queryString.isEmpty()) {
            for (String keyValuePair : queryString.split("&")) {
                String[] keyValue = keyValuePair.split("=", 2);
                if (keyValue.length > 1) {
                    result.put(keyValue[0], urlUtils.decodeQueryString(keyValue[1]));
                } else {
                    result.put(keyValue[0], "");
                }
            }
        }

        return result;
    }

    private boolean placeTokenIsValid(String placeToken) {
        return placeToken.startsWith("/") || placeToken.startsWith("!/");
    }
}
