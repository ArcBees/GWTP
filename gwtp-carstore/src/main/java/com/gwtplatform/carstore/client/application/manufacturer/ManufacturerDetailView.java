package com.gwtplatform.carstore.client.application.manufacturer;

import javax.inject.Inject;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ManufacturerDetailView extends ViewWithUiHandlers<ManufacturerDetailUiHandlers>
        implements ManufacturerDetailPresenter.MyView, Editor<ManufacturerDto> {
    public interface Binder extends UiBinder<Widget, ManufacturerDetailView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<ManufacturerDto, ManufacturerDetailView> {
    }

    @UiField
    TextBox name;

    private final Driver driver;

    @Inject
    public ManufacturerDetailView(Binder uiBinder, Driver driver) {
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        
        driver.initialize(this);

        name.getElement().setAttribute("placeholder", "Manufacturer name");
    }

    @Override
    public void edit(ManufacturerDto manufacturerDto) {
        name.setFocus(true);
        driver.edit(manufacturerDto);
    }

    @Override
    public void getManufacturer() {
        getUiHandlers().onSave(driver.flush());
    }
}

