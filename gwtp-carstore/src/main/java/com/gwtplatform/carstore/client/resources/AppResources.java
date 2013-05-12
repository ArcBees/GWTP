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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundle {
    public interface Styles extends CssResource {
        String success();

        String error();

        String message();

        String close();

        String doneAction();

        String addAction();

        String editAction();

        String deleteAction();

        String menuAction();

        String mobileTextField();

        String closeButton();

        @ClassName("gwt-PopupPanel")
        String gwtPopupPanel();

        String bigTitle();
    }

    Styles styles();

    @Source("carSample.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource logo();

    @Source("big_logo.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource bigLogo();

    @Source("ic_done.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource icDone();

    @Source("ic_add.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource icAdd();

    @Source("ic_delete.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource icDelete();

    @Source("ic_update.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource icUpdate();

    @Source("ic_menu.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource icMenu();

    @Source("remove_small.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
    ImageResource removeSmall();
}
