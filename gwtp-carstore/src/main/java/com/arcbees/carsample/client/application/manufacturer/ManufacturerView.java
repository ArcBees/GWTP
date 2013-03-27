package com.arcbees.carsample.client.application.manufacturer;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ManufacturerView extends ViewWithUiHandlers<ManufacturerUiHandlers> implements MyView {
    public interface Binder extends UiBinder<Widget, ManufacturerView> {
    }

    @UiField(provided = true)
    CellTable<Manufacturer> manufacturerGrid;

    private final ListDataProvider<Manufacturer> manufacturerDataProvider;

    @Inject
    public ManufacturerView(Binder uiBinder) {
        initManufacturerGrid();
        
        initWidget(uiBinder.createAndBindUi(this));

        manufacturerDataProvider = new ListDataProvider<Manufacturer>();
        manufacturerDataProvider.addDataDisplay(manufacturerGrid);
    }

    @Override
    public void displayManufacturers(List<Manufacturer> manufacturers) {
        manufacturerDataProvider.getList().clear();
        manufacturerDataProvider.getList().addAll(manufacturers);
    }

    @Override
    public void addManufacturer(Manufacturer manufacturer) {
        manufacturerDataProvider.getList().add(manufacturer);
    }

    @Override
    public void removeManufacturer(Manufacturer manufacturer) {
        manufacturerDataProvider.getList().remove(manufacturer);
    }

    @Override
    public void replaceManufacturer(Manufacturer oldManufacturer, Manufacturer newManufacturer) {
        List<Manufacturer> manufacturers = manufacturerDataProvider.getList();
        int index = manufacturers.indexOf(oldManufacturer);

        manufacturers.add(index, newManufacturer);
        manufacturers.remove(index + 1);
    }

    @UiHandler("create")
    void onCreateClicked(ClickEvent ignored) {
        getUiHandlers().onCreate();
    }

    private void initManufacturerGrid() {
        manufacturerGrid = new CellTable<Manufacturer>();
        manufacturerGrid.setSelectionModel(new NoSelectionModel<Manufacturer>());

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<Manufacturer, Number> idColumn = new Column<Manufacturer, Number>(new NumberCell()) {
            @Override
            public Long getValue(Manufacturer manufacturer) {
                return manufacturer.getId();
            }
        };

        Column<Manufacturer, String> nameColumn = new Column<Manufacturer, String>(new TextCell()) {
            @Override
            public String getValue(Manufacturer manufacturer) {
                return manufacturer.getName();
            }
        };

        manufacturerGrid.addColumn(idColumn, "ID");
        manufacturerGrid.addColumn(nameColumn, "Name");
        manufacturerGrid.setColumnWidth(idColumn, 50, Unit.PX);
    }

    private void initActionColumns() {
        Cell<Manufacturer> editCell = new ActionCell<Manufacturer>("Edit", new Delegate<Manufacturer>() {
            @Override
            public void execute(Manufacturer manufacturer) {
                getUiHandlers().onEdit(manufacturer);
            }
        });

        Cell<Manufacturer> deleteCell = new ActionCell<Manufacturer>("Delete", new Delegate<Manufacturer>() {
            @Override
            public void execute(Manufacturer manufacturer) {
                Boolean confirm = Window.confirm("Are you sure you want to delete " + manufacturer.getName() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(manufacturer);
                }
            }
        });

        IdentityColumn<Manufacturer> editColumn = new IdentityColumn<Manufacturer>(editCell);
        IdentityColumn<Manufacturer> deleteColumn = new IdentityColumn<Manufacturer>(deleteCell);

        manufacturerGrid.addColumn(editColumn, "Edit");
        manufacturerGrid.addColumn(deleteColumn, "Delete");

        manufacturerGrid.setColumnWidth(editColumn, 75, Unit.PX);
        manufacturerGrid.setColumnWidth(deleteColumn, 75, Unit.PX);
    }
}
