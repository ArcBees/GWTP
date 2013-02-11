package com.gwtplatform.dispatch.rebind.velocity;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

public interface GeneratorFactory {
    RestActionGenerator create(JMethod actionMethod);

    SerializerGenerator create(JClassType type);
}
