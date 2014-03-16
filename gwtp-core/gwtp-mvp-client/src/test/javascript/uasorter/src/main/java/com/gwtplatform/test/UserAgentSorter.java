package com.gwtplatform.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserAgentSorter {

    public static void main(final String[] args) throws IOException {
        System.out.println("Starting UserAgentSorter");

        System.out.println("How many useragents would you like to sort?");
        int repeat = 1;
        try {
            repeat = Integer.valueOf(System.console().readLine());
        } catch (final NumberFormatException e) {
            System.out.println("Could not interpret repeat paramater.");
        }

        System.out.println("Running " + repeat + " times.");
        System.out.println("----------------------------------------------\n");
        final String unsortedUserAgentsJson = getUnsortedUserAgents();
        final List<String> mobileUserAgents = getUserAgents("../mobileUserAgents.js");
        final List<String> tabletUserAgents = getUserAgents("../tabletUserAgents.js");
        final List<String> desktopUserAgents = getUserAgents("../desktopUserAgents.js");
        saveUserAgents(mobileUserAgents, "../mobileUserAgents.js");
        saveUserAgents(tabletUserAgents, "../tabletUserAgents.js");
        saveUserAgents(desktopUserAgents, "../desktopUserAgents.js");

        for (int i = 0; i < repeat; i++) {
            final JsonArray folders = new JsonParser().parse(unsortedUserAgentsJson).getAsJsonObject().get("useragentswitcher").getAsJsonObject().get("folder").getAsJsonArray();
            StringBuilder out = new StringBuilder();
            String userAgent = getRandomUserAgent(out, folders);
            int searchCount = 1000;
            while (searchCount-- > 0 && (userAgent == null || userAgent.isEmpty() || mobileUserAgents.contains(userAgent) || tabletUserAgents.contains(userAgent) || desktopUserAgents.contains(userAgent))) {
                out = new StringBuilder();
                userAgent = getRandomUserAgent(out, folders);
            }

            if (searchCount <= 0) {
                System.out.println(i + ": All the userAgents I found had already been sorted.");
                continue;
            }

            System.out.print(out.toString());
            System.out.println("User Agent: " + userAgent);
            System.out.println("Bots and console programs are generally desktop browsers.  Be aware of mobile bots though.");
            System.out.println("Is the useragent a desktop, tablet or mobile browser? d t m?");

            final String answer = System.console().readLine().toLowerCase();

            if (answer.startsWith("d")) {
                desktopUserAgents.add(userAgent);
            } else if (answer.startsWith("t")) {
                tabletUserAgents.add(userAgent);
            } else if (answer.startsWith("m")) {
                mobileUserAgents.add(userAgent);
            } else if (answer.startsWith("q")) {
                System.out.println("Quitting, good bye");
                return;
            } else {
                System.out.println("That wasn't a valid answer");
                continue;
            }

            saveUserAgents(mobileUserAgents, "../mobileUserAgents.js");
            saveUserAgents(tabletUserAgents, "../tabletUserAgents.js");
            saveUserAgents(desktopUserAgents, "../desktopUserAgents.js");

            System.out.println("----------------------------------------------");
            System.out.println("Thank You: " + ((repeat - 1) - i) + " to go");
            System.out.println("----------------------------------------------\n");

        }

    }

    private static String getUnsortedUserAgents() throws IOException {
        final String userAgentXml = FileUtils.readFileToString(new File("../useragentswitcher.xml"));
        try {
            final JSONObject xmlJSONObj = XML.toJSONObject(userAgentXml);
            return xmlJSONObj.toString(4);
        } catch (final JSONException je) {
            System.out.println(je.toString());
            throw new RuntimeException("Could not convert xml to json");
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> getUserAgents(final String fileName) throws IOException {
        final String currentUserAgents = FileUtils.readFileToString(new File(fileName));
        final String[] split = currentUserAgents.split("=");
        final ArrayList<String> result = new ArrayList<String>();
        return new Gson().fromJson(split[1], result.getClass());
    }

    private static void saveUserAgents(final List<String> userAgents, final String fileName) throws IOException {
        Collections.sort(userAgents);

        final String currentUserAgents = FileUtils.readFileToString(new File(fileName));
        final String[] split = currentUserAgents.split("=");

        final Gson gs = new GsonBuilder().setPrettyPrinting().create();
        final String newUserAgentsCoffee = split[0] + "= " + gs.toJson(userAgents);

        FileUtils.writeStringToFile(new File(fileName), newUserAgentsCoffee);

    }

    private static String getRandomUserAgent(final StringBuilder out, final JsonArray folders) {
        return getRandomUserAgent(out, getRandomFolder(out, folders));
    }

    private static String getRandomUserAgent(final StringBuilder out, final JsonObject folder) {
        final JsonElement userAgent = folder.get("useragent");
        JsonObject userAgentObject;
        if (userAgent.isJsonArray()) {
            userAgentObject = userAgent.getAsJsonArray().get((int) (Math.random() * (userAgent.getAsJsonArray().size()))).getAsJsonObject();
        } else {
            userAgentObject = userAgent.getAsJsonObject();
        }
        out.append("Name: " + userAgentObject.get("description").getAsString() + "\n");
        if (!userAgentObject.has("useragent") || userAgentObject.get("useragent").isJsonNull()) {
            return null;
        }
        return userAgentObject.get("useragent").getAsString();

    }

    private static JsonObject getRandomFolder(final StringBuilder out, final JsonArray folders) {
        final JsonElement randomFolder = folders.get((int) (Math.random() * (folders.size())));
        return getRandomSubFolder(out, randomFolder.getAsJsonObject());
    }

    private static JsonObject getRandomSubFolder(final StringBuilder out, final JsonObject folder) {
        out.append("Category: " + folder.get("description") + "\n");
        if (folder.has("folder")) {
            if (Math.random() < 0.5 || !folder.has("useragent")) {
                final JsonElement nextFolder = folder.get("folder");
                if (nextFolder.isJsonArray()) {
                    return getRandomFolder(out, nextFolder.getAsJsonArray());
                } else {
                    return getRandomSubFolder(out, nextFolder.getAsJsonObject());
                }
            }
        }
        return folder;
    }
}
