package com.philbeaudoin.gwtp.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceFactory;

/**
 * This annotation lets you define a a {@link PlaceFactory} to use
 * to create the {@link Place} associated with your proxy.
 * 
 * @author Olivier Monaco
 */
@Target(ElementType.TYPE)
public @interface PlaceProvider {
  Class<? extends PlaceFactory> value();
}
