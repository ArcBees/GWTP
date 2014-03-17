package com.gwtplatform.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserAgentSorter {

    private final static List<UserAgentProvider> userAgentProviders = new ArrayList<UserAgentProvider>();

    static {
        try {
            userAgentProviders.add(new SwitcherUserAgentProvider());
            userAgentProviders.add(new UasComUserAgentProvider());
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

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
        final List<String> mobileUserAgents = getUserAgents("../mobileUserAgents.js");
        final List<String> tabletUserAgents = getUserAgents("../tabletUserAgents.js");
        final List<String> desktopUserAgents = getUserAgents("../desktopUserAgents.js");
        for (int i = 0; i < repeat; i++) {
            StringBuilder out = new StringBuilder();
            String userAgent = getRandomUserAgent(out);
            int searchCount = 1000;
            while (searchCount-- > 0 && (userAgent.isEmpty() || mobileUserAgents.contains(userAgent) || tabletUserAgents.contains(userAgent) || desktopUserAgents.contains(userAgent))) {
                out = new StringBuilder();
                userAgent = getRandomUserAgent(out);
            }

            if (searchCount < 0) {
                System.out.println(i + ": All the user agents I found had already been sorted.");
                continue;
            }

            System.out.print(out.toString());
            System.out.println("User Agent: " + userAgent);
            System.out.println("Bots and console programs are generally desktop browsers.  Be aware of mobile bots though.");
            System.out.println("Is the useragent a desktop, tablet or mobile browser? d t m?");

            final String answer = System.console().readLine().toLowerCase();

            if (answer.startsWith("d")) {
                desktopUserAgents.add(userAgent);
                saveUserAgents(desktopUserAgents, "../desktopUserAgents.js");
            } else if (answer.startsWith("t")) {
                tabletUserAgents.add(userAgent);
                saveUserAgents(tabletUserAgents, "../tabletUserAgents.js");
            } else if (answer.startsWith("m")) {
                mobileUserAgents.add(userAgent);
                saveUserAgents(mobileUserAgents, "../mobileUserAgents.js");
            } else if (answer.startsWith("q")) {
                System.out.println("Quitting, good bye");
                return;
            } else {
                System.out.println("That wasn't a valid answer");
                continue;
            }

            System.out.println("----------------------------------------------");
            System.out.println("Thank You: " + ((repeat - 1) - i) + " to go");
            System.out.println("----------------------------------------------\n");
        }

    }

    private static String getRandomUserAgent(final StringBuilder out) {
        return userAgentProviders.get((int) (Math.random() * userAgentProviders.size())).getRandomUserAgent(out);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getUserAgents(final String fileName) throws IOException {
        final String currentUserAgents = FileUtils.readFileToString(new File(fileName));
        final String[] split = currentUserAgents.split("=");
        return new Gson().fromJson(split[1], new ArrayList<String>().getClass());
    }

    private static void saveUserAgents(final List<String> userAgents, final String fileName) throws IOException {
        Collections.sort(userAgents);

        final String currentUserAgents = FileUtils.readFileToString(new File(fileName));
        final String[] split = currentUserAgents.split("=");

        final Gson gs = new GsonBuilder().setPrettyPrinting().create();
        final String newUserAgents = split[0] + "= " + gs.toJson(userAgents);

        FileUtils.writeStringToFile(new File(fileName), newUserAgents);
    }

}
