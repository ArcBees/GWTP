package com.philbeaudoin.gwtp.mvp.client;

import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.mvp.client.proxy.Place;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceManager;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceRequest;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;

/**
 * A singleton presenter, one of the basic building block of
 * the <a href="http://code.google.com/intl/nl/events/io/2009/sessions/GoogleWebToolkitBestPractices.html">
 * model-view-presenter</a> architecture. Each page in your
 * application should correspond to a singleton {@link Presenter}
 * and it should have an accompanying singleton {@link View} and
 * {@link Proxy}.
 * 
 * @author Philippe Beaudoin
 */
@Singleton
public interface Presenter extends PresenterWidget {

  /**
   * Requests the presenter to reveal itself on screen. This call will
   * fail on presenters for which the {@link Proxy} is not a {@link Place},
   * since such presenters are not expected to be revealable.
   * Nothing happens if the presenter is currently visible (see 
   * {@link #isVisible()}). Upon being revealed, the presenter will ask to 
   * be inserted within its parent presenter by firing a {@link 
   * RevealContentEvent}. This will cause the parent to be revealed 
   * too, if necessary.
   */
  public void reveal();

  /**
   * <b>Important:</b> If you want to reveal a presenter from within
   * your application, you should call {@link #reveal()}
   * instead. This way you can make sure you don't inadvertently reveal a 
   * non-leaf Presenter. Also, you will benefit from the change confirmation
   * mechanism. (See {@link PlaceManager#setOnLeaveConfirmation(String)}).
   * <p />
   * Forces the presenter to reveal itself on screen.
   * 
   * @see #reveal()
   */
  public void forceReveal();

  /**
   * This method is called when a {@link Presenter} should prepare itself
   * based on a {@link PlaceRequest}. The presenter should extract
   * any parameters it needs from the request. A presenter should override
   * this method if it handles custom parameters, but it should call
   * the parent's {@code prepareFromRequest} method.
   *
   * @param request   The request.
   */
  public void prepareFromRequest( PlaceRequest request );
  
  /**
   * Returns the {@link Proxy} for the current presenter.
   *
   * @return The proxy.
   */
  public Proxy<?> getProxy();

  /**
   * This method is called when creating a {@link PlaceRequest} for this
   * {@link Presenter}. The presenter should add all the required parameters to the 
   * request.
   * <p/>
   * <p/>
   * If nothing is to be done, simply return the {@code request}
   * unchanged. Otherwise, call {@link PlaceRequest#with(String, String)} to
   * add parameters. Eg:
   * <p/>
   * <pre>
   * return request.with( &quot;id&quot;, getId() );
   * </pre>
   * <p/>
   * A presenter should override this method if it handles custom parameters, but
   * it should call the parent's {@code prepareRequest} method.
   * 
   * @param request   The current request.
   * @return The prepared place request.
   */
  public PlaceRequest prepareRequest( PlaceRequest request );

}
