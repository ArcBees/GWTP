package com.gwtplatform.carstore.client.application.report;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.gwtplatform.carstore.client.application.report.renderer.ReportCell;
import com.gwtplatform.carstore.client.resources.MobileDataListStyle;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.mvp.client.ViewImpl;

public class ReportMobileView extends ViewImpl implements ReportPresenter.MyView {

    public interface Binder extends UiBinder<Widget, ReportMobileView> {
    }

    @UiField(provided = true)
    CellList<ManufacturerRatingDto> reportList;

    private final ListDataProvider<ManufacturerRatingDto> ratingsProvider;

    @Inject
    public ReportMobileView(final Binder uiBinder, final ReportCell reportCell, final MobileDataListStyle listStyle) {
        reportList = new CellList<ManufacturerRatingDto>(reportCell, listStyle);

        initWidget(uiBinder.createAndBindUi(this));

        ratingsProvider = new ListDataProvider<ManufacturerRatingDto>();
        ratingsProvider.addDataDisplay(reportList);
    }

    @Override
    public void displayReport(List<ManufacturerRatingDto> manufacturerRatings) {
        ratingsProvider.getList().clear();
        ratingsProvider.getList().addAll(manufacturerRatings);
    }
}
