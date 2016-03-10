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

package com.gwtplatform.crawlerservice.server.domain;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CachedPageTest {
    private static final int LONG_TIMEOUT = 2000;
    private static final int SHORT_TIMEOUT = 500;

    @Test
    public void isExpired_notExpired() {
        // given
        Date now = new Date();
        CachedPage cachedPage = new CachedPage();
        Date earlierDate = new Date(now.getTime() - 1000 * 1000);
        cachedPage.setFetchDate(earlierDate);

        // when
        boolean expired = cachedPage.isExpired(LONG_TIMEOUT);

        // then
        assertFalse(expired);
    }

    @Test
    public void isExpired_isExpired() {
        // given
        Date now = new Date();
        CachedPage cachedPage = new CachedPage();
        Date earlierDate = new Date(now.getTime() - 1000 * 1000);
        cachedPage.setFetchDate(earlierDate);

        // when
        boolean expired = cachedPage.isExpired(SHORT_TIMEOUT);

        // then
        assertTrue(expired);
    }
}
