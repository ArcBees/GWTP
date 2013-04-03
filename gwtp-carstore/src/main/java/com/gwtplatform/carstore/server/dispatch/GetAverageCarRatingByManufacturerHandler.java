package com.gwtplatform.carstore.server.dispatch;

import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.service.ReportService;
import com.gwtplatform.carstore.shared.dispatch.GetAverageCarRatingByManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * A simple report that lists average car rating by manufacturer.
 */
public class GetAverageCarRatingByManufacturerHandler
        extends AbstractActionHandler<GetAverageCarRatingByManufacturerAction, GetResults<ManufacturerRatingDto>> {

    private final ReportService reportService;

    @Inject
    public GetAverageCarRatingByManufacturerHandler(final ReportService reportService) {
        super(GetAverageCarRatingByManufacturerAction.class);
        
        this.reportService = reportService;
    }

    @Override
    public GetResults<ManufacturerRatingDto> execute(GetAverageCarRatingByManufacturerAction action,
            ExecutionContext context)
            throws ActionException {
        List<ManufacturerRatingDto> results = this.reportService.getAverageCarRatingByManufacturer();
        
        return new GetResults<ManufacturerRatingDto>(results);
    }

}
