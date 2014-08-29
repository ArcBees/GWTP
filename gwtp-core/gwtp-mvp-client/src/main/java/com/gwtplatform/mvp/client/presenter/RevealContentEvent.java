/**
 * Copyright 2014 ArcBees Inc.
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
package com.gwtplatform.mvp.client.presenter;

import com.google.gwt.event.shared.GwtEvent;

public class RevealContentEvent extends GwtEvent<RevealContentHandler<?>> {

    private final Presenter<?, ?> content;
    private final Type<RevealContentHandler<?>> type;

    public RevealContentEvent(final Type<RevealContentHandler<?>> type, final Presenter<?, ?> content) {
        this.content = content;
        this.type = type;
    }

    @Override
    protected void dispatch(final RevealContentHandler handler) {
        handler.onRevealContent(this);
    }

    @Override
    public Type<RevealContentHandler<?>> getAssociatedType() {
        return type;
    }

    Presenter<?, ?> getContent() {
        return content;
    }
}
