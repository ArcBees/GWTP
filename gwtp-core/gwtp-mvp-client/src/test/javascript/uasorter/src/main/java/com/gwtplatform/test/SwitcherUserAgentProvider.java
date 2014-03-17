package com.gwtplatform.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SwitcherUserAgentProvider implements UserAgentProvider {

    private final JsonArray folders;

    public SwitcherUserAgentProvider() throws IOException {
        this.folders = getUnsortedUserAgents().get("useragentswitcher").getAsJsonObject().get("folder").getAsJsonArray();

    }

    public String getRandomUserAgent(final StringBuilder out) {
        return getRandomUserAgent(out, folders);
    }

    private JsonObject getUnsortedUserAgents() throws IOException {
        final String userAgentXml = FileUtils.readFileToString(new File("../useragentswitcher.xml"));
        try {
            final JSONObject xmlJSONObj = XML.toJSONObject(userAgentXml);
            return new JsonParser().parse(xmlJSONObj.toString()).getAsJsonObject();
        } catch (final JSONException je) {
            System.out.println(je.toString());
            throw new RuntimeException("Could not convert xml to json");
        }
    }

    private String getRandomUserAgent(final StringBuilder out, final JsonArray folders) {
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
