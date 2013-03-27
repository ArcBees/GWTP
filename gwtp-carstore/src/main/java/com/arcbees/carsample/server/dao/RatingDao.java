package com.arcbees.carsample.server.dao;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.arcbees.carsample.shared.domain.Rating;

public class RatingDao extends BaseDao<Rating> {
    public RatingDao(Provider<EntityManager> entityManagerProvider) {
        super(Rating.class);
    }
}
