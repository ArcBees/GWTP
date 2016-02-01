/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.mvp.client.fallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtplatform.mvp.client.ApplicationController;

public class ApplicationControllerFallback implements ApplicationController {

    public static final String REASON = "There must have been an issue generating " +
            "the ApplicationController. Please check your module configuration, ensure there " +
            "are no compile errors in your source code and ensure you are not importing sources " +
            "that cannot be compiled by GWT.";

    @Override
    public void init() {
        GWT.log(REASON);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                RootPanel.get().add(new HTML("<h3>" + REASON + "</h3>"));
            }
        });
    }

    @Override
    public void onModuleLoad() {
        init();
    }
}
