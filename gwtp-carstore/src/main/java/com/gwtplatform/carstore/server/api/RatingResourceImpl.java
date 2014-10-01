/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.server.api;

import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.shared.api.RatingResource;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public class RatingResourceImpl implements RatingResource {
    private final RatingDao ratingDao;

    @Inject
    RatingResourceImpl(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    @Override
    public List<RatingDto> getRatings() {
        return Rating.createDto(ratingDao.getAll());
    }

    @Override
    public RatingDto get(Long id) {
        return Rating.createDto(ratingDao.get(id));
    }

    @Override
    public RatingDto saveOrCreate(RatingDto ratingDto) {
        Rating rating = ratingDao.put(Rating.create(ratingDto));

        return Rating.createDto(rating);
    }

    @Override
    public void delete(Long id) {
        ratingDao.delete(id);
    }
}
