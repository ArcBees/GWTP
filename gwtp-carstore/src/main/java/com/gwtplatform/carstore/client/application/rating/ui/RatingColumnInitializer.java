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

package com.gwtplatform.carstore.client.application.rating.ui;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public class RatingColumnInitializer implements ColumnInitializer<RatingDto> {
    @Override
    public void initializeColumns(CellTable<RatingDto> table) {
        initIdColumn(table);
        initCarColumn(table);
        initRatingColumn(table);
    }

    private void initIdColumn(CellTable<RatingDto> table) {
        Column<RatingDto, Number> idColumn = new Column<RatingDto, Number>(new NumberCell()) {
            @Override
            public Long getValue(RatingDto ratingDto) {
                return ratingDto.getId();
            }
        };

        table.addColumn(idColumn, "ID");
    }

    private void initCarColumn(CellTable<RatingDto> table) {
        Column<RatingDto, String> carColumn = new Column<RatingDto, String>(new TextCell()) {
            @Override
            public String getValue(RatingDto ratingDto) {
                return ratingDto.toString();
            }
        };

        table.addColumn(carColumn, "Car");
    }

    private void initRatingColumn(CellTable<RatingDto> table) {
        Column<RatingDto, Number> ratingColumn = new Column<RatingDto, Number>(new NumberCell()) {
            @Override
            public Number getValue(RatingDto ratingDto) {
                return ratingDto.getRating();
            }
        };

        table.addColumn(ratingColumn, "Rating");
    }
}
