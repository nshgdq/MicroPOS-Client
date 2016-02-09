package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.ComparatorUtils;

import java.util.ArrayList;

public class OrderEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML public Label time;
    @FXML public Label employee;
    @FXML public Label target;
    @FXML public Label info;
    @FXML public Label status;
    @FXML public Label productTotal;
    @FXML public Label chargeTotal;
    @FXML public Label gratuityTotal;
    @FXML public Label taxTotal;
    @FXML public Label grandTotal;
    @FXML public ListView<ProductEntry> orderEntries;
    @FXML public StackPane upOption;
    @FXML public StackPane downOption;
    @FXML public StackPane spTabContent;

    private MenuPresenter menuPresenter;
    private PaymentPresenter payPresenter;
    private CookPresenter cookPresenter;

    private int sentWeight;
    private int holdWeight;
    private int voidWeight;
    private int rSentWeight;
    private int rEditWeight;
    private int rHoldWeight;
    private int rVoidWeight;

    @FXML
    public void initialize() {

        menuPresenter = Presenter.load("/view/order/menu.fxml");
        payPresenter = Presenter.load("/view/order/payment.fxml");
        cookPresenter = Presenter.load("/view/order/cook.fxml");

        sentWeight = App.properties.getInt("pe-status-weight-sent");
        holdWeight = App.properties.getInt("pe-status-weight-hold");
        voidWeight = App.properties.getInt("pe-status-weight-void");
        rSentWeight = App.properties.getInt("pe-status-weight-request-sent");
        rEditWeight = App.properties.getInt("pe-status-weight-request-edit");
        rHoldWeight = App.properties.getInt("pe-status-weight-request-hold");
        rVoidWeight = App.properties.getInt("pe-status-weight-request-void");

        spTabContent.getChildren().clear();

        GridPane.setHalignment(info, HPos.CENTER);
        GridPane.setHalignment(employee, HPos.LEFT);
        GridPane.setHalignment(status, HPos.RIGHT);
        GridPane.setHalignment(target, HPos.LEFT);
        GridPane.setHalignment(time, HPos.RIGHT);
        GridPane.setHalignment(productTotal, HPos.RIGHT);
        GridPane.setHalignment(chargeTotal, HPos.RIGHT);
        GridPane.setHalignment(gratuityTotal, HPos.RIGHT);
        GridPane.setHalignment(taxTotal, HPos.RIGHT);
        GridPane.setHalignment(grandTotal, HPos.RIGHT);

        upOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, -2))
        );

        downOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, 2))
        );

        orderEntries.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/order/view_product_entry.fxml");
            presenter.fixWidth(orderEntries);
            presenter.onClick(event -> {
                if (presenter.getItem().getStatus() != ProductEntryStatus.VOID
                        && presenter.getItem().getStatus() != ProductEntryStatus.REQUEST_VOID) {
                    Platform.runLater(() -> {
                        App.productEditorPresenter.setContextList(getItem().getProductEntries());
                        App.main.setNextRefresh(App.productEditorPresenter, presenter.getItem());
                    });
                }
            });
            return new PresenterCellAdapter<>(presenter);
        });

    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            unsetLabel(time);
            unsetLabel(status);
            unsetLabel(target);
            unsetLabel(employee);
            unsetLabel(info);
            unsetLabel(productTotal);
            unsetLabel(chargeTotal);
            unsetLabel(gratuityTotal);
            unsetLabel(taxTotal);
            unsetLabel(grandTotal);
            unsetListView(orderEntries);
            menuPresenter.setItem(null);
            payPresenter.setItem(null);
            cookPresenter.setItem(null);

        } else {
            setLabel(info, new StringBinding() {
                {bind(newItem.idProperty());}

                @Override
                protected String computeValue() {
                    Long id = newItem.idProperty().get();
                    return (id == null) ? "---" : "Order # " + Long.toString(id);
                }

                @Override
                public void dispose() {
                    unbind(newItem.idProperty());
                }
            });
            setLabel(status, newItem.statusTextProperty());
            setLabel(employee, newItem.employeeNameProperty());
            setLabel(target, newItem.targetNameProperty());
            setLabel(time, Bindings.concat(newItem.prettyCookTimeProperty(), newItem.prettyTimeProperty()));
            setLabel(productTotal, newItem.productTotalProperty().asString());
            setLabel(chargeTotal, newItem.chargeTotalProperty().asString());
            setLabel(grandTotal, newItem.grandTotalProperty().asString());
            setLabel(gratuityTotal, newItem.gratuityTotalProperty().asString());
            setLabel(taxTotal, newItem.taxTotalProperty().asString());
            setListView(orderEntries, newItem.getProductEntries());

            sortProductEntries(orderEntries.getItems());

            menuPresenter.setItem(newItem);
            payPresenter.setItem(newItem);
            cookPresenter.setItem(newItem);

            setView(View.ORDER);
        }
    }

    @Override
    public void refresh() {
        sortProductEntries(orderEntries.getItems());
    }

    @Override
    public void dispose() {
        unsetLabel(time);
        unsetLabel(status);
        unsetLabel(target);
        unsetLabel(employee);
        unsetLabel(info);
        unsetLabel(productTotal);
        unsetLabel(chargeTotal);
        unsetLabel(gratuityTotal);
        unsetLabel(taxTotal);
        unsetLabel(grandTotal);
        unsetListView(orderEntries);
        setItem(null);
    }

    public static enum View {
        ORDER, PAY, COOK
    }

    public void setView(View view) {
        switch (view) {
            case ORDER:
                updateMenu(View.ORDER);
                spTabContent.getChildren().setAll(menuPresenter.getView());
                menuPresenter.refresh();
                break;
            case PAY:
                updateMenu(View.PAY);
                spTabContent.getChildren().setAll(payPresenter.getView());
                payPresenter.refresh();
                break;
            case COOK:
                updateMenu(View.COOK);
                cookPresenter.refresh();
                spTabContent.getChildren().setAll(cookPresenter.getView());
                break;
        }
    }

    public void scrollToEntry(ProductEntry entry) {
        Platform.runLater(() -> {
            orderEntries.scrollTo(entry);
            sortProductEntries(orderEntries.getItems());
        });
    }

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(new ArrayList<>());

    private final Action orderTabSelect = new Action("Order", ActionType.TAB_SELECT, event -> Platform.runLater(() -> setView(View.ORDER)));
    private final Action payTabSelect = new Action("Pay", ActionType.TAB_SELECT, event -> Platform.runLater(() -> setView(View.PAY)));
    private final Action cookTabSelect = new Action("Cook", ActionType.TAB_SELECT, event -> Platform.runLater(() -> setView(View.COOK)));
    private final Action orderTabDefault = new Action("Order", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> setView(View.ORDER)));
    private final Action payTabDefault = new Action("Pay", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> setView(View.PAY)));
    private final Action cookTabDefault = new Action("Cook", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> setView(View.COOK)));

    private void updateMenu(View view) {
        switch (view) {
            case ORDER:
                menu.setAll(orderTabSelect, payTabDefault, cookTabDefault);
                break;

            case PAY:
                menu.setAll(orderTabDefault, payTabSelect, cookTabDefault);
                break;

            case COOK:
                menu.setAll(orderTabDefault, payTabDefault, cookTabSelect);
                break;
        }
    }

    private int getStatusWeight(ProductEntry pe) {
        switch (pe.getStatus()) {
            case SENT:
                return sentWeight;
            case HOLD:
                return holdWeight;
            case VOID:
                return voidWeight;
            case REQUEST_SENT:
                return rSentWeight;
            case REQUEST_EDIT:
                return rEditWeight;
            case REQUEST_HOLD:
                return rHoldWeight;
            case REQUEST_VOID:
            case REQUEST_HOLD_VOID:
                return rVoidWeight;
            default:
                return 0;
        }
    }

    private void sortProductEntries(ObservableList<ProductEntry> entries) {
        entries.sort((pe1, pe2) -> {
            if (pe1.getStatus() != pe2.getStatus())
                return getStatusWeight(pe1) - getStatusWeight(pe2);
            else if (pe1.getMenuItem().getWeight() != pe2.getMenuItem().getWeight())
                return pe1.getMenuItem().getWeight() - pe2.getMenuItem().getWeight();
            else
                return ComparatorUtils.tagComparator.compare(pe1.getMenuItem().getTag(), pe2.getMenuItem().getTag());
        });
    }
}
