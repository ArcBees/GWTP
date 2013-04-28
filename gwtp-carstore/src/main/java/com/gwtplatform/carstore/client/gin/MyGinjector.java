package com.gwtplatform.carstore.client.gin;

import com.google.gwt.inject.client.Ginjector;
import com.gwtplatform.carstore.client.application.cars.car.widget.CarPropertiesEditor;

/**
 * Methods defined here will be added to the generated Ginjector. While using GinUiBinder, you must define each
 * classes that is used inside a .ui.xml that use injection.
 */
public interface MyGinjector extends Ginjector {
    CarPropertiesEditor getPropertiesEditor();
}
