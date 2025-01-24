/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.client.googleanalytics;

/**
 * This interface lets you manually register Google Analytics events in
 * your application.
 * <p>
 * Event Tracking is a method available in the {@code ga.js} tracking code that
 * you can use to record user interaction with website elements. This is
 * accomplished by attaching the method call to the particular UI element you
 * want to track. When used this way, all user activity on such elements is
 * calculated and displayed as Events in the Analytics reporting interface.
 * Additionally, pageview calculations are unaffected by user activity tracked
 * using the Event Tracking method. Finally, Event Tracking employs an
 * object-oriented model that you can use to collect and classify different
 * types of interaction with your web page objects.
 * <p>
 * Inject this interface in your presenters when you need to interact with the
 * Google Analytics module, for example to log your custom event. You can
 * also configure automatic registration of navigation event using
 * {@link GoogleAnalyticsNavigationTracker}.
 * <p>
 * Also, for more information about Event Tracking, please read:
 * <a href="http://code.google.com/apis/analytics/docs/tracking/eventTrackerGuide.html">Event Tracking Guide</a>
 * @deprecated Since 1.4. Use https://github.com/ArcBees/universal-analytics instead.
 */
@Deprecated
public interface GoogleAnalytics {

    /**
     * Initializes the script for Google Analytics for a specific userAccount.
     *
     * @param userAccount The Google Analytics account. (i.e. {@code UA-12345678-1})
     */
    void init(String userAccount);

    /**
     * Given that you already have initialized your default Google account with
     * {@link #init(String)}, this function will add a new account on which you'll
     * be able to track events and pages.
     *
     * @param trackerName A user-defined tracker name.
     * @param userAccount The Google Analytics account. (i.e. {@code UA-12345678-1})
     */
    void addAccount(String trackerName, String userAccount);

    /**
     * Tracks the root of your application.
     */
    void trackPageview();

    /**
     * Tracks a page given a specific page name.
     *
     * @param pageName The page name to track.
     */
    void trackPageview(String pageName);

    /**
     * Tracks a page given a specific page name on a user defined tracker
     * name. See {@link #addAccount(String, String)}.
     *
     * @param trackerName A user defined tracker name.
     * @param pageName    The page name to track.
     */
    void trackPageview(String trackerName, String pageName);

    /**
     * Tracks an event in Google analytics.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     */
    void trackEvent(String category, String action);

    /**
     * Tracks an event in Google analytics on a user defined tracker name.
     * See {@link #addAccount(String, String)}.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     */
    void trackEventWithTracker(String trackerName, String category, String action);

    /**
     * Tracks an event in Google analytics, attaching a label and value.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     * @param optLabel An string to provide additional dimensions to the event
     *                 data.
     */
    void trackEvent(String category, String action, String optLabel);

    /**
     * Tracks an event in Google analytics, attaching a label and value on a user
     * defined tracker name. See {@link #addAccount(String, String)}.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     * @param optLabel An string to provide additional dimensions to the event
     *                 data.
     */
    void trackEventWithTracker(String trackerName, String category, String action,
            String optLabel);

    /**
     * Tracks an event in Google analytics, attaching a label and value.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     * @param optLabel An string to provide additional dimensions to the event
     *                 data.
     * @param optValue An integer that you can use to provide numerical data about
     *                 the user event.
     */
    void trackEvent(String category, String action, String optLabel, int optValue);

    /**
     * Tracks an event in Google analytics, attaching a label and value on a user
     * defined tracker name. See {@link #addAccount(String, String)}.
     *
     * @param category The name you supply for the group of objects you want to
     *                 track.
     * @param action   A string that is uniquely paired with each category, and
     *                 commonly used to define the type of user interaction for the web
     *                 object.
     * @param optLabel An string to provide additional dimensions to the event
     *                 data.
     * @param optValue An integer that you can use to provide numerical data about
     *                 the user event.
     */
    void trackEventWithTracker(String trackerName, String category, String action,
            String optLabel, int optValue);

    /**
     * Tracks an event in Google analytics, attaching a label and value.
     *
     * @param category          The name you supply for the group of objects you want to
     *                          track.
     * @param action            A string that is uniquely paired with each category, and
     *                          commonly used to define the type of user interaction for the web
     *                          object.
     * @param optLabel          An string to provide additional dimensions to the event
     *                          data.
     * @param optValue          An integer that you can use to provide numerical data about
     *                          the user event.
     * @param optNonInteraction A boolean that when set to true, indicates that the event hit
     *                          will not be used in bounce-rate calculation.
     */
    void trackEvent(String category, String action, String optLabel, int optValue, boolean optNonInteraction);

    /**
     * Tracks an event in Google analytics, attaching a label and value on a user
     * defined tracker name. See {@link #addAccount(String, String)}.
     *
     * @param category          The name you supply for the group of objects you want to
     *                          track.
     * @param action            A string that is uniquely paired with each category, and
     *                          commonly used to define the type of user interaction for the web
     *                          object.
     * @param optLabel          An string to provide additional dimensions to the event
     *                          data.
     * @param optValue          An integer that you can use to provide numerical data about
     *                          the user event.
     * @param optNonInteraction A boolean that when set to true, indicates that the event hit
     *                          will not be used in bounce-rate calculation.
     */
    void trackEventWithTracker(String trackerName, String category, String action,
            String optLabel, int optValue, boolean optNonInteraction);
}
