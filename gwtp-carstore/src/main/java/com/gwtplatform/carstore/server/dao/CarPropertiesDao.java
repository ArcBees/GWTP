package com.gwtplatform.carstore.server.dao;

import com.gwtplatform.carstore.server.dao.domain.CarProperties;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;

public class CarPropertiesDao extends BaseDao<CarProperties> {
    public CarPropertiesDao() {
        super(CarProperties.class);
    }

    public CarPropertiesDto put(CarPropertiesDto carPropertiesDto) {
        CarProperties carProperties = super.put(CarProperties.create(carPropertiesDto));
        
        return CarProperties.createDto(carProperties);
    }
}
