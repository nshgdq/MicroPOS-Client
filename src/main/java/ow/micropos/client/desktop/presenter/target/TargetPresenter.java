package ow.micropos.client.desktop.presenter.target;

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
import ow.micropos.client.desktop.model.people.Customer;
import ow.micropos.client.desktop.model.seating.Seat;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;

import java.math.BigDecimal;
import java.util.List;

public class TargetPresenter extends ItemPresenter<Object> {

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

        if (getItem() == null) {
            // Do nothing

        } else if (getItem() instanceof Seat) {
            App.api.getSalesOrderBySeat(
                    ((Seat) getItem()).getId(),
                    SalesOrderStatus.OPEN,
                    (AlertCallback<List<SalesOrder>>) (salesOrders, response) ->
                            gvOrderGrid.setItems(FXCollections.observableList(salesOrders))
            );

        } else if (getItem() instanceof Customer) {
            App.api.getSalesOrderByCustomer(
                    ((Customer) getItem()).getId(),
                    SalesOrderStatus.OPEN,
                    (AlertCallback<List<SalesOrder>>) (salesOrders, response) ->
                            gvOrderGrid.setItems(FXCollections.observableList(salesOrders))
            );

        }
    }

    @Override
    protected void updateItem(Object currentItem, Object newItem) {}

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

                if (getItem() == null) {
                    // Do nothing

                } else if (getItem() instanceof Seat) {
                    // Inherit gratuity settings from first sales order
                    BigDecimal gratuityPercent;
                    List<SalesOrder> currentOrders = gvOrderGrid.getItems();
                    if (currentOrders == null || currentOrders.size() <= 0)
                        gratuityPercent = BigDecimal.ZERO;
                    else
                        gratuityPercent = currentOrders.get(0).getGratuityPercent();

                    App.main.setNextRefresh(App.orderEditorPresenter, SalesOrder.forSeat(
                            App.employee,
                            (Seat) getItem(),
                            App.properties.getBd("tax-percent"),
                            gratuityPercent
                    ));

                } else if (getItem() instanceof Customer) {
                    App.main.setNextRefresh(App.orderEditorPresenter, SalesOrder.forCustomer(
                            App.employee,
                            (Customer) getItem(),
                            App.properties.getBd("tax-percent")
                    ));

                }
            })),
            new Action("Split", ActionType.FINISH, event -> Platform.runLater(() ->
                    App.main.setNextRefresh(App.movePresenter, gvOrderGrid.getItems())))
            ,
            new Action("Cancel", ActionType.FINISH, event -> Platform.runLater(App.main::back)),
            new Action("Prev Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::prevPage)),
            new Action("Next Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::nextPage))
    );

}
