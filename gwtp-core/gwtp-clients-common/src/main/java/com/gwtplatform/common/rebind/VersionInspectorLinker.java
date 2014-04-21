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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.http.client.Response;

import static com.google.gwt.core.ext.TreeLogger.Type.DEBUG;

@Shardable
@LinkerOrder(Order.POST)
public class VersionInspectorLinker extends Linker {
    private static final String GROUP_ID = "com.gwtplatform";
    private static final String ARTIFACT = "gwtp-mvp-client";
    private static final String API_SEARCH = "http://arcbees-stats-service.appspot.com/version%s";

    private static final String GROUP_QUERY_PARAMETER = "groupid";
    private static final String ARTIFACT_QUERY_PARAMETER = "artifactid";
    private static final String VERSION_QUERY_PARAMETER = "version";

    private static final String PROPERTY_VERIFY_NEWER_VERSION = "verifyNewerVersion";

    private static final String HR = "------------------------------------------------------------";
    private static final String NEW_VERSION_AVAILABLE = "A new version available of %s is available!";
    private static final String SEE_ARTIFACT_DETAILS = "See http://search.maven.org/#artifactdetails|%s|%s|%s|jar";
    private static final String YOUR_VERSION = "Your version: %s";
    private static final String LATEST_VERSION = "Latest version: %s";

    private Logger logger;
    private String currentVersionString;
    private StringBuffer queryString;

    public VersionInspectorLinker() {
    }

    @Override
    public String getDescription() {
        return "Verify the availability of a more recent version of GWTP.";
    }

    @Override
    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation)
            throws UnableToCompleteException {
        if (!onePermutation && canVerifyNewerVersion(context)) {
            this.logger = new Logger(logger);
            queryString = new StringBuffer();

            checkLatestVersion();
        }

        return artifacts;
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

    private void checkLatestVersion() {
        try {
            logger.debug("----- Checking version --------------");

            ArtifactVersion currentVersion = getCurrentVersion();
            String versionResponse = fetchArtifactVersion();
            String latestVersion = versionResponse.split("close")[1];
            ArtifactVersion latestArtifactVersion = extractLatestVersion(latestVersion);

            if (versionResponse.contains(String.valueOf(Response.SC_OK))) {
                logger.info("You are using the latest version!");
            } else {

                warnVersion(latestArtifactVersion, currentVersion);
            }

            logger.debug("----- Checking version: Success -----");
        } catch (Exception e) {
            logger.getTreeLogger().log(DEBUG, "Exception caught", e);
            logger.debug("----- Checking version: Failure -----");
        }
    }

    /**
     * Calls the Arcbees Stats service to retrieve data about a GWTP artifact.
     * Note: We would use URL.openConnection, but because this code may be used in a GAE environment with hosted mode,
     * this will cause fallback to the URLFetch Service. Using a socket bypasses this.
     *
     * @return The resulting plain text response
     */
    private String fetchArtifactVersion() throws IOException {
        buildQueryParameters();
        URL maven = new URL(String.format(API_SEARCH, queryString.toString()));

        Socket socket = new Socket(maven.getHost(), 80);
        String response;

        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
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
        String response = "";
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));

        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            response += inputLine;
        }

        return response;
    }

    private ArtifactVersion extractLatestVersion(String version) {
        return new DefaultArtifactVersion(version);
    }

    private ArtifactVersion getCurrentVersion() throws IOException {
        currentVersionString = getClass().getPackage().getImplementationVersion();

        return new DefaultArtifactVersion(currentVersionString);
    }

    private void warnVersion(ArtifactVersion latestVersion, ArtifactVersion currentVersion) {
        logger.warn(HR);

        logger.warn(NEW_VERSION_AVAILABLE, ARTIFACT);
        logger.warn(YOUR_VERSION, currentVersion);
        logger.warn(LATEST_VERSION, latestVersion);
        logger.warn(SEE_ARTIFACT_DETAILS, GROUP_ID, ARTIFACT, latestVersion.toString());

        logger.warn(HR);
    }

    private void buildQueryParameters() {
        appendParameter(GROUP_QUERY_PARAMETER, GROUP_ID);
        appendParameter(ARTIFACT_QUERY_PARAMETER, ARTIFACT);
        appendParameter(VERSION_QUERY_PARAMETER, currentVersionString);
    }

    private void appendParameter(String key, String value) {
        if (queryString.toString().isEmpty()) {
            queryString.append("?").append(key).append("=").append(value);
        } else {
            queryString.append("&").append(key).append("=").append(value);
        }
    }
}
