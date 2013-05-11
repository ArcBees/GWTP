/*
 * Copyright 2013 ArcBees Inc.
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.carstore.client.application.rating.renderer.RatingCellFactory;
import com.gwtplatform.carstore.client.resources.MobileDataListStyle;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingMobileView extends ViewWithUiHandlers<RatingUiHandlers> implements RatingPresenter.MyView {
    interface Binder extends UiBinder<Widget, RatingMobileView> {
    }

    @UiField(provided = true)
    CellList<RatingDto> ratingList;

    private final ListDataProvider<RatingDto> ratingDataProvider;
    private final SingleSelectionModel<RatingDto> selectionModel;

    @Inject
    RatingMobileView(Binder uiBinder,
                     RatingCellFactory ratingCellFactory,
                     MobileDataListStyle mobileDataListStyle) {
        ratingList = new CellList<RatingDto>(ratingCellFactory.create(setupRemoveAction()), mobileDataListStyle);

        initWidget(uiBinder.createAndBindUi(this));

        ratingDataProvider = new ListDataProvider<RatingDto>();
        ratingDataProvider.addDataDisplay(ratingList);
        selectionModel = new SingleSelectionModel<RatingDto>();
        ratingList.setSelectionModel(selectionModel);
    }

    @Override
    public void displayRatings(List<RatingDto> ratingDtos) {
        ratingDataProvider.getList().clear();
        ratingDataProvider.getList().addAll(ratingDtos);
        ratingDataProvider.refresh();
        ratingList.setPageSize(ratingDtos.size());
    }

    @Override
    public void addRating(RatingDto ratingDto) {
        ratingDataProvider.getList().add(ratingDto);
    }

    @Override
    public void removeRating(RatingDto ratingDto) {
        ratingDataProvider.getList().remove(ratingDto);
    }

    private ActionCell.Delegate<RatingDto> setupRemoveAction() {
        return new ActionCell.Delegate<RatingDto>() {
            @Override
            public void execute(RatingDto ratingDto) {
                Boolean confirm = Window.confirm("Are you sure you want to delete" + ratingDto.toString() + "?");
                if (confirm) {
                    getUiHandlers().onDelete(ratingDto);
                }
            }
        };
    }
}
