package com.philbeaudoin.gwtp.mvp.rebind;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
public @interface NameToken {
  String value();
}
