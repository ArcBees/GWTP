package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.inject.client.Ginjector;
import com.philbeaudoin.gwtp.mvp.client.TabContainerPresenter;

/**
 * Annotation used to specify the priority and name to display
 * on a tab. Specify either {@link #label()} or {@link #getLabel}.
 * The latter let you specify the code to call to obtain the name
 * of the tab. Make sure class names are fully qualified. Also, you have access
 * to the variable {@code ginjector} (your specific {@link Ginjector}-derived
 * class). For example:
 * <pre>
 * {@code @}TabInfo( 
 *    priority = 0,
 *    label = "Default" )
 *    
 * {@code @}TabInfo( 
 *    priority = 12,
 *    getLabel = "ginjector.getTranslations().tabDetailsLabel()" )
 * </pre>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface TabInfo {
  Class<? extends TabContainerPresenter> container();
  int priority();
  String label() default "";
  String getLabel() default "";
}
