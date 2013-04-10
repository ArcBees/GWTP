package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveManufacturerAction;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveManufacturerHandler extends AbstractActionHandler<SaveManufacturerAction, GetResult<ManufacturerDto>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public SaveManufacturerHandler(final ManufacturerDao manufacturerDao) {
        super(SaveManufacturerAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResult<ManufacturerDto> execute(SaveManufacturerAction action, ExecutionContext context)
            throws ActionException {
        ManufacturerDto manufacturerDto = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(action.getManufacturer())));

        return new GetResult<ManufacturerDto>(manufacturerDto);
    }
}
