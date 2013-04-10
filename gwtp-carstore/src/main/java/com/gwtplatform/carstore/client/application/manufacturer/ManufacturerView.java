package com.gwtplatform.carstore.client.application.manufacturer;

import java.util.List;

import javax.inject.Inject;

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
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ManufacturerView extends ViewWithUiHandlers<ManufacturerUiHandlers> implements MyView {
    public interface Binder extends UiBinder<Widget, ManufacturerView> {
    }

    @UiField(provided = true)
    CellTable<ManufacturerDto> manufacturerGrid;

    private final ListDataProvider<ManufacturerDto> manufacturerDataProvider;

    @Inject
    public ManufacturerView(Binder uiBinder) {
        initManufacturerGrid();
        
        initWidget(uiBinder.createAndBindUi(this));

        manufacturerDataProvider = new ListDataProvider<ManufacturerDto>();
        manufacturerDataProvider.addDataDisplay(manufacturerGrid);
    }

    @Override
    public void displayManufacturers(List<ManufacturerDto> manufacturerDtos) {
        manufacturerDataProvider.getList().clear();
        manufacturerDataProvider.getList().addAll(manufacturerDtos);
    }

    @Override
    public void addManufacturer(ManufacturerDto manufacturerDto) {
        manufacturerDataProvider.getList().add(manufacturerDto);
    }

    @Override
    public void removeManufacturer(ManufacturerDto manufacturerDto) {
        manufacturerDataProvider.getList().remove(manufacturerDto);
    }

    @Override
    public void replaceManufacturer(ManufacturerDto oldManufacturer, ManufacturerDto newManufacturer) {
        List<ManufacturerDto> manufacturerDtos = manufacturerDataProvider.getList();
        int index = manufacturerDtos.indexOf(oldManufacturer);

        manufacturerDtos.add(index, newManufacturer);
        manufacturerDtos.remove(index + 1);
    }

    @UiHandler("create")
    void onCreateClicked(ClickEvent ignored) {
        getUiHandlers().onCreate();
    }

    private void initManufacturerGrid() {
        manufacturerGrid = new CellTable<ManufacturerDto>();
        manufacturerGrid.setSelectionModel(new NoSelectionModel<ManufacturerDto>());

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<ManufacturerDto, Number> idColumn = new Column<ManufacturerDto, Number>(new NumberCell()) {
            @Override
            public Long getValue(ManufacturerDto manufacturerDto) {
                return manufacturerDto.getId();
            }
        };

        Column<ManufacturerDto, String> nameColumn = new Column<ManufacturerDto, String>(new TextCell()) {
            @Override
            public String getValue(ManufacturerDto manufacturerDto) {
                return manufacturerDto.getName();
            }
        };

        manufacturerGrid.addColumn(idColumn, "ID");
        manufacturerGrid.addColumn(nameColumn, "Name");
        manufacturerGrid.setColumnWidth(idColumn, 50, Unit.PX);
    }

    private void initActionColumns() {
        Cell<ManufacturerDto> editCell = new ActionCell<ManufacturerDto>("Edit", new Delegate<ManufacturerDto>() {
            @Override
            public void execute(ManufacturerDto manufacturerDto) {
                getUiHandlers().onEdit(manufacturerDto);
            }
        });

        Cell<ManufacturerDto> deleteCell = new ActionCell<ManufacturerDto>("Delete", new Delegate<ManufacturerDto>() {
            @Override
            public void execute(ManufacturerDto manufacturerDto) {
                Boolean confirm = Window.confirm("Are you sure you want to delete " + manufacturerDto.getName() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(manufacturerDto);
                }
            }
        });

        IdentityColumn<ManufacturerDto> editColumn = new IdentityColumn<ManufacturerDto>(editCell);
        IdentityColumn<ManufacturerDto> deleteColumn = new IdentityColumn<ManufacturerDto>(deleteCell);

        manufacturerGrid.addColumn(editColumn, "Edit");
        manufacturerGrid.addColumn(deleteColumn, "Delete");

        manufacturerGrid.setColumnWidth(editColumn, 75, Unit.PX);
        manufacturerGrid.setColumnWidth(deleteColumn, 75, Unit.PX);
    }
}
