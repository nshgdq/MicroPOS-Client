package ow.micropos.client.desktop.presenter.finder;


import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.comtel2000.keyboard.control.VkProperties;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;
import java.util.function.Predicate;

public class FinderPresenter extends Presenter {

    @FXML StackPane spReset;
    @FXML StackPane spNext;
    @FXML StackPane spBack;
    @FXML VBox vbOrderEntry;
    @FXML StackPane spOrderEntry;
    @FXML TextField tfPhoneNumber;
    @FXML TextField tfFirstName;
    @FXML TextField tfLastName;
    @FXML TextField tfSeat;
    @FXML TextField tfOrderNumber;
    @FXML CheckBox cbTypeDineIn;
    @FXML CheckBox cbTypeTakeOut;
    @FXML CheckBox cbStatusPaid;
    @FXML CheckBox cbStatusOpen;
    @FXML CheckBox cbStatusVoid;

    @FXML GridView<SalesOrder> orders;

    private FilteredList<SalesOrder> filteredSalesOrders = new FilteredList<>(FXCollections.observableArrayList());

    private String fn = "";
    private String ln = "";
    private String pn = "";
    private String st = "";
    private String on = "";

    @FXML
    public void initialize() {

        spReset.setOnMouseClicked(event -> Platform.runLater(this::refresh));
        spBack.setOnMouseClicked(event -> Platform.runLater(orders::prevPage));
        spNext.setOnMouseClicked(event -> Platform.runLater(orders::nextPage));

        tfPhoneNumber.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfFirstName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfLastName.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        tfSeat.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfOrderNumber.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        tfPhoneNumber.textProperty().addListener(changeUpdateFilter);
        tfFirstName.textProperty().addListener(changeUpdateFilter);
        tfLastName.textProperty().addListener(changeUpdateFilter);
        tfSeat.textProperty().addListener(changeUpdateFilter);
        tfOrderNumber.textProperty().addListener(changeUpdateFilter);
        cbTypeTakeOut.setOnAction(eventUpdateFilter);
        cbTypeDineIn.setOnAction(eventUpdateFilter);
        cbStatusPaid.setOnAction(eventUpdateFilter);
        cbStatusOpen.setOnAction(eventUpdateFilter);
        cbStatusVoid.setOnAction(eventUpdateFilter);

        orders.setPage(0);
        orders.setRows(App.properties.getInt("finder-rows"));
        orders.setCols(App.properties.getInt("finder-cols"));
        orders.setCellFactory(param -> {
            ViewSalesOrder presenter = Presenter.load("/view/common/view_sales_order.fxml");
            presenter.onClickDefaults();
            return presenter;
        });

    }

    @Override
    public void refresh() {
        orders.setItems(FXCollections.emptyObservableList());
        tfPhoneNumber.clear();
        tfFirstName.clear();
        tfLastName.clear();
        tfSeat.clear();
        tfOrderNumber.clear();
        cbTypeTakeOut.setSelected(true);
        cbTypeDineIn.setSelected(true);
        cbStatusPaid.setSelected(false);
        cbStatusOpen.setSelected(true);
        cbStatusVoid.setSelected(false);
        orders.setPage(0);

        if (App.employee == null)
            menu.remove(manager);
        else if (!App.employee.hasPermission(Permission.CLIENT_MANAGER) && menu.contains(manager))
            menu.remove(manager);
        else if (App.employee.hasPermission(Permission.CLIENT_MANAGER) && !menu.contains(manager))
            menu.add(manager);

        App.apiProxy.getSalesOrders(new RunLaterCallback<List<SalesOrder>>() {
            @Override
            public void laterSuccess(List<SalesOrder> salesOrders) {
                filteredSalesOrders = new FilteredList<>(FXCollections.observableList(salesOrders));
                updateSalesOrderFilter();
                orders.setItems(filteredSalesOrders);
            }
        });
    }

    @Override
    public void dispose() {
        tfPhoneNumber.textProperty().removeListener(changeUpdateFilter);
        tfFirstName.textProperty().removeListener(changeUpdateFilter);
        tfLastName.textProperty().removeListener(changeUpdateFilter);
        tfSeat.textProperty().removeListener(changeUpdateFilter);
        tfOrderNumber.textProperty().removeListener(changeUpdateFilter);
        cbTypeTakeOut.setOnAction(null);
        cbTypeDineIn.setOnAction(null);
        cbStatusPaid.setOnAction(null);
        cbStatusOpen.setOnAction(null);
        cbStatusVoid.setOnAction(null);
        orders.setItems(null);
    }

    private void updateSalesOrderFilter() {
        fn = tfFirstName.getText().toLowerCase();
        ln = tfLastName.getText().toLowerCase();
        pn = tfPhoneNumber.getText().toLowerCase();
        st = tfSeat.getText().toLowerCase();
        on = tfOrderNumber.getText().toLowerCase();
        filteredSalesOrders.setPredicate(null);
        filteredSalesOrders.setPredicate(filter);
    }

    /*****************************************************************************
     *                                                                           *
     * Filters                                                                   *
     *                                                                           *
     *****************************************************************************/

    private final ChangeListener<String> changeUpdateFilter = (observable, oldValue, newValue) -> updateSalesOrderFilter();

    private final EventHandler<ActionEvent> eventUpdateFilter = (event) -> updateSalesOrderFilter();

    private final Predicate<SalesOrder> filter = (salesOrder) -> {
        try {
            if (salesOrder.hasType(SalesOrderType.DINEIN) && !cbTypeDineIn.isSelected())
                return false;
            else if (salesOrder.hasType(SalesOrderType.TAKEOUT) && !cbTypeTakeOut.isSelected())
                return false;
            else if (salesOrder.hasStatus(SalesOrderStatus.CLOSED) && !cbStatusPaid.isSelected())
                return false;
            else if (salesOrder.hasStatus(SalesOrderStatus.VOID) && !cbStatusVoid.isSelected())
                return false;
            else if (salesOrder.hasStatus(SalesOrderStatus.OPEN) && !cbStatusOpen.isSelected())
                return false;
            else if (!(fn.isEmpty() && ln.isEmpty() && pn.isEmpty()) && (!salesOrder.hasCustomer() || !salesOrder.getCustomer().contains(fn, ln, pn)))
                return false;
            else if (!st.isEmpty() && (!salesOrder.hasSeat() || !salesOrder.getSeat().getTag().toLowerCase().contains(st)))
                return false;
            else if (!on.isEmpty() && Long.compare(salesOrder.getId(), Long.parseLong(on)) != 0)
                return false;
        } catch (Exception e) {
            // TODO : Log errors
        }
        return true;
    };

    /******************************************************************
     *                                                                *
     * Menu
     *                                                                *
     ******************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final Action manager = new Action("Manage", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
            App.main.swapRefresh(App.managerPresenter))
    );

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Dine In", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.dineInPresenter))
            ),
            new Action("Take Out", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.takeOutPresenter))
            ),
            new Action("Finder", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.finderPresenter))
            )
    );

}
