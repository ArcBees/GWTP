package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetManufacturerHandler extends AbstractActionHandler<GetManufacturerAction, GetResult<ManufacturerDto>> {
    private final ManufacturerDao manufacturerDao;

    @Inject
    public GetManufacturerHandler(final ManufacturerDao manufacturerDao) {
        super(GetManufacturerAction.class);

        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public GetResult<ManufacturerDto> execute(GetManufacturerAction action, ExecutionContext context)
            throws ActionException {
        ManufacturerDto manufacturerDto = Manufacturer.createDto(manufacturerDao.get(action.getId()));
        
        return new GetResult<ManufacturerDto>(manufacturerDto);
    }
}
