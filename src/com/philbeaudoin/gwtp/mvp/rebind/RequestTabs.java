package com.philbeaudoin.gwtp.mvp.rebind;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import com.philbeaudoin.gwtp.mvp.client.TabContainerPresenter;

/**
 * Use this annotation in classes implementing {@link TabContainerPresenter}.
 * This annotates a static field containing the type of the event fired 
 * when the tab container wants to discover its contained tabs. 
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.FIELD)
public @interface RequestTabs {
}
