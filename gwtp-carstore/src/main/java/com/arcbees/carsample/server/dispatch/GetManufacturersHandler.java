package com.arcbees.carsample.server.dispatch;

import javax.inject.Inject;

import com.arcbees.carsample.server.dao.ManufacturerDao;
import com.arcbees.carsample.shared.dispatch.GetManufacturersAction;
import com.arcbees.carsample.shared.dispatch.GetResults;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetManufacturersHandler extends AbstractActionHandler<GetManufacturersAction, GetResults<Manufacturer>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public GetManufacturersHandler(final ManufacturerDao manufacturerDao) {
        super(GetManufacturersAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResults<Manufacturer> execute(GetManufacturersAction action, ExecutionContext context)
            throws ActionException {
        return new GetResults<Manufacturer>(manufacturerDao.getAll());
    }
}
