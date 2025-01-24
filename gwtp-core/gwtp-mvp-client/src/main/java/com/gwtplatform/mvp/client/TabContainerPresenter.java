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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.presenter.slots.IsSlot;
import com.gwtplatform.mvp.client.presenter.slots.LegacySlotConvertor;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.TabContentProxy;

/**
 * A {@link Presenter} that can display many tabs and the content of one of these tabs.
 * Note that this presenter is only meant to be used when the content are themselves
 * {@link Presenter}, if they are {@link PresenterWidget} then no special mechanism is
 * required and you can rely on a standard widget.
 * <p>
 * Classes extending {@link TabContainerPresenter} must declare one constant of type
 * {@link com.google.gwt.event.shared.GwtEvent.Type Type&lt;RequestTabsHandler&gt;}
 * and annotate it with {@link com.gwtplatform.mvp.client.annotations.RequestTabs RequestTabs}.
 * For example:
 * <pre>
 * {@literal @}RequestTabs
 * public static final Type&lt;RequestTabsHandler&gt; SLOT_RequestTabs =
 *   new Type&lt;RequestTabsHandler&gt;();
 * </pre>
 * {@link Presenter} meant to appear within a {@link TabContainerPresenter} main
 * content area should be associated to a
 * {@link com.gwtplatform.mvp.client.proxy.TabContentProxy TabContentProxy}
 * or a {@link com.gwtplatform.mvp.client.proxy.TabContentProxyPlace TabContentProxyPlace}.
 *
 * @param <V>      The specific type of the {@link View}. Must implement {@link TabPanel}.
 * @param <Proxy_> The specific type of the {@link Proxy}.
 */
public abstract class TabContainerPresenter<V extends View & TabPanel, Proxy_ extends Proxy<?>>
        extends Presenter<V, Proxy_> {
    private final Type<RequestTabsHandler> requestTabsEventType;
    private final Object tabContentSlot;

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     * @param revealType           The {@link RevealType}.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType,
            Type<ChangeTabHandler> changeTabType, RevealType revealType) {
        this(eventBus, view, proxy, tabContentSlot, requestTabsEventType, changeTabType, revealType, null);
    }

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     * @param slot                 The slot where to reveal this presenter see {@see com.google.gwt.event.shared
     * .GwtEvent.Type}
     *                             and {@see RevealContentHandler}.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType,
            Type<ChangeTabHandler> changeTabType, GwtEvent.Type<RevealContentHandler<?>> slot) {
        this(eventBus, view, proxy, tabContentSlot, requestTabsEventType, changeTabType, null, slot);
    }

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     * @param revealType           The {@link RevealType}.
     * @param slot                 The slot where to reveal this presenter see {@see com.google.gwt.event.shared
     * .GwtEvent.Type}
     *                             and {@see RevealContentHandler}.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType,
            Type<ChangeTabHandler> changeTabType, RevealType revealType, Type<RevealContentHandler<?>> slot) {
        super(eventBus, view, proxy, revealType, slot);
        this.tabContentSlot = tabContentSlot;
        this.requestTabsEventType = requestTabsEventType;
        if (changeTabType != null) {
            eventBus.addHandler(changeTabType, new ChangeTabHandler() {
                @Override
                public void onChangeTab(ChangeTabEvent event) {
                    TabContentProxy<?> tabProxy = event.getTabContentProxy();
                    getView().changeTab(tabProxy.getTab(), tabProxy.getTabData(),
                            tabProxy.getTargetHistoryToken());
                }
            });
        }
    }

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will only work when
     * instantiating this object using Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on automatic binding.
     * This version of the constructor does not allow dynamically modifying the tabs after they were
     * created.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType) {
        this(eventBus, view, proxy, tabContentSlot, requestTabsEventType, null, null, null);
    }

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will only work when
     * instantiating this object using Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on automatic binding.
     * This version of the constructor does not allow dynamically modifying the tabs after they were
     * created.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     * @param revealType           The {@link RevealType}.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType,
            RevealType revealType) {
        this(eventBus, view, proxy, tabContentSlot, requestTabsEventType, null, revealType, null);
    }

    /**
     * Creates a {@link TabContainerPresenter} that uses automatic binding. This will only work when
     * instantiating this object using Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on automatic binding.
     * This version of the constructor does not allow dynamically modifying the tabs after they were
     * created.
     *
     * @param eventBus             The {@link EventBus}.
     * @param view                 The {@link View}.
     * @param proxy                The {@link Proxy}.
     * @param tabContentSlot       An opaque object identifying the slot in which the
     *                             main content should be displayed.
     * @param requestTabsEventType The {@link Type} of the object to fire to
     *                             identify all the displayed tabs.
     * @param slot                 The slot where to reveal this presenter see {@see com.google.gwt.event.shared
     * .GwtEvent.Type}
     *                             and {@see RevealContentHandler}.
     */
    public TabContainerPresenter(EventBus eventBus, V view, Proxy_ proxy,
            Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType,
            GwtEvent.Type<RevealContentHandler<?>> slot) {
        this(eventBus, view, proxy, tabContentSlot, requestTabsEventType, null, null, slot);
    }

    /**
     * Adds a new tab to this presenter. This is meant to be called by a
     * {@link TabContentProxy} in response to a {@link RequestTabsEvent}.
     *
     * @param tabProxy The {@link TabContentProxy} containing information on the tab to add.
     * @return The newly added {@link Tab}.
     */
    public Tab addTab(final TabContentProxy<?> tabProxy) {
        return getView().addTab(tabProxy.getTabData(), tabProxy.getTargetHistoryToken());
    }

    @Override
    public void setInSlot(Object slot, PresenterWidget<?> content) {
        setInSlot(LegacySlotConvertor.convert(slot), content);
    }

    @Override
    public <T extends PresenterWidget<?>> void setInSlot(IsSlot<T> slot, T child) {
        super.setInSlot(slot, child);

        // TODO: Consider switching this to an event bus based mechanism where the
        // child presenter fires an event when it is revealed and the parent highlights the tab.

        // If we're setting a presenter attached to an actual slot, then highlight the tab
        if (slot == tabContentSlot) {
            try {
                Presenter<?, ?> presenter = (Presenter<?, ?>) child;
                TabContentProxy<?> proxy = (TabContentProxy<?>) presenter.getProxy();
                getView().setActiveTab(proxy.getTab());
            } catch (Exception e) {
                // We can't cast, no worry, we just won't highlight a tab
            }
        }
    }

    @Override
    protected void onBind() {
        super.onBind();

        // The following call will trigger a series of call to addTab, so
        // we should make sure we clear all the tabs when unbinding.
        RequestTabsEvent.fire(this, requestTabsEventType, this);
    }

    @Override
    protected void onUnbind() {
        super.onUnbind();

        // The tabs are added indirectly in onBind() via the RequestTabsEvent, so we
        // clear them now.
        getView().removeTabs();
    }
}
