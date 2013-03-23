package com.arcbees.carsample.server.dispatch;

import javax.inject.Inject;

import com.arcbees.carsample.server.dao.ManufacturerDao;
import com.arcbees.carsample.shared.dispatch.GetManufacturerAction;
import com.arcbees.carsample.shared.dispatch.GetResult;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetManufacturerHandler extends AbstractActionHandler<GetManufacturerAction, GetResult<Manufacturer>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public GetManufacturerHandler(final ManufacturerDao manufacturerDao) {
        super(GetManufacturerAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResult<Manufacturer> execute(GetManufacturerAction action, ExecutionContext context)
            throws ActionException {
        return new GetResult<Manufacturer>(manufacturerDao.find(action.getId()));
    }
}
