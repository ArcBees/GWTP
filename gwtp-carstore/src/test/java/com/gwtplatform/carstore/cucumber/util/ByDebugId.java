/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.cucumber.util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.gwt.user.client.ui.UIObject;

public class ByDebugId extends By {
    private final String debugId;

    public static By id(String debugId) {
        return new ByDebugId(debugId);
    }

    public ByDebugId(String debugId) {
        this.debugId = debugId;
    }

    @Override
    public WebElement findElement(SearchContext context) {
        return context.findElement(By.id(UIObject.DEBUG_ID_PREFIX + debugId));
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        throw new UnsupportedOperationException();
    }
}
