package com.gwtplatform.carstore.client.application.manufacturer.ui;

import javax.inject.Inject;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter.MyView;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class EditManufacturerView extends PopupViewWithUiHandlers<EditManufacturerUiHandlers> implements MyView,
        Editor<ManufacturerDto> {
    interface Binder extends UiBinder<Widget, EditManufacturerView> {
    }

    interface Driver extends SimpleBeanEditorDriver<ManufacturerDto, EditManufacturerView> {
    }

    @UiField
    TextBox name;

    private final Driver driver;

    @Inject
    EditManufacturerView(Binder uiBinder,
                         Driver driver,
                         EventBus eventBus) {
        super(eventBus);

        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));

        driver.initialize(this);
    }

    @Override
    public void edit(ManufacturerDto manufacturerDto) {
        driver.edit(manufacturerDto);
    }

    @UiHandler("save")
    void onSaveClicked(ClickEvent ignored) {
        getUiHandlers().onSave(driver.flush());
    }

    @UiHandler("close")
    void onCloseClicked(ClickEvent ignored) {
        getUiHandlers().onCancel();
    }
}
