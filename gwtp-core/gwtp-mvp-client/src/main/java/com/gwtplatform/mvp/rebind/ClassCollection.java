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

package com.gwtplatform.mvp.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.gwtplatform.mvp.client.ChangeTabHandler;
import com.gwtplatform.mvp.client.DelayedBind;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.NonLeafTabContentProxy;
import com.gwtplatform.mvp.client.proxy.NonLeafTabContentProxyImpl;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceImpl;
import com.gwtplatform.mvp.client.proxy.PlaceWithGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceWithGatekeeperWithParams;
import com.gwtplatform.mvp.client.proxy.ProxyImpl;
import com.gwtplatform.mvp.client.proxy.ProxyPlaceImpl;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.SetPlaceTitleHandler;
import com.gwtplatform.mvp.client.proxy.TabContentProxy;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlaceImpl;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import jakarta.inject.Provider;

/**
 * Contains all the classes that are useful to various generators.
 * This class contains no getters, fields are accessed directly.
 */
public class ClassCollection {

    static final String asyncProviderClassName = AsyncProvider.class.getCanonicalName();
    static final String baseGinjectorClassName = Ginjector.class.getCanonicalName();
    static final String basePlaceClassName = Place.class.getCanonicalName();
    static final String basePresenterClassName = Presenter.class.getCanonicalName();
    static final String delayedBindClassName = DelayedBind.class.getCanonicalName();
    static final String eventHandlerClassName = EventHandler.class.getCanonicalName();
    static final String gatekeeperClassName = Gatekeeper.class.getCanonicalName();
    static final String gwtEventClassName = GwtEvent.class.getCanonicalName();
    static final String gwtEventTypeClassName = GwtEvent.Type.class.getCanonicalName();
    static final String placeImplClassName = PlaceImpl.class.getCanonicalName();
    static final String placeRequestClassName = PlaceRequest.class.getCanonicalName();
    static final String placeWithGatekeeperClassName = PlaceWithGatekeeper.class.getCanonicalName();
    static final String placeWithGatekeeperWithParamsClassName =
            PlaceWithGatekeeperWithParams.class.getCanonicalName();
    static final String providerClassName = Provider.class.getCanonicalName();
    static final String proxyImplClassName = ProxyImpl.class.getCanonicalName();
    static final String proxyPlaceImplClassName = ProxyPlaceImpl.class.getCanonicalName();
    static final String requestTabsHandlerClassName = RequestTabsHandler.class.getCanonicalName();
    static final String changeTabHandlerClassName = ChangeTabHandler.class.getCanonicalName();
    static final String revealContentHandlerClassName = RevealContentHandler.class.getCanonicalName();
    static final String setPlaceTitleHandlerClassName = SetPlaceTitleHandler.class.getCanonicalName();
    static final String tabContentProxyClassName = TabContentProxy.class.getCanonicalName();
    static final String nonLeafTabContentProxyClassName =
            NonLeafTabContentProxy.class.getCanonicalName();
    static final String nonLeafTabContentProxyImplClassName =
            NonLeafTabContentProxyImpl.class.getCanonicalName();
    static final String tabContentProxyPlaceImplClassName =
            TabContentProxyPlaceImpl.class.getCanonicalName();
    static final String typeClassName = Type.class.getCanonicalName();
    static final String nestedSlotClassName = NestedSlot.class.getCanonicalName();
    static final String tabDataClassName = TabData.class.getCanonicalName();
    final JGenericType asyncProviderClass;
    final JClassType baseGinjectorClass;
    final JClassType basePlaceClass;
    final JClassType basePresenterClass;
    final JClassType eventHandlerClass;
    final JClassType gatekeeperClass;
    final JGenericType gwtEventClass;
    final JGenericType gwtEventTypeClass;
    final JClassType placeRequestClass;
    final JGenericType providerClass;
    final JClassType requestTabsHandlerClass;
    final JClassType changeTabHandlerClass;
    final JClassType revealContentHandlerClass;
    final JClassType setPlaceTitleHandlerClass;
    final JClassType stringClass;
    final JClassType tabDataClass;
    final JClassType tabContentProxyClass;
    final JClassType nonLeafTabContentProxyClass;
    final JClassType typeClass;
    final JClassType nestedSlotClass;

    public ClassCollection(TypeOracle oracle) {
        // Find the required base types
        stringClass = oracle.findType(String.class.getName());
        basePresenterClass = oracle.findType(basePresenterClassName);
        baseGinjectorClass = oracle.findType(baseGinjectorClassName);
        typeClass = oracle.findType(typeClassName);
        revealContentHandlerClass = oracle.findType(revealContentHandlerClassName);
        requestTabsHandlerClass = oracle.findType(requestTabsHandlerClassName);
        changeTabHandlerClass = oracle.findType(changeTabHandlerClassName);
        providerClass = oracle.findType(providerClassName).isGenericType();
        asyncProviderClass = oracle.findType(asyncProviderClassName).isGenericType();
        basePlaceClass = oracle.findType(basePlaceClassName);
        tabContentProxyClass = oracle.findType(tabContentProxyClassName);
        nonLeafTabContentProxyClass = oracle.findType(nonLeafTabContentProxyClassName);
        gatekeeperClass = oracle.findType(gatekeeperClassName);
        placeRequestClass = oracle.findType(placeRequestClassName);
        gwtEventClass = oracle.findType(gwtEventClassName).isGenericType();
        gwtEventTypeClass = oracle.findType(gwtEventTypeClassName).isGenericType();
        eventHandlerClass = oracle.findType(eventHandlerClassName);
        setPlaceTitleHandlerClass = oracle.findType(setPlaceTitleHandlerClassName);
        tabDataClass = oracle.findType(tabDataClassName);
        nestedSlotClass = oracle.findType(nestedSlotClassName);
    }
}
