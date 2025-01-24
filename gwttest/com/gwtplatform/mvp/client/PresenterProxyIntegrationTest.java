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

import org.junit.Ignore;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * Integration test for {@link PresenterImpl} and automatically-generated
 * {@link Proxy} classes.
 * <p>
 * TODO: This doesn't work yet. See
 * http://code.google.com/p/gwt-platform/issues/detail?id=38
 */
@Ignore
public class PresenterProxyIntegrationTest extends GWTTestCase {
    public static class MyPresenter extends
            Presenter<View, MyPresenter.MyProxy> {

        /**
         * {@link MyPresenter}'s proxy.
         */
        @ProxyCodeSplit
        public interface MyProxy extends Proxy<MyPresenter> {
        }

        public MyPresenter() {
            super((EventBus) GWT.create(EventBus.class),
                    (View) GWT.create(View.class), (MyProxy) GWT.create(MyProxy.class));
        }

        @Override
        protected void revealInParent() {
        }
    }

    @Override
    public String getModuleName() {
        return "com.gwtplatform.mvp.Mvp";
    }

    public void test() {
        GWT.create(MyPresenter.class);
    }
}
