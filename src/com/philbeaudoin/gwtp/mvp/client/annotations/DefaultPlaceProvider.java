package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation in your custom ginjector to annotate
 * a method returning a {@link com.philbeaudoin.gwtp.mvp.client.proxy.PlaceFactory}-derived
 * class. This class will be used to provide places for proxys that are not
 * annotated with the {@link PlaceProvider} annotation.
 * 
 * @author Philippe Beaudoin
 */
@Target(ElementType.METHOD)
public @interface DefaultPlaceProvider {
}
