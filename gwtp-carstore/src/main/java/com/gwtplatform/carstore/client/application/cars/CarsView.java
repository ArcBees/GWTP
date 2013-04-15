package com.gwtplatform.carstore.client.application.cars;

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
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.Range;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter.MyView;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class CarsView extends ViewWithUiHandlers<CarsUiHandlers> implements MyView {
    public interface Binder extends UiBinder<Widget, CarsView> {
    }

    @UiField(provided = true)
    CellTable<CarDto> carGrid;

    @UiField(provided = true)
    SimplePager pager;

    private static final int PAGE_SIZE = 10;
    
    private AsyncDataProvider<CarDto> asyncDataProvider;

    @Inject
    public CarsView(Binder uiBinder) {
        initCarGrid();
        
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void initDataProvider() {
        asyncDataProvider = new AsyncDataProvider<CarDto>() {
            @Override
            protected void onRangeChanged(HasData<CarDto> display) {
                Range range = display.getVisibleRange();
                getUiHandlers().fetchData(range.getStart(), range.getLength());
            }
        };

        asyncDataProvider.addDataDisplay(carGrid);
    }

    @Override
    public HasData<CarDto> getCarDisplay() {
        return carGrid;
    }

    @Override
    public void setCarsCount(Integer result) {
        asyncDataProvider.updateRowCount(result, true);
    }

    @Override
    public void displayCars(int offset, List<CarDto> carDtos) {
        asyncDataProvider.updateRowData(offset, carDtos);
    }

    @UiHandler("create")
    @SuppressWarnings("unused")
    void onCreateClicked(ClickEvent ignored) {
        getUiHandlers().onCreate();
    }

    private void initCarGrid() {
        carGrid = new CellTable<CarDto>();
        carGrid.setSelectionModel(new NoSelectionModel<CarDto>());

        pager = new SimplePager(SimplePager.TextLocation.CENTER);
        pager.setDisplay(carGrid);
        pager.setPageSize(PAGE_SIZE);

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<CarDto, Number> idColumn = new Column<CarDto, Number>(new NumberCell()) {
            @Override
            public Long getValue(CarDto carDto) {
                return carDto.getId();
            }
        };

        Column<CarDto, String> manufacturerColumn = new Column<CarDto, String>(new TextCell()) {
            @Override
            public String getValue(CarDto carDto) {
                return carDto.getManufacturer().getName();
            }
        };

        Column<CarDto, String> modelColumn = new Column<CarDto, String>(new TextCell()) {
            @Override
            public String getValue(CarDto carDto) {
                return carDto.getModel();
            }
        };

        carGrid.addColumn(idColumn, "ID");
        carGrid.addColumn(manufacturerColumn, "Manufacturer");
        carGrid.addColumn(modelColumn, "Model");
        carGrid.setColumnWidth(idColumn, 50, Unit.PX);
    }

    private void initActionColumns() {
        Cell<CarDto> editCell = new ActionCell<CarDto>("Edit", new Delegate<CarDto>() {
            @Override
            public void execute(CarDto carDto) {
                getUiHandlers().onEdit(carDto);
            }
        });

        Cell<CarDto> deleteCell = new ActionCell<CarDto>("Delete", new Delegate<CarDto>() {
            @Override
            public void execute(CarDto carDto) {
                Boolean confirm = Window.confirm("Are you sure you want to delete " + carDto.getModel() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(carDto);
                }
            }
        });

        IdentityColumn<CarDto> editColumn = new IdentityColumn<CarDto>(editCell);
        IdentityColumn<CarDto> deleteColumn = new IdentityColumn<CarDto>(deleteCell);

        carGrid.addColumn(editColumn, "Edit");
        carGrid.addColumn(deleteColumn, "Delete");

        carGrid.setColumnWidth(editColumn, 75, Unit.PX);
        carGrid.setColumnWidth(deleteColumn, 75, Unit.PX);
    }
}
