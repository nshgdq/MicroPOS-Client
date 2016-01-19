package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbSalesOrderPresenter extends DbEntityPresenter<SalesOrder> {

    TableColumn<SalesOrder, String> id;
    TableColumn<SalesOrder, String> status;
    TableColumn<SalesOrder, String> total;
    TableColumn<SalesOrder, String> payments;

    public DbSalesOrderPresenter() {
        super();

        // Hack to remove new & submit buttons. Removal order is important. (see DbEntityPresenter)
        menu.remove(2);
        menu.remove(1);

    }

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
        // Disabled
        return null;
    }

    @Override
    void updateTableContent(TableView<SalesOrder> table) {
        App.apiProxy.listSalesOrders(new RunLaterCallback<List<SalesOrder>>() {
            @Override
            public void laterSuccess(List<SalesOrder> salesOrders) {
                table.setItems(FXCollections.observableList(salesOrders));
            }
        });
    }

    @Override
    void submitItem(SalesOrder item) {
        // Disabled
    }

    @Override
    void deleteItem(SalesOrder item) {
        if (item == null)
            return;
        App.apiProxy.removeSalesOrder(item.getId(), new RunLaterCallback<Boolean>() {
            @Override
            public void laterSuccess(Boolean aBoolean) {
                // No delete notification
                App.main.refresh();
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                super.laterFailure(error);
                App.main.refresh();
            }
        });
    }
}
