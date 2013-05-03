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
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingView extends ViewWithUiHandlers<RatingUiHandlers> implements RatingPresenter.MyView {
    public interface Binder extends UiBinder<Widget, RatingView> {
    }

    @UiField
    Button create;
    @UiField(provided = true)
    CellTable<RatingDto> ratingGrid;

    private final ListDataProvider<RatingDto> ratingDataProvider;

    @Inject
    public RatingView(final Binder uiBinder) {
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

        initDataColumns();
        initActionColumns();
    }

    private void initDataColumns() {
        Column<RatingDto, Number> idColumn = new Column<RatingDto, Number>(new NumberCell()) {
            @Override
            public Long getValue(RatingDto ratingDto) {
                return ratingDto.getId();
            }
        };

        Column<RatingDto, String> carColumn = new Column<RatingDto, String>(new TextCell()) {
            @Override
            public String getValue(RatingDto ratingDto) {
                return ratingDto.toString();
            }
        };

        Column<RatingDto, Number> ratingColumn = new Column<RatingDto, Number>(new NumberCell()) {
            @Override
            public Number getValue(RatingDto ratingDto) {
                return ratingDto.getRating();
            }
        };

        ratingGrid.addColumn(idColumn, "ID");
        ratingGrid.addColumn(carColumn, "Car");
        ratingGrid.addColumn(ratingColumn, "Rating");

        ratingGrid.setColumnWidth(idColumn, 50, Style.Unit.PX);
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
