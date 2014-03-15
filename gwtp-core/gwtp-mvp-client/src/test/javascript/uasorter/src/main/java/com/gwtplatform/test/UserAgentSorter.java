package com.gwtplatform.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserAgentSorter {

	public static void main(final String[] args) throws IOException {
		System.out.println("Starting UserAgentSorter");
		int repeat = 1;
		if (args.length > 0) {
			try {
				repeat = Integer.valueOf(args[0]);
			} catch (final NumberFormatException e) {
				System.out.println("Could not interpret repeat paramater.");
			}
		}
		System.out.println("Running " + repeat + " times.");
		System.out.println("----------------------------------------------\n");
		for (int i = 0; i < repeat; i++) {
			final String unsortedUserAgentsJson = FileUtils.readFileToString(new File("../unsortedUserAgents.json"));
			//System.out.println(unsortedUserAgentsJson);
			final JsonArray folders = new JsonParser().parse(unsortedUserAgentsJson).getAsJsonObject().get("useragentswitcher").getAsJsonObject().get("folder").getAsJsonArray();

			StringBuilder out = new StringBuilder();
			String userAgent = getRandomUserAgent(getRandomFolder(out, folders));
			while (userAgent == null) {
				out = new StringBuilder();
				userAgent = getRandomUserAgent(getRandomFolder(out, folders));
			}
			System.out.print(out.toString());
			System.out.println("User Agent: " + userAgent);
			System.out.println("Bots and console programs are generally desktop browsers.  Be aware of mobile bots though.");
			System.out.println("Is the useragent a desktop, tablet or mobile browser? d t m?");

			final String answer = System.console().readLine().toLowerCase();

			if (answer.startsWith("d")) {
				addUserAgent(userAgent, "../desktopUserAgents.js");
			} else if (answer.startsWith("t")) {
				addUserAgent(userAgent, "../tabletUserAgents.js");
			} else if (answer.startsWith("m")) {
				addUserAgent(userAgent, "../mobileUserAgents.js");
			} else if (answer.startsWith("q")) {
				System.out.println("Quiting, good bye");
				return;
			} else {
				System.out.println("That wasn't a valid answer");
				break;
			}

			//overwrite found useragent with null

			FileUtils.writeStringToFile(new File("../unsortedUserAgents.json"), unsortedUserAgentsJson.replace("\"" + userAgent + "\"", "null"));
			System.out.println("----------------------------------------------");
			System.out.println("Thank You: " + ((repeat - 1) - i) + " to go");
			System.out.println("----------------------------------------------\n");
		}

	}

	private static void addUserAgent(final String userAgent, final String fileName) throws IOException {
		final String currentUserAgents = FileUtils.readFileToString(new File(fileName));
		final String[] split = currentUserAgents.split("=");
		final JsonArray existingUserAgents = new JsonParser().parse(split[1]).getAsJsonArray();
		for (int i = 0; i < existingUserAgents.size(); i++) {
			if (existingUserAgents.get(i).getAsString().equals(userAgent)) {
				System.out.println("User Agent is already sorted.");
				return;
			}
		}

		System.out.println("Adding " + userAgent + " to " + fileName);

		existingUserAgents.add(new JsonParser().parse("\"" + userAgent + "\""));

		final Gson gs = new GsonBuilder().setPrettyPrinting().create();
		final String newUserAgentsCoffee = split[0] + "= " + gs.toJson(existingUserAgents);

		FileUtils.writeStringToFile(new File(fileName), newUserAgentsCoffee);

	}

	private static String getRandomUserAgent(final JsonObject folder) {
		JsonElement userAgent = folder.get("useragent");
		if (userAgent.isJsonArray()) {
			userAgent = userAgent.getAsJsonArray().get((int) (Math.random() * (userAgent.getAsJsonArray().size() - 1)));
		}
		if (userAgent.getAsJsonObject().get("useragent").isJsonNull()) {
			return null;
		}
		return userAgent.getAsJsonObject().get("useragent").getAsString();

	}

	private static JsonObject getRandomFolder(final StringBuilder out, final JsonArray folders) {
		final JsonElement randomFolder = folders.get((int) (Math.random() * (folders.size() - 1)));
		return getRandomSubFolder(out, randomFolder.getAsJsonObject());
	}

	private static JsonObject getRandomSubFolder(final StringBuilder out, final JsonObject folder) {
		out.append("Description: " + folder.get("description") + "\n");
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
