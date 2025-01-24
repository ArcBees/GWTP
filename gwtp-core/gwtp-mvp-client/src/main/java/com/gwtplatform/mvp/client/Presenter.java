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
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * A singleton presenter, the basic building block of the
 * <a href="http://gwtcreate.com/videos/#gwt-dev-practices">
 * model-view-presenter</a> architecture. Each logical page of your application will usually
 * correspond to a singleton {@link Presenter}. If you need to separate logic from view
 * in a simple graphical component, you might consider using a {@link PresenterWidget}.
 * <p>
 * For more details on the hierarchical organization of presenters, see {@link PresenterWidget}.
 * <p>
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
 * <p>
 * To reveal a presenter associated to a {@link com.gwtplatform.mvp.client.proxy.ProxyPlace ProxyPlace}
 * you can simply navigate to an hyperlink corresponding to this place's name token. The
 * {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager} offers a number of method for building
 * such hyperlinks. If you want to reveal it programmatically, you should build a
 * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest PlaceRequest} and call one of the
 * following method:
 * <ul>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealPlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest)
 * PlaceManager.revealPlace(PlaceRequest)}</li>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealRelativePlace(
 * com.gwtplatform.mvp.shared.proxy.PlaceRequest) PlaceManager.revealRelativePlace(PlaceRequest)}</li>
 * <li>{@link com.gwtplatform.mvp.client.proxy.PlaceManager#revealRelativePlace(
 * com.gwtplatform.mvp.shared.proxy.PlaceRequest, int) PlaceManager.revealRelativePlace(PlaceRequest, int)}</li>
 * </ul>
 * If the presenter is associated to a regular {@link Proxy} and does not have
 * a name token then you should call the {@link #forceReveal()} method. For such
 * presenters, it is customary to define an event responsible of revealing them in
 * order to enforce loose coupling. This event is then handled by the presenter
 * using the {@link com.gwtplatform.mvp.client.annotations.ProxyEvent ProxyEvent}
 * mechanism.
 * <p>
 * If the presenter is revealed and is not currently visible, then its {@link #revealInParent()} method
 * will be called.
 * <p>
 * To hide a presenter, you can reveal another one in the same slot or you can use
 * one of the methods described in {@link PresenterWidget}.
 * <p>
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
 * <li>If a presenter already occupies this slot it is removed.
 * <ul><li>If the presenter owning the slot is currently visible then
 * {@link #onHide()} is called on the removed presenter and, recursively,
 * on its children (bottom-up: first the children, then the parent)</li>
 * <li>If the parent is not visible, it asks to be set in one of its parent slot
 * by firing a {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent}
 * too, this continues recursively until a visible presenter is reached, or until a presenter fires
 * {@link com.gwtplatform.mvp.client.proxy.RevealRootContentEvent RevealRootContentEvent}
 * or {@link com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent RevealRootLayoutContentEvent}</li>
 * </ul></li>
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
public abstract class Presenter<V extends View, Proxy_ extends Proxy<?>> extends PresenterWidget<V> {
    /**
     * The RevealType define which event will be fired in the default {@link #revealInParent()}.
     * <p>
     * Root will fire a {@link RevealRootContentEvent}.
     * RootLayout will fire a {@link RevealRootLayoutContentEvent}.
     * RootPopup will fire a {@link RevealRootPopupContentEvent}.
     */
    public enum RevealType {
        Root,
        RootLayout,
        RootPopup
    }

    /**
     * The light-weight {@link Proxy} around this presenter.
     */
    private final Proxy_ proxy;
    private RevealType revealType;
    private GwtEvent.Type<RevealContentHandler<?>> slot;

    /**
     * Creates a {@link Presenter} that is not necessarily using automatic
     * binding. Automatic binding will only work when instantiating this object using
     * Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl(boolean)} for
     * more details on automatic binding.
     *
     * @param autoBind {@code true} to request automatic binding, {@code false} otherwise.
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     * @param proxy    The {@link Proxy}.
     */
    public Presenter(boolean autoBind, EventBus eventBus, V view, Proxy_ proxy) {
        super(eventBus, view);
        this.proxy = proxy;
    }

    /**
     * Creates a {@link Presenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     * @param proxy    The {@link Proxy}.
     */
    public Presenter(EventBus eventBus, V view, Proxy_ proxy) {
        this(eventBus, view, proxy, null, null);
    }

    /**
     * Creates a {@link Presenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus   The {@link EventBus}.
     * @param view       The {@link View}.
     * @param proxy      The {@link Proxy}.
     * @param revealType The {@link RevealType}.
     */
    public Presenter(EventBus eventBus, V view, Proxy_ proxy, RevealType revealType) {
        this(eventBus, view, proxy, revealType, null);
    }

    /**
     * Creates a {@link Presenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     * @param proxy    The {@link Proxy}.
     * @param slot     The slot where to reveal this presenter see {@see com.google.gwt.event.shared.GwtEvent.Type} and
     *                 {@see RevealContentHandler}.
     */
    public Presenter(EventBus eventBus, V view, Proxy_ proxy, GwtEvent.Type<RevealContentHandler<?>> slot) {
        this(eventBus, view, proxy, null, slot);
    }

    /**
     * Creates a {@link Presenter} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus   The {@link EventBus}.
     * @param view       The {@link View}.
     * @param proxy      The {@link Proxy}.
     * @param revealType The {@link RevealType}.
     * @param slot       The slot where to reveal this presenter see {@see com.google.gwt.event.shared.GwtEvent.Type}
     *                   and {@see RevealContentHandler}.
     */
    public Presenter(EventBus eventBus, V view, Proxy_ proxy, RevealType revealType,
            GwtEvent.Type<RevealContentHandler<?>> slot) {
        super(eventBus, view);
        this.proxy = proxy;
        this.revealType = revealType;
        this.slot = slot;
    }

    @Override
    public final void bind() {
        super.bind();

        if (getProxy() instanceof HasHandlerContainer) {
            ((HasHandlerContainer) getProxy()).getHandlerContainer().bind();
        }
    }

    @Override
    public final void unbind() {
        super.unbind();

        if (getProxy() instanceof HasHandlerContainer) {
            ((HasHandlerContainer) getProxy()).getHandlerContainer().unbind();
        }
    }

    /**
     * Reveals the presenter, bypassing any service offered by the
     * {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager}.
     * Since this method bypasses the {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager}
     * it will not:
     * <ul>
     * <li>Update the browser history</li>
     * <li>Check accessibility via the {@link com.gwtplatform.mvp.client.proxy.Gatekeeper Gatekeeper}</li>
     * <li>Setup the presenter via {@link #prepareFromRequest(PlaceRequest)}</li>
     * <li>Uses the leave confirmation mechanism (see {@link com.gwtplatform.mvp.client.proxy
     * .PlaceManager#setOnLeaveConfirmation(String) PlaceManager.setOnLeaveConfirmation})</li>
     * </ul>
     * Therefore, to reveal a presenter associated to a {@link com.gwtplatform.mvp.client.proxy.ProxyPlace ProxyPlace}
     * use one of the method provided by the {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager}.
     * For more details see {@link Presenter}.
     */
    public final void forceReveal() {
        if (isVisible()) {
            return;
        }
        revealInParent();
    }

    /**
     * Returns the {@link Proxy} attached to this presenter.
     *
     * @return The {@link Proxy}.
     */
    public final Proxy_ getProxy() {
        return proxy;
    }

    /**
     * Verifies if this presenter can be revealed automatically or if it is meant to be
     * revealed manually.
     * Normally, the user wants to reveal a presenter manually when it cannot be used
     * until some data is received from the server. For example, a form
     * to edit client details is unusable until all the data for this user has been
     * received. Fetching this data should be done in the {@link #prepareFromRequest(PlaceRequest)}
     * method.
     * <p>
     * In order to use manual reveal, override this method to return {@code true}.
     * Then, in your {@link #prepareFromRequest}, you can either:
     * <ul>
     * <li> Fetch the data using a {@link com.gwtplatform.mvp.client.proxy.ManualRevealCallback ManualRevealCallback},
     * which will automatically reveal the presenter upon success.</li>
     * <li> Fetch the data by any other mean and call
     * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace#manualReveal(Presenter)} when
     * your data is available. In this case you also have to call
     * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace#manualRevealFailed()}
     * if loading fails, otherwise your application will become unusable.</li>
     * </ul>
     * The default implementation uses automatic reveal, and therefore returns {@code false}.
     *
     * @return {@code true} if you want to use manual reveal, or {@code false} to use
     *         automatic reveal.
     */
    public boolean useManualReveal() {
        return false;
    }

    /**
     * Prepare the state of the presenter given the information contained in
     * the {@link PlaceRequest}. This method is called when the
     * {@link com.gwtplatform.mvp.client.proxy.PlaceManager PlaceManager} navigates
     * to this {@link Presenter}. You should override the method to extract any
     * parameters you need from the request. Make sure you call your parent's
     * {@link #prepareFromRequest} method.
     * <p>
     * If your presenter needs to fetch some information from the server while
     * preparing itself, consider using manual reveal. See {@link #useManualReveal()}.
     * <p>
     * If your presenter does not handle any parameter and does not want to fetch
     * extra information, then there is no need to override this method.
     *
     * @param request The {@link PlaceRequest}.
     */
    public void prepareFromRequest(PlaceRequest request) {
    }

    /**
     * Returns the {@link RevealType} of this presenter.
     *
     * @return The {@link RevealType}.
     */
    protected RevealType getRevealType() {
        return revealType;
    }

    /**
     * Set the {@link RevealType} of this presenter.
     * If the parent type is not null, the slot will be ignored in {@link #revealInParent()}.
     *
     * @param revealType The {@link RevealType}.
     */
    protected void setRevealType(RevealType revealType) {
        this.revealType = revealType;
    }

    /**
     * Returns the slot where this presenter is to be revealed.
     *
     * @return The slot where to reveal this presenter see {@see com.google.gwt.event.shared.GwtEvent.Type} and {@see
     *         RevealContentHandler}.
     */
    protected GwtEvent.Type<RevealContentHandler<?>> getSlot() {
        return slot;
    }

    /**
     * Set the slot where this presenter is to be revealed.
     * The slot is ignored in {@link #revealInParent()} if the parent type is not null.
     *
     * @param slot The slot where to reveal this presenter see {@see com.google.gwt.event.shared.GwtEvent.Type} and
     *             {@see RevealContentHandler}.
     */
    protected void setSlot(GwtEvent.Type<RevealContentHandler<?>> slot) {
        this.slot = slot;
    }

    /**
     * Requests that the presenter reveal itself in its parent presenter.
     * You can override this method to either fire a
     * {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent},
     * a {@link com.gwtplatform.mvp.client.proxy.RevealRootContentEvent RevealRootContentEvent}
     * or a {@link com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent RevealRootLayoutContentEvent}.
     */
    protected void revealInParent() {
        if (revealType != null) {
            switch (revealType) {
                case Root:
                    RevealRootContentEvent.fire(this, this);
                    break;

                case RootLayout:
                    RevealRootLayoutContentEvent.fire(this, this);
                    break;

                case RootPopup:
                    RevealRootPopupContentEvent.fire(this, (PresenterWidget<PopupView>) this);
                    break;
            }
        } else {
            RevealContentEvent.fire(this, slot, this);
        }
    }
}
