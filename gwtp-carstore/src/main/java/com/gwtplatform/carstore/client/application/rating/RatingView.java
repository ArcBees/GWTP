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
import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingView extends ViewWithUiHandlers<RatingUiHandlers> implements RatingPresenter.MyView {
    public interface Binder extends UiBinder<Widget, RatingView> {
    }

    @UiField
    Button create;
    @UiField(provided = true)
    CellTable<Rating> ratingGrid;

    private final ListDataProvider<Rating> ratingDataProvider;

    @Inject
    public RatingView(final Binder uiBinder) {
        initRatingGrid();
        
        initWidget(uiBinder.createAndBindUi(this));

        ratingDataProvider = new ListDataProvider<Rating>();
        ratingDataProvider.addDataDisplay(ratingGrid);
    }

    @Override
    public void displayRatings(List<Rating> ratings) {
        ratingDataProvider.getList().clear();
        ratingDataProvider.getList().addAll(ratings);
    }

    @Override
    public void addRating(Rating rating) {
        ratingDataProvider.getList().add(rating);
    }

    @Override
    public void removeRating(Rating rating) {
        ratingDataProvider.getList().remove(rating);
    }

    @UiHandler("create")
    void onCreateClicked(ClickEvent event) {
        getUiHandlers().onCreate();
    }

    private void initRatingGrid() {
        ratingGrid = new CellTable<Rating>();
        ratingGrid.setSelectionModel(new NoSelectionModel<Rating>());

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<Rating, Number> idColumn = new Column<Rating, Number>(new NumberCell()) {
            @Override
            public Long getValue(Rating rating) {
                return rating.getId();
            }
        };

        Column<Rating, String> carColumn = new Column<Rating, String>(new TextCell()) {
            @Override
            public String getValue(Rating rating) {
                return rating.toString();
            }
        };

        Column<Rating, Number> ratingColumn = new Column<Rating, Number>(new NumberCell()) {
            @Override
            public Number getValue(Rating rating) {
                return rating.getRating();
            }
        };

        ratingGrid.addColumn(idColumn, "ID");
        ratingGrid.addColumn(carColumn, "Car");
        ratingGrid.addColumn(ratingColumn, "Rating");

        ratingGrid.setColumnWidth(idColumn, 50, Style.Unit.PX);
    }

    private void initActionColumns() {
        Cell<Rating> deleteCell = new ActionCell<Rating>("Delete", new ActionCell.Delegate<Rating>() {
            @Override
            public void execute(Rating rating) {
                Boolean confirm = Window.confirm("Are you sure you want to delete" + rating.toString() + "?");

                if (confirm) {
                    getUiHandlers().onDelete(rating);
                }
            }
        });

        IdentityColumn<Rating> deleteColumn = new IdentityColumn<Rating>(deleteCell);
        deleteColumn.setCellStyleNames("delete");
        ratingGrid.addColumn(deleteColumn, "Delete");
        ratingGrid.setColumnWidth(deleteColumn, 75, Style.Unit.PX);
    }
}
