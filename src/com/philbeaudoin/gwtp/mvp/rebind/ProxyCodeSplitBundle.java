package com.philbeaudoin.gwtp.mvp.rebind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.philbeaudoin.gwtp.mvp.client.ProviderBundle;

@Target(ElementType.TYPE)
public @interface ProxyCodeSplitBundle {
  Class<? extends ProviderBundle> bundleClass();
  int id();
}
