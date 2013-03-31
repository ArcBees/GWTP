package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveManufacturerAction;
import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveManufacturerHandler extends AbstractActionHandler<SaveManufacturerAction, GetResult<Manufacturer>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public SaveManufacturerHandler(final ManufacturerDao manufacturerDao) {
        super(SaveManufacturerAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResult<Manufacturer> execute(SaveManufacturerAction action, ExecutionContext context)
            throws ActionException {
        Manufacturer manufacturer = manufacturerDao.put(action.getManufacturer());

        return new GetResult<Manufacturer>(manufacturer);
    }
}
