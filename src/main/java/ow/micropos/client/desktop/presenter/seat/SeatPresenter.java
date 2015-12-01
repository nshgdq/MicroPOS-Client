package ow.micropos.client.desktop.presenter.seat;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.seating.Seat;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;

import java.math.BigDecimal;
import java.util.List;

public class SeatPresenter extends ItemPresenter<Seat> {

    @FXML GridView<SalesOrder> gvOrderGrid;

    @FXML
    public void initialize() {

        gvOrderGrid.setPage(0);
        gvOrderGrid.setRows(App.properties.getInt("multi-rows"));
        gvOrderGrid.setCols(App.properties.getInt("multi-cols"));
        gvOrderGrid.setHorizontal(true);
        gvOrderGrid.setCellFactory(param -> {
            ViewSalesOrder presenter = Presenter.load("/view/common/view_sales_order.fxml");
            presenter.onClickDefaults();
            return presenter;
        });

    }

    @Override
    public void refresh() {
        gvOrderGrid.setPage(0);
        gvOrderGrid.setItems(FXCollections.emptyObservableList());

        if (App.apiIsBusy.compareAndSet(false, true)) {
            App.api.getSalesOrderBySeat(
                    getItem().getId(),
                    SalesOrderStatus.OPEN,
                    (AlertCallback<List<SalesOrder>>) (salesOrders, response) ->
                            gvOrderGrid.setItems(FXCollections.observableList(salesOrders))
            );
        }
    }

    @Override
    protected void updateItem(Seat currentItem, Seat newItem) {

    }

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {

    }

    /*****************************************************************************
     *                                                                           *
     * Reusable Components                                                       *
     *                                                                           *
     *****************************************************************************/

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("New", ActionType.FINISH, event -> Platform.runLater(() -> {

                // Inherit gratuity settings from first sales order
                BigDecimal gratuityPercent;
                List<SalesOrder> currentOrders = gvOrderGrid.getItems();
                if (currentOrders == null || currentOrders.size() <= 0)
                    gratuityPercent = BigDecimal.ZERO;
                else
                    gratuityPercent = currentOrders.get(0).getGratuityPercent();

                System.out.println(gratuityPercent);

                App.main.setNextRefresh(App.orderEditorPresenter, SalesOrder.forSeat(
                        App.employee,
                        getItem(),
                        App.properties.getBd("tax-percent"),
                        gratuityPercent
                ));
            })),
            new Action("Split", ActionType.FINISH, event -> Platform.runLater(() ->
                    App.main.setNextRefresh(App.movePresenter, gvOrderGrid.getItems())))
            ,
            new Action("Cancel", ActionType.FINISH, event -> Platform.runLater(App.main::back)),
            new Action("Prev Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::prevPage)),
            new Action("Next Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::nextPage))
    );

}
