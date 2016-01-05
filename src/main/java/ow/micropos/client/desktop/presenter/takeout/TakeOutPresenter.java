package ow.micropos.client.desktop.presenter.takeout;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.comtel2000.keyboard.control.VkProperties;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.target.Customer;

import java.util.List;
import java.util.function.Predicate;

public class TakeOutPresenter extends Presenter {

    @FXML StackPane spCreate;
    @FXML StackPane spReset;
    @FXML StackPane spBack;
    @FXML StackPane spNext;
    @FXML GridView<Customer> gvCustomers;
    @FXML TextField tfPhoneNumber;
    @FXML TextField tfFirstName;
    @FXML TextField tfLastName;

    private FilteredList<Customer> filteredCustomers = new FilteredList<>(FXCollections.observableArrayList());
    ;

    @FXML
    public void initialize() {

        spReset.setOnMouseClicked(event -> Platform.runLater(this::refresh));
        spBack.setOnMouseClicked(event -> Platform.runLater(gvCustomers::prevPage));
        spNext.setOnMouseClicked(event -> Platform.runLater(gvCustomers::nextPage));
        spCreate.setOnMouseClicked(event -> {
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

        filteredCustomers = new FilteredList<>(FXCollections.emptyObservableList());

        tfFirstName.textProperty().addListener(event -> updateCustomerFilter());
        tfLastName.textProperty().addListener(event -> updateCustomerFilter());
        tfPhoneNumber.textProperty().addListener(event -> updateCustomerFilter());

        tfFirstName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfLastName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfPhoneNumber.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        gvCustomers.setPage(0);
        gvCustomers.setRows(App.properties.getInt("take-out-rows"));
        gvCustomers.setCols(App.properties.getInt("take-out-cols"));
        gvCustomers.setCellFactory(param -> {
            ViewCustomer presenter = Presenter.load("/view/takeout/view_customer.fxml");
            presenter.onClick(event -> {
                List<SalesOrder> orders = presenter.getItem().getSalesOrders();
                if (orders.size() == 0) {
                    App.main.setNextRefresh(
                            App.orderEditorPresenter,
                            SalesOrder.forCustomer(
                                    App.employee,
                                    presenter.getItem(),
                                    App.properties.getBd("tax-percent"))
                    );
                } else {
                    App.main.setNextRefresh(
                            App.targetPresenter,
                            presenter.getItem()
                    );
                }
            });
            return presenter;
        });
    }

    @Override
    public void refresh() {
        tfLastName.clear();
        tfFirstName.clear();
        tfPhoneNumber.clear();

        if (App.employee == null)
            menu.remove(manager);
        else if (!App.employee.hasPermission(Permission.CLIENT_MANAGER) && menu.contains(manager))
            menu.remove(manager);
        else if (App.employee.hasPermission(Permission.CLIENT_MANAGER) && !menu.contains(manager))
            menu.add(manager);

        App.api.getCustomers((AlertCallback<List<Customer>>) (cList, response) ->
                Platform.runLater(() -> {
                    filteredCustomers = new FilteredList<>(FXCollections.observableList(cList));
                    updateCustomerFilter();
                    gvCustomers.setItems(filteredCustomers);
                }));
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
                if (fn.isEmpty() && ln.isEmpty() && pn.isEmpty()) {
                    return !customer.getSalesOrders().isEmpty();

                } else {
                    return customer != null
                            && customer.getFirstName() != null
                            && customer.getLastName() != null
                            && customer.getPhoneNumber() != null
                            && customer.getFirstName().toLowerCase().contains(fn)
                            && customer.getLastName().toLowerCase().contains(ln)
                            && customer.getPhoneNumber().toLowerCase().contains(pn);
                }
            }
        });
    }

    /******************************************************************
     *                                                                *
     * Menu
     *                                                                *
     ******************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final Action manager = new Action("Manager", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
            App.main.swapRefresh(App.managerPresenter))
    );

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Dine In", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.dineInPresenter))
            ),
            new Action("Take Out", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.takeOutPresenter))
            ),
            new Action("Finder", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.finderPresenter))
            )
    );

}
