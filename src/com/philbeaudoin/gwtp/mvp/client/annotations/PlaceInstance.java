package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.inject.client.Ginjector;

/**
 * Annotation used to specify the code to call when creating a 
 * new place. Usually a simple call to {@code new}. Make sure
 * class names are fully qualified. Also, you have access
 * to the variable {@code nameToken} (a String) and 
 * {@code ginjector} (your specific {@link Ginjector}-derived
 * class). For example:
 * <pre>
 * {@code @}NewPlaceCode( "new com.project.client.AdminSecurePlace(nameToken, ginjector.getCurrentUser())" )
 * </pre>
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.TYPE)
public @interface PlaceInstance {
  String value();
}
