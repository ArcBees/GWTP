package com.gwtplatform.carstore.server.dao;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.gwtplatform.carstore.shared.domain.Rating;

public class RatingDao extends BaseDao<Rating> {
    public RatingDao(Provider<EntityManager> entityManagerProvider) {
        super(Rating.class);
    }
}
