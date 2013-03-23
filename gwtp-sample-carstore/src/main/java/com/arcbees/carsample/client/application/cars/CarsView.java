package com.arcbees.carsample.client.application.cars;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.cars.CarsPresenter.MyView;
import com.arcbees.carsample.shared.domain.Car;
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
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class CarsView extends ViewWithUiHandlers<CarsUiHandlers> implements MyView {
    public interface Binder extends UiBinder<Widget, CarsView> {
    }

    @UiField(provided = true)
    CellTable<Car> carGrid;

    @UiField(provided = true)
    SimplePager pager;

    private static final int PAGE_SIZE = 10;
    
    private AsyncDataProvider<Car> asyncDataProvider;

    @Inject
    public CarsView(Binder uiBinder) {
        initCarGrid();
        
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void initDataProvider() {
        asyncDataProvider = new AsyncDataProvider<Car>() {
            @Override
            protected void onRangeChanged(HasData<Car> display) {
                Range range = display.getVisibleRange();
                getUiHandlers().fetchData(range.getStart(), range.getLength());
            }
        };

        asyncDataProvider.addDataDisplay(carGrid);
    }

    @Override
    public HasData<Car> getCarDisplay() {
        return carGrid;
    }

    @Override
    public void setCarsCount(Integer result) {
        asyncDataProvider.updateRowCount(result, true);
    }

    @Override
    public void displayCars(int offset, List<Car> cars) {
        asyncDataProvider.updateRowData(offset, cars);
    }

    @UiHandler("create")
    @SuppressWarnings("unused")
    void onCreateClicked(ClickEvent ignored) {
        getUiHandlers().onCreate();
    }

    private void initCarGrid() {
        carGrid = new CellTable<Car>();
        carGrid.setSelectionModel(new NoSelectionModel<Car>());

        pager = new SimplePager(SimplePager.TextLocation.CENTER);
        pager.setDisplay(carGrid);
        pager.setPageSize(PAGE_SIZE);

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<Car, Number> idColumn = new Column<Car, Number>(new NumberCell()) {
            @Override
            public Integer getValue(Car car) {
                return car.getId();
            }
        };

        Column<Car, String> manufacturerColumn = new Column<Car, String>(new TextCell()) {
            @Override
            public String getValue(Car car) {
                return car.getManufacturer().getName();
            }
        };

        Column<Car, String> modelColumn = new Column<Car, String>(new TextCell()) {
            @Override
            public String getValue(Car car) {
                return car.getModel();
            }
        };

        carGrid.addColumn(idColumn, "ID");
        carGrid.addColumn(manufacturerColumn, "Manufacturer");
        carGrid.addColumn(modelColumn, "Model");
        carGrid.setColumnWidth(idColumn, 50, Unit.PX);
    }

    private void initActionColumns() {
        Cell<Car> editCell = new ActionCell<Car>("Edit", new Delegate<Car>() {
            @Override
            public void execute(Car car) {
                getUiHandlers().onEdit(car);
            }
        });

        Cell<Car> deleteCell = new ActionCell<Car>("Delete", new Delegate<Car>() {
            @Override
            public void execute(Car car) {
                Boolean confirm = Window.confirm("Are you sure you want to delete " + car.getModel() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(car);
                }
            }
        });

        IdentityColumn<Car> editColumn = new IdentityColumn<Car>(editCell);
        IdentityColumn<Car> deleteColumn = new IdentityColumn<Car>(deleteCell);

        carGrid.addColumn(editColumn, "Edit");
        carGrid.addColumn(deleteColumn, "Delete");

        carGrid.setColumnWidth(editColumn, 75, Unit.PX);
        carGrid.setColumnWidth(deleteColumn, 75, Unit.PX);
    }
}
