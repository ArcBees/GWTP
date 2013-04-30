package com.gwtplatform.carstore.client.application.report;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.mvp.client.ViewImpl;

public class ReportView extends ViewImpl implements ReportPresenter.MyView {
    interface Binder extends UiBinder<Widget, ReportView> {
    }

    @UiField(provided = true)
    CellTable<ManufacturerRatingDto> reportGrid;

    private final ListDataProvider<ManufacturerRatingDto> ratingsProvider;

    @Inject
    ReportView(Binder uiBinder) {
        ratingsProvider = new ListDataProvider<ManufacturerRatingDto>();

        initCarGrid();

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void displayReport(List<ManufacturerRatingDto> manufacturerRatings) {
        ratingsProvider.getList().clear();
        ratingsProvider.getList().addAll(manufacturerRatings);
    }

    private void initCarGrid() {
        reportGrid = new CellTable<ManufacturerRatingDto>();
        reportGrid.setSelectionModel(new NoSelectionModel<ManufacturerRatingDto>());

        ratingsProvider.addDataDisplay(reportGrid);

        initDataColumns();
    }

    private void initDataColumns() {
        Column<ManufacturerRatingDto, String> manufacturerColumn =
                new Column<ManufacturerRatingDto, String>(new TextCell()) {
                    @Override
                    public String getValue(ManufacturerRatingDto manufacturerRating) {
                        return manufacturerRating.getManufacturer();
                    }
                };

        Column<ManufacturerRatingDto, Number> ratingColumn =
                new Column<ManufacturerRatingDto, Number>(new NumberCell()) {
                    @Override
                    public Double getValue(ManufacturerRatingDto manufacturerRating) {
                        return manufacturerRating.getRating();
                    }
                };

        reportGrid.addColumn(manufacturerColumn, "Manufacturer");
        reportGrid.addColumn(ratingColumn, "Rating");
        reportGrid.setColumnWidth(ratingColumn, 50, Unit.PX);
    }
}
