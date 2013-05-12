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

package com.gwtplatform.carstore.client.resources;

import com.google.gwt.user.cellview.client.CellList;

public interface MobileDataListStyle extends CellList.Resources {
    @Source({CellList.Style.DEFAULT_CSS, "mobileDataListStyle.css"})
    ListStyle cellListStyle();

    interface ListStyle extends CellList.Style {
        String removeButton();
    }
}

