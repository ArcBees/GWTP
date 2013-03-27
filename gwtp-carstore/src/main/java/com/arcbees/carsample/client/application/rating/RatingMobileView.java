package com.arcbees.carsample.client.application.rating;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.rating.renderer.RatingCellFactory;
import com.arcbees.carsample.client.resources.MobileDataListStyle;
import com.arcbees.carsample.shared.domain.Rating;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingMobileView extends ViewWithUiHandlers<RatingUiHandlers> implements RatingPresenter.MyView {
    public interface Binder extends UiBinder<Widget, RatingMobileView> {
    }

    @UiField(provided = true)
    CellList<Rating> ratingList;

    private final ListDataProvider<Rating> ratingDataProvider;
    private final SingleSelectionModel<Rating> selectionModel;

    @Inject
    public RatingMobileView(Binder uiBinder, RatingCellFactory ratingCellFactory,
            MobileDataListStyle mobileDataListStyle) {
        ratingList = new CellList<Rating>(ratingCellFactory.create(setupRemoveAction()), mobileDataListStyle);

        initWidget(uiBinder.createAndBindUi(this));

        ratingDataProvider = new ListDataProvider<Rating>();
        ratingDataProvider.addDataDisplay(ratingList);
        selectionModel = new SingleSelectionModel<Rating>();
        ratingList.setSelectionModel(selectionModel);
    }

    @Override
    public void displayRatings(List<Rating> ratings) {
        ratingDataProvider.getList().clear();
        ratingDataProvider.getList().addAll(ratings);
        ratingDataProvider.refresh();
        ratingList.setPageSize(ratings.size());
    }

    @Override
    public void addRating(Rating rating) {
        ratingDataProvider.getList().add(rating);
    }

    @Override
    public void removeRating(Rating rating) {
        ratingDataProvider.getList().remove(rating);
    }

    private ActionCell.Delegate<Rating> setupRemoveAction() {
        return new ActionCell.Delegate<Rating>() {
            @Override
            public void execute(Rating rating) {
                Boolean confirm = Window.confirm("Are you sure you want to delete" + rating.toString() + "?");
                if (confirm) {
                    getUiHandlers().onDelete(rating);
                }
            }
        };
    }
}
