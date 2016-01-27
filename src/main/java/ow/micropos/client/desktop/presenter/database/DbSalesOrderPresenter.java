package ow.micropos.client.desktop.presenter.database;

import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.PaymentEntryStatus;
import ow.micropos.client.desktop.model.enums.PaymentEntryType;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.List;

public class DbSalesOrderPresenter extends DbEntityPresenter<SalesOrder> {

    TableColumn<SalesOrder, String> id;
    TableColumn<SalesOrder, String> status;
    TableColumn<SalesOrder, String> total;
    TableColumn<SalesOrder, String> payments;

    Label lblSales, lblPayments, lblCash, lblCredit, lblCheck, lblGC;

    public DbSalesOrderPresenter() {
        super();

        // Hack to remove new & submit buttons. Removal order is important. (see DbEntityPresenter)
        menu.remove(2);
        menu.remove(1);

    }

    @Override
    Label getTitleLabel() {
        return new Label("Sales Order Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{
                new Label("Sales"), new Label("Payments"), new Label("Cash"),
                new Label("Credit"), new Label("Check"), new Label("GC")
        };
    }

    @Override
    Node[] getEditControls() {
        lblSales = new Label();
        lblPayments = new Label();
        lblCash = new Label();
        lblCredit = new Label();
        lblCheck = new Label();
        lblGC = new Label();
        return new Node[]{lblSales, lblPayments, lblCash, lblCredit, lblCheck, lblGC};
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
    void toggleControls(boolean visible) {

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
                ObservableList<SalesOrder> obs = FXCollections.observableList(salesOrders);

                table.setItems(obs);

                lblSales.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return "$" + obs.stream()
                                .filter(so -> so.hasStatuses(SalesOrderStatus.OPEN, SalesOrderStatus.CLOSED))
                                .map(so -> so.grandTotalProperty().get())
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .toString();
                    }
                });

                lblPayments.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return "$" + obs.stream()
                                .filter(so -> so.hasStatuses(SalesOrderStatus.OPEN, SalesOrderStatus.CLOSED))
                                .flatMap(so -> so.getPaymentEntries().stream())
                                .filter(pe -> pe.hasStatus(PaymentEntryStatus.PAID))
                                .map(PaymentEntry::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .toString();
                    }
                });

                lblCash.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return getPaymentTotalOf(obs, PaymentEntryType.CASH);
                    }
                });

                lblCredit.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return getPaymentTotalOf(obs, PaymentEntryType.CREDIT);
                    }
                });

                lblCheck.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return getPaymentTotalOf(obs, PaymentEntryType.CHECK);
                    }
                });

                lblCredit.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return getPaymentTotalOf(obs, PaymentEntryType.CREDIT);
                    }
                });

                lblGC.textProperty().bind(new StringBinding() {
                    {bind(obs);}

                    @Override
                    protected String computeValue() {
                        return getPaymentTotalOf(obs, PaymentEntryType.GIFTCARD);
                    }
                });

            }
        });
    }

    @Override
    void submitItem(SalesOrder item) {
        // Disabled
    }

    @Override
    void deleteItem(SalesOrder item) {
        if (item == null) {
            // Do nothing
        } else if (!item.getPaymentEntries().filtered(pe -> pe.hasType(PaymentEntryType.CREDIT)).isEmpty()) {
            App.confirm.showAndWait(
                    "Order has CREDIT payments. Do not forget to charge back. Continue?",
                    () -> App.apiProxy.removeSalesOrder(item.getId(), RunLaterCallback.deleteCallback(item, table))
            );
        } else {
            App.apiProxy.removeSalesOrder(item.getId(), RunLaterCallback.deleteCallback(item, table));
        }
    }

    private String getPaymentTotalOf(ObservableList<SalesOrder> obs, PaymentEntryType type) {
        return "$" + obs.stream()
                .filter(so -> so.hasStatuses(SalesOrderStatus.OPEN, SalesOrderStatus.CLOSED))
                .flatMap(so -> so.getPaymentEntries().stream())
                .filter(pe -> pe.hasStatus(PaymentEntryStatus.PAID) && pe.hasType(type))
                .map(PaymentEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .toString();
    }
}
