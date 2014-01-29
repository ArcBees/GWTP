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

package com.gwtplatform.mvp.rebind.velocity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.core.ext.TreeLogger;

import static com.google.gwt.core.ext.TreeLogger.Type.INFO;
import static com.google.gwt.core.ext.TreeLogger.Type.WARN;

public class VersionInspector {
    private static final String GROUP_ID = "com.gwtplatform";
    private static final String ARTIFACT = "gwtp-mvp-client";
    private static final String MAVEN_ARTIFACT_DETAILS =
            "http://search.maven.org/#artifactdetails%%7C%s%%7C%s%%7C%s%%7Cjar";
    private static final String API_SEARCH = "http://search.maven.org/solrsearch/select?wt=json&q=%s";
    private static final String API_QUERY = "g:\"%s\" AND a:\"%s\"";
    private static final Pattern LATEST_VERSION_PATTERN =
            Pattern.compile("\"latestVersion\":\\s*\"([0-9](?:\\.[0-9])*)\"");
    private static final Pattern RESPONSE_CONTENT_PATTERN =
            Pattern.compile("^[^{]*(\\{.*\\})$");

    private static boolean versionChecked = false;

    private final TreeLogger logger;

    private VersionInspector(TreeLogger logger) {
        this.logger = logger;
    }

    public static void checkVersion(TreeLogger logger) {
        if (!versionChecked) {
            versionChecked = true;
            new VersionInspector(logger).checkLatestVersion();
        }
    }

    public void checkLatestVersion() {
        try {
            logger.log(INFO, "----- Checking version --------------");

            String json = fetchArtifactJson();
            String latestVersion = extractLatestVersion(json);
            String currentVersion = getCurrentVersion();

            logger.log(INFO, "Your version is: " + currentVersion);
            logger.log(INFO, "Latest version is: " + latestVersion);

            // TODO: Do something more robust (ie: 1.2-SNAPSHOT > 1.1.1, so it should not warn the dev)
            if (!currentVersion.equals(latestVersion)) {
                logger.log(WARN, "A new version available of " + ARTIFACT + " is available!");
                logger.log(WARN, "See " + String.format(MAVEN_ARTIFACT_DETAILS, GROUP_ID, ARTIFACT, latestVersion));
            }

            logger.log(INFO, "----- Checking version: Success -----");
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(INFO, "----- Checking version: Failure -----");
        }
    }

    /**
     * Calls the maven API to retrieve data about a GWTP artifact.
     * Note: We would use URL.openConnection, but because this code may be used in a GAE environment, this will cause
     * fallback to the URLFetch Service. Using a socket bypasses this.
     *
     * @return The resulting JSON
     */
    private String fetchArtifactJson() throws IOException {
        String query = URLEncoder.encode(String.format(API_QUERY, GROUP_ID, ARTIFACT), "UTF-8");
        URL maven = new URL(String.format(API_SEARCH, query));

        Socket socket = new Socket(maven.getHost(), 80);
        String json = "";

        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            output.println("GET " + maven.getFile() + " HTTP/1.1");
            output.println("Host: " + maven.getHost());
            output.println("Connection: close");
            output.println();
            output.flush();

            json = readJson(socket.getInputStream());
        } finally {
            socket.close();
        }

        return json;
    }

    private String readJson(InputStream stream) throws IOException {
        String response = "";
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));

        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            response += inputLine;
        }

        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        Matcher matcher = RESPONSE_CONTENT_PATTERN.matcher(response);
        matcher.find();

        return matcher.group(1);
    }

    private String extractLatestVersion(String json) {
        Matcher matcher = LATEST_VERSION_PATTERN.matcher(json);
        matcher.find();

        return matcher.group(1);
    }

    private String getCurrentVersion() throws IOException {
        // TODO: Explore feasibility to get it from MANIFEST.MF + getClass().getPackage().getImplementationVersion()
        Properties prop = new Properties();
        InputStream input = getClass().getResourceAsStream("/com/gwtplatform/mvp/gwtp-mvp-client.properties");
        prop.load(input);
        input.close();

        return prop.getProperty("version");
    }
}
