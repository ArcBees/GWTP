package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.user.client.ui.Widget;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.View;

/**
 * Use this annotation in classes implementing {@link Presenter} and
 * that have slots to display child presenters.
 * This annotates every static field containing a type of event that
 * is monitored by this presenter. When handling this event, a child
 * presenter is inserted in the presenter's view. You should make
 * sure the view handles event of this type in its 
 * {@link View#setContent(Object, Widget)} method. 
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.FIELD)
public @interface ContentSlot {
}
