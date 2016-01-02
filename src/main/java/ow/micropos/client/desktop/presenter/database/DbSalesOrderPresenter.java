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
    TableColumn<SalesOrder, String> employee;
    TableColumn<SalesOrder, String> total;

    @Override
    Node[] getEditControls() {
        return new Node[0];
    }

    @Override
    TableColumn<SalesOrder, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString());
        employee = createTableColumn("Employee", param -> param.getValue().employeeNameProperty());
        total = createTableColumn("Total", param -> param.getValue().grandTotalProperty().asString());

        return new TableColumn[]{id, employee, total};
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
        return null;
    }

    @Override
    void submitItem(SalesOrder item) {

    }

    @Override
    void deleteItem(SalesOrder item) {
        if (item == null)
            return;

        App.api.removeSalesOrder(item.getId(), new AlertCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean, Response response) {
                refresh();
                App.notify.showAndWait("Sales Order " + item.getId() + " removed.");
            }
        });
    }
}
