/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.common.rebind;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.Shardable;

import static com.google.gwt.core.ext.TreeLogger.Type.DEBUG;

@Shardable
@LinkerOrder(Order.POST)
public class VersionInspectorLinker extends Linker {
    private static final int CHECK_TIMEOUT_MS = 5000;

    private static final String MVP_EXIST_CLASS = "com.gwtplatform.mvp.client.gin.DefaultModule";
    private static final String MVP_GROUP_ID = "com.gwtplatform";
    private static final String MVP_ARTIFACT = "gwtp-mvp-client";
    private static final Dependency MVP = new DependencyImpl(MVP_EXIST_CLASS, MVP_GROUP_ID, MVP_ARTIFACT);

    private static final String RPC_EXIST_CLASS = "com.gwtplatform.dispatch.rpc.client.gin.RpcDispatchAsyncModule";
    private static final String RPC_GROUP_ID = "com.gwtplatform";
    private static final String RPC_ARTIFACT = "gwtp-dispatch-rpc-client";
    private static final Dependency DISPATCH_RPC = new DependencyImpl(RPC_EXIST_CLASS, RPC_GROUP_ID, RPC_ARTIFACT);

    private static final String REST_EXIST_CLASS = "com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModule";
    private static final String REST_GROUP_ID = "com.gwtplatform";
    private static final String REST_ARTIFACT = "gwtp-dispatch-rest";
    private static final Dependency DISPATCH_REST = new DependencyImpl(REST_EXIST_CLASS, REST_GROUP_ID, REST_ARTIFACT);

    private static final String API_SEARCH = "http://arcbees-stats-service.appspot.com/version";
    private static final String GROUP_QUERY_PARAMETER = "groupid";
    private static final String ARTIFACT_QUERY_PARAMETER = "artifactid";
    private static final String VERSION_QUERY_PARAMETER = "version";

    private static final String PROPERTY_VERIFY_NEWER_VERSION = "verifyNewerVersion";

    private static final Pattern VERSION_PATTERN = Pattern.compile("\"version\":\\s*\"([0-9](?:\\.[0-9])*)\"");
    private static final Pattern LATEST_PATTERN = Pattern.compile("\"latest\":([a-z]{1,4})");
    private static final Pattern RESPONSE_CONTENT_PATTERN = Pattern.compile("^[^{]*(\\{.*\\})$");

    private static final String HR = "------------------------------------------------------------";
    private static final String NEW_VERSION_AVAILABLE = "A new version of %s is available!";
    private static final String SEE_ARTIFACT_DETAILS = "See http://search.maven.org/#artifactdetails|%s|%s|%s|jar";
    private static final String YOUR_VERSION = "Your version: %s";
    private static final String LATEST_VERSION = "Latest version: %s";

    private Logger logger;

    public VersionInspectorLinker() {
    }

    @Override
    public String getDescription() {
        return "Verify the availability of a more recent version of GWTP.";
    }

    @Override
    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation)
            throws UnableToCompleteException {
        if (!onePermutation && !isSuperDevMode(context) && !isTest(context) && canVerifyNewerVersion(context)) {
            checkLatestVersions(logger);
        }

        return artifacts;
    }

    private boolean isSuperDevMode(LinkerContext context) {
        boolean isSdm = false;

        for (SelectionProperty property : context.getProperties()) {
            if ("superdevmode".equals(property.getName())) {
                isSdm = "on".equals(property.tryGetValue());
                break;
            }
        }

        return isSdm;
    }

    private boolean isTest(LinkerContext context) {
        boolean isTest = false;

        for (ConfigurationProperty property : context.getConfigurationProperties()) {
            String name = property.getName();
            List<String> values = property.getValues();

            if ("junit.moduleName".equals(name) && !values.isEmpty() && !values.get(0).isEmpty()) {
                isTest = true;
                break;
            }
        }

        return isTest;
    }

    private boolean canVerifyNewerVersion(LinkerContext context) {
        boolean verifyNewerVersion = true;

        for (ConfigurationProperty property : context.getConfigurationProperties()) {
            if (PROPERTY_VERIFY_NEWER_VERSION.equals(property.getName())) {
                List<String> values = property.getValues();
                verifyNewerVersion = !(!values.isEmpty() && "false".equals(values.get(0)));

                break;
            }
        }

        return verifyNewerVersion;
    }

    private void checkLatestVersions(TreeLogger logger) {
        checkLatestVersionIfPresent(logger, MVP);
        checkLatestVersionIfPresent(logger, DISPATCH_RPC);
        checkLatestVersionIfPresent(logger, DISPATCH_REST);
    }

    private void checkLatestVersionIfPresent(TreeLogger baseLogger, Dependency dependency) {
        if (dependency.isPresent()) {
            String artifactId = dependency.getArtifactId();
            logger = new Logger(baseLogger.branch(DEBUG, "Checking version information for " + artifactId));
            logger.debug("You can disable this check by adding this line to your GWT module:");
            logger.debug("<set-configuration-property name=\"verifyNewerVersion\" value=\"false\"/>");

            checkLatestVersion(dependency);
        }
    }

    private void checkLatestVersion(Dependency dependency) {
        try {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String currentVersion = dependency.getVersion();

            String json = fetchArtifactVersion(groupId, artifactId, currentVersion);
            String latestVersion = extractVersion(json);
            Boolean isLatest = extractIsLatest(json);

            if (isLatest) {
                logger.info("You are using the latest version of " + artifactId + "!");
            } else {
                warnVersion(groupId, artifactId, latestVersion, currentVersion);
            }
        } catch (Exception e) {
            logger.getTreeLogger().log(DEBUG, "Exception caught", e);
        }
    }

    /**
     * Calls the Arcbees Stats service to retrieve data about a GWTP artifact.
     * Note: We would use URL.openConnection, but because this code may be used in a GAE environment with hosted mode,
     * this will cause fallback to the URLFetch Service. Using a socket bypasses this.
     *
     * @return The resulting plain text response
     */
    private String fetchArtifactVersion(String groupId, String artifactId, String version) throws IOException {
        URL maven = new URL(buildUrl(groupId, artifactId, version));

        Socket socket = new Socket(maven.getHost(), 80);
        socket.setSoTimeout(CHECK_TIMEOUT_MS);

        String response;

        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8")));
            output.println("GET " + maven.getFile() + " HTTP/1.1");
            output.println("Host: " + maven.getHost());
            output.println("Connection: close");
            output.println();
            output.flush();

            response = readText(socket.getInputStream());
        } finally {
            socket.close();
        }

        return response;
    }

    private String readText(InputStream stream) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            response.append(inputLine);
        }

        return extractResponseContent(response.toString());
    }

    private String extractResponseContent(String response) {
        Matcher matcher = RESPONSE_CONTENT_PATTERN.matcher(response);

        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractVersion(String versionResponse) {
        Matcher matcher = VERSION_PATTERN.matcher(versionResponse);

        return matcher.find() ? matcher.group(1) : "";
    }

    private Boolean extractIsLatest(String versionResponse) {
        Matcher matcher = LATEST_PATTERN.matcher(versionResponse);

        return matcher.find() && Boolean.valueOf(matcher.group(1));
    }

    private void warnVersion(String groupId, String artifactId, String latestVersion, String currentVersion) {
        logger.warn(HR);

        logger.warn(NEW_VERSION_AVAILABLE, artifactId);
        logger.warn(YOUR_VERSION, currentVersion);
        logger.warn(LATEST_VERSION, latestVersion);
        logger.warn(SEE_ARTIFACT_DETAILS, groupId, artifactId, latestVersion);

        logger.warn(HR);
    }

    private String buildUrl(String groupId, String artifactId, String version) {
        StringBuilder queryString = new StringBuilder();

        appendParameter(queryString, GROUP_QUERY_PARAMETER, groupId);
        appendParameter(queryString, ARTIFACT_QUERY_PARAMETER, artifactId);
        appendParameter(queryString, VERSION_QUERY_PARAMETER, version);

        queryString.insert(0, API_SEARCH);

        return queryString.toString();
    }

    private void appendParameter(StringBuilder stringBuilder, String key, String value) {
        if (stringBuilder.length() == 0) {
            stringBuilder.append("?").append(key).append("=").append(value);
        } else {
            stringBuilder.append("&").append(key).append("=").append(value);
        }
    }
}
