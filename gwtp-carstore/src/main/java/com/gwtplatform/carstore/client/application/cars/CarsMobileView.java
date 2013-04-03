package com.gwtplatform.carstore.client.application.cars;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter.MyView;
import com.gwtplatform.carstore.client.application.cars.renderer.CarCell;
import com.gwtplatform.carstore.client.application.ui.ShowMorePagerPanel;
import com.gwtplatform.carstore.client.resources.MobileDataListStyle;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class CarsMobileView extends ViewWithUiHandlers<CarsUiHandlers> implements MyView {
    public interface Binder extends UiBinder<Widget, CarsMobileView> {
    }

    private static final int PAGE_SIZE = 20;

    @UiField(provided = true)
    ShowMorePagerPanel pagerPanel;

    private CellList<CarDto> carList;
    private AsyncDataProvider<CarDto> asyncDataProvider;
    private SingleSelectionModel<CarDto> selectionModel;

    @Inject
    public CarsMobileView(Binder uiBinder, CarCell carCell, MobileDataListStyle mobileDataListStyle) {
        pagerPanel = new ShowMorePagerPanel(PAGE_SIZE);
        carList = new CellList<CarDto>(carCell, mobileDataListStyle);
        selectionModel = new SingleSelectionModel<CarDto>();

        initWidget(uiBinder.createAndBindUi(this));

        pagerPanel.setDisplay(carList);
        carList.setSelectionModel(selectionModel);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                getUiHandlers().onEdit(selectionModel.getSelectedObject());
            }
        });
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

        asyncDataProvider.addDataDisplay(carList);
    }

    @Override
    public HasData<CarDto> getCarDisplay() {
        return carList;
    }

    @Override
    public void setCarsCount(Integer result) {
        asyncDataProvider.updateRowCount(result, true);
    }

    @Override
    public void displayCars(int offset, List<CarDto> carDtos) {
        asyncDataProvider.updateRowData(offset, carDtos);
    }
}
