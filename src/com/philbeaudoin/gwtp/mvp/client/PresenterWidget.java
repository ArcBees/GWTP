package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.Widget;
import com.philbeaudoin.gwtp.mvp.client.proxy.Place;
import com.philbeaudoin.gwtp.mvp.client.proxy.ResetPresentersEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;

/**
 * A presenter that does not have to be a singleton. Single pages
 * from your application will usually implement the singleton
 * {@link Presenter} interface. Use the {@link PresenterWidget}
 * interface for complex widget that need to interact with 
 * model objects, but that can be instantiated in many different
 * places in your application.
 * <p />
 * {@link PresenterWidget}s and {@link Presenter}s are organized in a
 * hierarchy. Parent presenters have links to their child presenters.
 * To reveal a presenter, you call its {@link Presenter#reveal()} method,
 * or you invoke its corresponding {@link Place}. The presenter's 
 * {@link Proxy} then asks the presenter to reveal itself by calling
 * {@link Presenter#forceReveal()}, triggering the following sequence 
 * of operations:
 * <ul>
 * <li> The presenter that wants to reveal itself asks to be set in one of its 
 * parent slot by firing a {@link RevealContentEvent} ;
 * <li> If a presenter already occupies this slot it is removed. If the parent is 
 * currently visible, then {@link onHide()} is called on the removed presenter and, 
 * recursively, on its children ;
 * <li> If the parent is not visible, it asks to be set in one of its parent slot
 * by firing a {@link RevealContentEvent} too, this continue recursively until
 * the top-level or until a visible presenter is reached ;
 * <li> When a visible presenter is reached, it calls {@link #onReveal()} on 
 * the presenter it just added to a slot and, recursively, on that presenter's children;</li>
 * <li> Finally {@link #onReset()} is called on all the currently visible presenters,</li>
 *     starting from the top-level presenter and going down to the leaf presenters.</li>
 * </ul>
 * 
 * @author Philippe Beaudoin
 */
public interface PresenterWidget extends HandlerContainer {

  /**
   * Returns the {@link View} for the current presenter.
   *
   * @return The view.
   */
  public View getView();

  /**
   * Verifies if the presenter is currently visible on the screen. A
   * presenter should be visible if it successfully revealed itself
   * and was not hidden later.
   * 
   * @return {@code true} if the presenter is visible, {@code false} otherwise.
   */
  public boolean isVisible();

  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * 
   * @param slot An opaque object identifying
   *             which slot this content is being set into. The attached view should know
   *             what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
   */
  void setContent(Object slot, PresenterWidget content);

  /**
   * This method adds some content in a specific slot of the {@link Presenter}.
   * 
   * @param slot An opaque object identifying
   *             which slot this content is being added into. The attached view should know
   *             what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null} will not do anything.
   */
  void addContent(Object slot, PresenterWidget content);

  /**
   * This method clears the content in a specific slot.
   * 
   * @param slot An opaque object of type identifying
   *             which slot to clear. The attached view should know
   *             what to do with this slot.
   */
  void clearContent(Object slot);

  /**
   * Makes it possible to access the {@link Widget} object associated with that presenter.
   * 
   * @return The Widget associated with that presenter.
   */
  public Widget getWidget();

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReveal()}
   * if you override.
   * <p />
   * This method will be called whenever the presenter is revealed. Override
   * it to perform any action (such as refreshing content) that needs
   * to be done when the presenter is revealed.
   */
  void onReveal();

  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onHide()}
   * if you override.
   * <p />
   * Override this method to perform any clean-up operations. For example,
   * objects created directly or indirectly during the call to
   * {@link #onReveal()} should be disposed of in this methods.
   */
  void onHide();
   
  /**
   * You shouldn't call this manually. Instead, consider firing a 
   * {@link ResetPresentersEvent}.
   */
  void reset();
  
  /**
   * <b>Important:</b> Make sure you call your superclass {@link #onReset()}
   * if you override.
   * <p />
   * This method is called whenever a new presenter is requested, even if 
   * the presenter was already visible. It is called on every visible presenter,
   * starting from the top-level presenter and going to the leaves.
   */
  void onReset();
}