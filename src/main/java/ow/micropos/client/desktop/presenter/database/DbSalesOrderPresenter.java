package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import retrofit.client.Response;

import java.util.List;

public class DbSalesOrderPresenter extends DbEntityPresenter<SalesOrder> {

    TableColumn<SalesOrder, String> id;
    TableColumn<SalesOrder, String> status;
    TableColumn<SalesOrder, String> total;
    TableColumn<SalesOrder, String> payments;

    @Override
    Node[] getEditControls() {
        return new Node[0];
    }

    @Override
    TableColumn<SalesOrder, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString());
        status = createTableColumn("Status", param -> param.getValue().statusProperty().asString());
        total = createTableColumn("Total", param -> param.getValue().grandTotalProperty().asString());
        payments = createTableColumn("Payment", param -> param.getValue().paymentSummaryProperty());

        return new TableColumn[]{id, status, payments, total};
    }

    @Override
    void updateTableContent(TableView<SalesOrder> table) {
        App.api.listSalesOrders(new AlertCallback<List<SalesOrder>>() {
            @Override
            public void onSuccess(List<SalesOrder> salesOrders, Response response) {
                table.setItems(FXCollections.observableList(salesOrders));
            }
        });
    }

    @Override
    void unbindItem(SalesOrder currentItem) {

    }

    @Override
    void bindItem(SalesOrder newItem) {

    }

    @Override
    void clearControls() {

    }

    @Override
    SalesOrder createNew() {
        App.notify.showAndWait("Not available on this screen.");
        return null;
    }

    @Override
    void submitItem(SalesOrder item) {
        App.notify.showAndWait("Not available on this screen.");
    }

    @Override
    void deleteItem(SalesOrder item) {
        if (item == null)
            return;

        App.api.removeSalesOrder(item.getId(), (AlertCallback<Boolean>) (aBoolean, response) -> {
            refresh();
            App.notify.showAndWait("Sales Order " + item.getId() + " removed.");
        });
    }
}
