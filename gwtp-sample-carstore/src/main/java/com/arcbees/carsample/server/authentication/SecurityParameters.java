package com.arcbees.carsample.server.authentication;

public class SecurityParameters {
    private static final String USER_SESSION_KEY = "userId";

    public static String getUserSessionKey() {
        return USER_SESSION_KEY;
    }
}
