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

package com.gwtplatform.carstore.client.application.rating;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.gwtplatform.carstore.client.application.rating.ui.RatingColumnInitializer;
import com.gwtplatform.carstore.client.application.rating.ui.RatingColumnsDefinition;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingView extends ViewWithUiHandlers<RatingUiHandlers> implements RatingPresenter.MyView {
    public interface Binder extends UiBinder<Widget, RatingView> {
    }

    @UiField
    Button create;
    @UiField(provided = true)
    CellTable<RatingDto> ratingGrid;

    private final RatingColumnsDefinition ratingColumnInitializer;
    private final ListDataProvider<RatingDto> ratingDataProvider;

    @Inject
    RatingView(Binder uiBinder,
               RatingColumnsDefinition ratingColumnInitializer) {
        this.ratingColumnInitializer = ratingColumnInitializer;

        initRatingGrid();
        
        initWidget(uiBinder.createAndBindUi(this));

        ratingDataProvider = new ListDataProvider<RatingDto>();
        ratingDataProvider.addDataDisplay(ratingGrid);
    }

    @Override
    public void displayRatings(List<RatingDto> ratingDtos) {
        ratingDataProvider.getList().clear();
        ratingDataProvider.getList().addAll(ratingDtos);
    }

    @Override
    public void addRating(RatingDto ratingDto) {
        ratingDataProvider.getList().add(ratingDto);
    }

    @Override
    public void removeRating(RatingDto ratingDto) {
        ratingDataProvider.getList().remove(ratingDto);
    }

    @UiHandler("create")
    void onCreateClicked(ClickEvent event) {
        getUiHandlers().onCreate();
    }

    private void initRatingGrid() {
        ratingGrid = new CellTable<RatingDto>();
        ratingGrid.setSelectionModel(new NoSelectionModel<RatingDto>());

        ratingColumnInitializer.initializeColumns(ratingGrid);
        initActionColumns();
    }

    private void initActionColumns() {
        Cell<RatingDto> deleteCell = new ActionCell<RatingDto>("Delete", new ActionCell.Delegate<RatingDto>() {
            @Override
            public void execute(RatingDto ratingDto) {
                Boolean confirm = Window.confirm("Are you sure you want to delete" + ratingDto.toString() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(ratingDto);
                }
            }
        });

        IdentityColumn<RatingDto> deleteColumn = new IdentityColumn<RatingDto>(deleteCell);
        ratingGrid.addColumn(deleteColumn, "Delete");
        ratingGrid.setColumnWidth(deleteColumn, 75, Style.Unit.PX);
    }
}
