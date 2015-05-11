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

package com.gwtplatform.mvp.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.gwtplatform.mvp.client.gwt.mvp.MvpGwtTestInSuite;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatterGwtTestInSuite;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * All the GWT test cases of gwtp-mvp-client. Does not extend {@code GWTTestSuite} as it is
 * not compatible with gwt-maven-plugin. See {@link http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html}.
 */
public class MvpClientGwtTestSuite extends TestCase {
    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("All the GWT test cases of gwtp-mvp-client and gwtp-mvp-shared.");
        suite.addTestSuite(ParameterTokenFormatterGwtTestInSuite.class);
        suite.addTestSuite(MvpGwtTestInSuite.class);
        return suite;
    }
}
