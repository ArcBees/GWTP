package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturersAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetManufacturersHandler extends AbstractActionHandler<GetManufacturersAction, GetResults<ManufacturerDto>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public GetManufacturersHandler(final ManufacturerDao manufacturerDao) {
        super(GetManufacturersAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResults<ManufacturerDto> execute(GetManufacturersAction action, ExecutionContext context)
            throws ActionException {
        return new GetResults<ManufacturerDto>(Manufacturer.createDto(manufacturerDao.getAll()));
    }
}
