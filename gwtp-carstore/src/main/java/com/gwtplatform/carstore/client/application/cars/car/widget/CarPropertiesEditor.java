package com.gwtplatform.carstore.client.application.cars.car.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;

public class CarPropertiesEditor extends Composite implements Editor<CarPropertiesDto> {
    public interface Binder extends UiBinder<Widget, CarPropertiesEditor> {
    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    TextBox someString;
    @UiField
    IntegerBox someNumber;
    @UiField
    DateBox someDate;

    public CarPropertiesEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        someString.getElement().setAttribute("placeholder", "Property #1");
        someNumber.getElement().setAttribute("placeholder", "Property #2");
        someDate.getElement().setAttribute("placeholder", "Property #3");
    }
}
