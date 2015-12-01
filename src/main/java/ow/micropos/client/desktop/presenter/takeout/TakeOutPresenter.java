package ow.micropos.client.desktop.presenter.takeout;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.comtel2000.keyboard.control.VkProperties;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.people.Customer;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;

import java.util.List;
import java.util.function.Predicate;

public class TakeOutPresenter extends Presenter {

    @FXML TextField tfPhoneNumber;
    @FXML TextField tfFirstName;
    @FXML TextField tfLastName;

    @FXML Button newCustomer;

    @FXML ListView<Customer> customers;
    @FXML GridView<SalesOrder> salesOrders;

    private FilteredList<Customer> filteredCustomers;
    private FilteredList<SalesOrder> filteredSalesOrders;

    @FXML
    public void initialize() {

        tfFirstName.textProperty().addListener(changeUpdateFilter);
        tfLastName.textProperty().addListener(changeUpdateFilter);
        tfPhoneNumber.textProperty().addListener(changeUpdateFilter);

        tfFirstName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfLastName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfPhoneNumber.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        customers.setCellFactory(param -> PresenterCellAdapter.load("/view/takeout/view_customer.fxml"));
        customers.getSelectionModel().selectedItemProperty().addListener((obv, ov, nv) -> {
            if (nv != null)
                App.main.setNextRefresh(
                        App.orderEditorPresenter,
                        SalesOrder.forCustomer(App.employee, nv, App.properties.getBd("tax-percent"))
                );
        });

        salesOrders.setRows(App.properties.getInt("take-out-rows"));
        salesOrders.setCols(App.properties.getInt("take-out-cols"));
        salesOrders.setCellFactory(param -> {
            ViewSalesOrder presenter = Presenter.load("/view/common/view_sales_order.fxml");
            presenter.onClickDefaults();
            return presenter;
        });

        newCustomer.setOnAction(event -> {
            String fn = tfFirstName.getText();
            String ln = tfLastName.getText();
            String pn = tfPhoneNumber.getText();

            if (!fn.isEmpty() || !ln.isEmpty() || !pn.isEmpty()) {
                Customer c = new Customer(fn, ln, pn);
                App.api.postCustomer(c, (AlertCallback<Long>) (aLong, response) -> {
                    c.setId(aLong);
                    App.main.setNextRefresh(
                            App.orderEditorPresenter,
                            SalesOrder.forCustomer(App.employee, c, App.properties.getBd("tax-percent"))
                    );
                });
            }
        });
    }

    @Override
    public void clear() {
        tfLastName.clear();
        tfFirstName.clear();
        tfPhoneNumber.clear();
        customers.setItems(FXCollections.emptyObservableList());
        salesOrders.setItems(FXCollections.emptyObservableList());
    }

    @Override
    public void refresh() {

        // TODO : Merge into single request

        App.api.getCustomers((AlertCallback<List<Customer>>) (cList, response) -> {
            filteredCustomers = new FilteredList<>(FXCollections.observableList(cList));
            updateCustomerFilter();
            customers.setItems(filteredCustomers);
        });

        App.api.getSalesOrders(SalesOrderStatus.OPEN, SalesOrderType.TAKEOUT, (AlertCallback<List<SalesOrder>>) (soList, response) -> {
            filteredSalesOrders = new FilteredList<>(FXCollections.observableList(soList));
            updateSalesOrderFilter();
            salesOrders.setItems(filteredSalesOrders);
        });

    }

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {}

    private void updateCustomerFilter() {
        String fn = tfFirstName.getText().toLowerCase();
        String ln = tfLastName.getText().toLowerCase();
        String pn = tfPhoneNumber.getText().toLowerCase();

        filteredCustomers.setPredicate(new Predicate<Customer>() {
            @Override
            public boolean test(Customer customer) {
                return !(fn.isEmpty() && ln.isEmpty() && pn.isEmpty())
                        && customer.getFirstName().toLowerCase().contains(fn)
                        && customer.getLastName().toLowerCase().contains(ln)
                        && customer.getPhoneNumber().toLowerCase().contains(pn);

            }
        });
    }

    private void updateSalesOrderFilter() {
        String fn = tfFirstName.getText().toLowerCase();
        String ln = tfLastName.getText().toLowerCase();
        String pn = tfPhoneNumber.getText().toLowerCase();

        filteredSalesOrders.setPredicate(new Predicate<SalesOrder>() {
            @Override
            public boolean test(SalesOrder salesOrder) {
                if (!salesOrder.hasType(SalesOrderType.TAKEOUT))
                    return false;
                else if (!salesOrder.hasCustomer())
                    return false;
                else if (fn.isEmpty() && ln.isEmpty() && pn.isEmpty())
                    return true;
                else if (salesOrder.getCustomer().getFirstName().toLowerCase().contains(fn)
                        && salesOrder.getCustomer().getLastName().toLowerCase().contains(ln)
                        && salesOrder.getCustomer().getPhoneNumber().toLowerCase().contains(pn))
                    return true;
                else
                    return false;
            }
        });
    }

    private ChangeListener<String> changeUpdateFilter = (observable, oldValue, newValue) -> {
        updateCustomerFilter();
        updateSalesOrderFilter();
    };

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Dine In", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.dineInPresenter))
            ),
            new Action("Take Out", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.takeOutPresenter))
            ),
            new Action("Finder", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.finderPresenter))
            ),
            new Action("Manager", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.managerPresenter))
            ),
            new Action("Reset", ActionType.BUTTON, event -> Platform.runLater(this::clear))
    );

}
