/**
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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.junit.client.GWTTestCase;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder;

/**
 * Integration test for various components of GWTP's MVP module.
 */
public class MvpGwtTestInSuite extends GWTTestCase {
    GinjectorTestUtilGwt ginjector;
    MainPresenterTestUtilGwt presenter;

    @Override
    public String getModuleName() {
        return "com.gwtplatform.mvp.MvpGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        InstantiationCounterTestUtilGwt.resetCounter();
        ginjector = GWT.create(GinjectorTestUtilGwt.class);
        DelayedBindRegistry.bind(ginjector);
    }

    /**
     * Verifies that the ginjector is created only once.
     */
    public void testShouldCreateOnlyOneGinjector() {
        ginjector.getPlaceManager().revealCurrentPlace();

        assertEquals(1, InstantiationCounterTestUtilGwt.getCounter());
    }

    /**
     * Verify multiple name tokens.
     */
    public void testMultipleTokens() {
        ginjector.getPlaceManager().revealDefaultPlace();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                assertTrue(ginjector.getMainPresenter().get().isVisible());
                assertFalse(ginjector.getAdminPresenter().get().isVisible());

                revealAdmin();
            }
        });

        delayTestFinish(1000);
    }

    private void revealAdmin() {
        PlaceRequest placeRequest = new Builder().nameToken("admin").build();
        ginjector.getPlaceManager().revealPlace(placeRequest);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                assertFalse(ginjector.getMainPresenter().get().isVisible());
                assertTrue(ginjector.getAdminPresenter().get().isVisible());

                revealDefaultPlace();
            }
        });
    }

    private void revealDefaultPlace() {
        ginjector.getPlaceManager().revealDefaultPlace();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                assertTrue(ginjector.getMainPresenter().get().isVisible());
                assertFalse(ginjector.getAdminPresenter().get().isVisible());

                revealSelfService();
            }
        });
    }

    private void revealSelfService() {
        PlaceRequest placeRequest = new Builder().nameToken("selfService").build();
        ginjector.getPlaceManager().revealPlace(placeRequest);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                assertFalse(ginjector.getMainPresenter().get().isVisible());
                assertTrue(ginjector.getAdminPresenter().get().isVisible());

                finishTest();
            }
        });
    }
}
