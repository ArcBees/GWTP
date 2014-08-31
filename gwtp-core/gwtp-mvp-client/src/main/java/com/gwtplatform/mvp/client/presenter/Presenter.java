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

package com.gwtplatform.mvp.client.presenter;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.GenericPresenter;
import com.gwtplatform.mvp.client.GenericPresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.presenter.root.RevealType;
import com.gwtplatform.mvp.client.presenter.slots.AbstractSlot;
import com.gwtplatform.mvp.client.presenter.slots.MultiSlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * A singleton presenter, the basic building block of the
 * <a href="http://code.google.com/intl/nl/events/io/2009/sessions/GoogleWebToolkitBestPractices.html">
 * model-view-presenter</a> architecture. Each logical page of your application will usually
 * correspond to a singleton {@link Presenter}. If you need to separate logic from view
 * in a simple graphical component, you might consider using a {@link PresenterWidget}.
 * <p/>
 * For more details on the hierarchical organization of presenters, see {@link PresenterWidget}.
 * <p/>
 * Each presenter is associated to a {@link Proxy} which is responsible for listening to the
 * various events of interest for this presenter. This makes it possible to lazily instantiate
 * the presenter and use GWT code splitting. Proxies are automatically generated for you
 * based on the information contained in the presenter. For this purpose, your presenter has to
 * define an embedded interface extending {@link Proxy} or
 * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace ProxyPlace}. Then you should apply various
 * annotations to that interface in order to specify the characteristics of your presenter. For example,
 * the following code indicates that the presenter will use GWT code splitting and will be
 * reachable at url {@code http://mydomain.com#main}:
 * <pre>
 * public class MainPagePresenter extends
 *     Presenter&lt;MainPagePresenter.MyView, MainPagePresenter.MyProxy&gt; {
 *
 *   public interface MyView extends View {}
 *
 *  {@literal @}ProxyCodeSplit
 *  {@literal @}NameToken("main")
 *   public interface MyProxy extends ProxyPlace&lt;MainPagePresenter&gt; {}
 *
 *  {@literal @}Inject
 *   public MainPagePresenter(EventBus eventBus, MyView view, MyProxy proxy) {
 *     super(eventBus, view, proxy, Presenter.RevealType.Root);
 *   }
 * }
 * </pre>
 * One of {@link com.gwtplatform.mvp.client.annotations.ProxyStandard ProxyStandard},
 * {@link com.gwtplatform.mvp.client.annotations.ProxyCodeSplit ProxyCodeSplit} or
 * {@link com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle ProxyCodeSplitBundle}
 * must always annotate the {@link Proxy} interface.
 * <p/>
 * To reveal a presenter associated to a {@link com.gwtplatform.mvp.client.proxy.ProxyPlace ProxyPlace}
 * you can simply navigate to an hyperlink corresponding to this place's name token. The
 * {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager} offers a number of method for building
 * such hyperlinks. If you want to reveal it programatically, you should build a
 * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest PlaceRequest} and call one of the
 * following method:
 * <ul>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealPlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest)
 * PlaceManager.revealPlace(PlaceRequest)}</li>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealRelativePlace(com.gwtplatform.mvp.client.proxy
 * .PlaceRequest) PlaceManager.revealRelativePlace(PlaceRequest)}</li>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealRelativePlace(com.gwtplatform.mvp.client.proxy
 * .PlaceRequest, int) PlaceManager.revealRelativePlace(PlaceRequest, int)}</li>
 * </ul>
 * If the presenter is associated to a regular {@link Proxy} and does not have
 * a name token then you should call the {@link #forceReveal()} method. For such
 * presenters, it is customary to define an event responsible of revealing them in
 * order to enforce loose coupling. This event is then handled by the presenter
 * using the {@link com.gwtplatform.mvp.client.annotations.ProxyEvent ProxyEvent}
 * mechanism.
 * <p/>
 * If the presenter is revealed and is not currently visible, then its {@link #revealInParent()} method
 * will be called.
 * <p/>
 * To hide a presenter, you can reveal another one in the same slot or you can use
 * one of the methods described in {@link PresenterWidget}.
 * <p/>
 * A presenter has a number of lifecycle methods that you can hook on to:
 * <ul>
 * <li>{@link #onBind()}
 * <li>{@link #onReveal()}
 * <li>{@link #onReset()}
 * <li>{@link #onHide()}
 * <li>{@link #onUnbind()}
 * </ul>
 * Revealing or hiding a {@link PresenterWidget} triggers an internal chain of events that result
 * in these lifecycle methods being called. For an example, here is what happens when a presenter
 * is revealed (either via {@link #forceReveal()}, or through a
 * {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager} method):
 * <ul>
 * <li>The presenter's {@link #revealInParent()} is call and it asks to be set in one of its
 * parent slot by firing a
 * {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent}</li>
 * <li>If a presenter already occupies this slot it is removed.</li>
 * <ul><li>If the presenter owning the slot is currently visible then
 * {@link #onHide()} is called on the removed presenter and, recursively,
 * on its children (bottom-up: first the children, then the parent)</li>
 * <li>If the parent is not visible, it asks to be set in one of its parent slot
 * by firing a {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent}
 * too, this continues recursively until a visible presenter is reached, or until a presenter fires
 * {@link com.gwtplatform.mvp.client.proxy.RevealRootContentEvent RevealRootContentEvent}
 * or {@link com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent RevealRootLayoutContentEvent}</li>
 * </ul>
 * <li>When the above chain stops, {@link #onReveal} is called on all the presenters
 * that were traversed. (top down: first the parent, then the children);</li>
 * <li>Finally {@link #onReset()} is called on all the
 * currently visible presenters (top-down: first the parent, then
 * the children).</li>
 * </ul>
 *
 * @param <V>      The {@link View} type.
 * @param <Proxy_> The {@link Proxy} type.
 */
@Singleton
public abstract class Presenter<V extends View, Proxy_ extends Proxy<?>> extends
        GenericPresenter<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, V, Proxy_>
        implements HasSlots {
    public Presenter(boolean autoBind, EventBus eventBus, V view, Proxy_ proxy) {
        super(autoBind, eventBus, view, proxy);
    }

    public Presenter(EventBus eventBus, V view, Proxy_ proxy, RevealType revealType,
            Type<RevealContentHandler<?>> slot) {
        super(eventBus, view, proxy, revealType, slot);
    }

    public Presenter(EventBus eventBus, V view, Proxy_ proxy, RevealType revealType) {
        super(eventBus, view, proxy, revealType);
    }

    public Presenter(EventBus eventBus, V view, Proxy_ proxy, Type<RevealContentHandler<?>> slot) {
        super(eventBus, view, proxy, slot);
    }

    public Presenter(EventBus eventBus, V view, Proxy_ proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    public
        <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>
        & Comparable<T>>
        SortedSet<T> getOrderedSlotChildren(
            Class<? extends OrderedSlot<T>> slot) {
        return new TreeSet<T>(unsafeGetChildren(slot));
    }

    @Override
    public <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>>
        Set<T> getSlotChildren(
            Class<? extends Slot<T>> slot) {
        return unsafeGetChildren(slot);
    }

    @Override
    public <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>>
        T getSlotChild(
            Class<? extends SingleSlot<T>> slot) {
        Iterator<T> it = unsafeGetChildren(slot).iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
