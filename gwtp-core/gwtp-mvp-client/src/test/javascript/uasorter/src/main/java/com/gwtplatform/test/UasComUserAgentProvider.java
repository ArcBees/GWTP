package com.gwtplatform.test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UasComUserAgentProvider implements UserAgentProvider {

    private static final Random random = new Random();

    private final JsonObject unsortedUserAgents;

    public UasComUserAgentProvider() throws IOException {
        this.unsortedUserAgents = getUnsortedUserAgents();
        System.out.println("instant uascom");
    }

    private JsonObject getUnsortedUserAgents() throws IOException {
        System.out.println("Init Uascom");
        final String unsortedUserAgentsJson = FileUtils.readFileToString(new File("../uascom.json"), "UTF-8");
        System.out.println("fetched unsorted Uascom");
        return new JsonParser().parse(unsortedUserAgentsJson).getAsJsonObject();
    }

    public String getRandomUserAgent(final StringBuilder out) {

        return getRandomUserAgent(out, navigateTo(1, 2, 1, 0));
    }

    private JsonArray navigateTo(final int... navList) {
        JsonArray node = unsortedUserAgents.get("content").getAsJsonArray();
        for (final int branch : navList) {
            node = node.get(branch).getAsJsonObject().get("content").getAsJsonArray();
        }
        return node;

    }

    private String getRandomUserAgent(final StringBuilder out, final JsonArray contentArray) {
        int agentStart = -1;
        for (int i = 0; i < contentArray.size(); i++) {
            final JsonObject content = contentArray.get(i).getAsJsonObject();
            if (content.has("type") && content.get("type").getAsString().matches("h[1-3]")) {
                //printHeaders(out, content.get("content"));
            }
            if (content.has("type") && content.get("type").getAsString().equals("h4")) {
                agentStart = i;
                break;
            }
        }

        int randomEntry = agentStart + random.nextInt(contentArray.size() - (agentStart + 1));
        while (!contentArray.get(randomEntry).getAsJsonObject().get("type").getAsString().equals("h4")) {
            randomEntry = agentStart + random.nextInt(contentArray.size() - (agentStart + 1));

        }
        printHeaders(out, contentArray.get(randomEntry).getAsJsonObject().get("content"));

        final JsonObject ul = contentArray.get(randomEntry + 1).getAsJsonObject();

        final JsonArray liAry = forceElementToBeArray(ul.get("content"));
        final JsonObject a = liAry.get(random.nextInt(liAry.size())).getAsJsonObject();
        return a.get("content").getAsJsonObject().get("content").getAsJsonObject().get("text").getAsString();

    }

    private JsonArray forceElementToBeArray(final JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        } else if (jsonElement.isJsonObject()) {
            return new JsonParser().parse("[" + jsonElement.toString() + "]").getAsJsonArray();
        }
        return null;
    }

    private void printHeaders(final StringBuilder out, final JsonElement content) {
        if (content.isJsonObject() && content.getAsJsonObject().has("text")) {
            out.append("Category: " + content.getAsJsonObject().get("text").getAsString() + "\n");
        } else if (content.isJsonArray()) {
            final JsonArray ary = content.getAsJsonArray();
            for (int i = 0; i < ary.size(); i++) {
                printHeaders(out, ary.get(i));
            }
        }

    }
}
