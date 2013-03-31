package com.gwtplatform.carstore.server.dao;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class ManufacturerDao extends BaseDao<Manufacturer> {
    public ManufacturerDao(Provider<EntityManager> entityManagerProvider) {
        super(Manufacturer.class);
    }
}
