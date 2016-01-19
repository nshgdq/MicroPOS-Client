package ow.micropos.client.desktop.presenter.target;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.target.Customer;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.List;

public class TargetPresenter extends ItemPresenter<Object> {

    @FXML StackPane newOption;
    @FXML StackPane cancelOption;
    @FXML StackPane spBack;
    @FXML StackPane spNext;
    @FXML GridView<SalesOrder> gvOrderGrid;

    @FXML
    public void initialize() {

        newOption.setOnMouseClicked(event -> Platform.runLater(() -> {
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
        }));

        cancelOption.setOnMouseClicked(event -> Platform.runLater(App.main::backRefresh));
        spNext.setOnMouseClicked(event -> Platform.runLater(gvOrderGrid::nextPage));
        spBack.setOnMouseClicked(event -> Platform.runLater(gvOrderGrid::prevPage));

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
            App.apiProxy.getSalesOrderBySeat(
                    ((Seat) getItem()).getId(),
                    SalesOrderStatus.OPEN,
                    new RunLaterCallback<List<SalesOrder>>() {
                        @Override
                        public void laterSuccess(List<SalesOrder> salesOrders) {
                            gvOrderGrid.setItems(FXCollections.observableList(salesOrders));
                        }
                    }
            );

        } else if (getItem() instanceof Customer) {
            App.apiProxy.getSalesOrderByCustomer(
                    ((Customer) getItem()).getId(),
                    SalesOrderStatus.OPEN,
                    new RunLaterCallback<List<SalesOrder>>() {
                        @Override
                        public void laterSuccess(List<SalesOrder> salesOrders) {
                            gvOrderGrid.setItems(FXCollections.observableList(salesOrders));
                        }
                    }
            );
        }
    }

    @Override
    protected void updateItem(Object currentItem, Object newItem) {}

    @Override
    public void dispose() {

    }

    /*****************************************************************************
     *                                                                           *
     * Reusable Components                                                       *
     *                                                                           *
     *****************************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("View", ActionType.TAB_SELECT, event -> Platform.runLater(this::refresh)),
            new Action("Split", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.setNextRefresh(App.movePresenter, gvOrderGrid.getItems())))
    );

}
