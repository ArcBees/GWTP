package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also
 * a {@link Place}. See {@link ProxyPlaceAbstract} for more details.
 * 
 * @param <P> Type of the associated {@link Presenter}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public class ProxyPlace<P extends Presenter> 
extends ProxyPlaceAbstract<P, Proxy<P>> {
}
