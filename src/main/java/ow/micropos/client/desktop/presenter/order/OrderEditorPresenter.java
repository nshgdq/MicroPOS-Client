package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.List;

public class OrderEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML StackPane spItems;
    @FXML GridView<Category> gvCategories;
    @FXML GridView<MenuItem> gvMenuItems;
    @FXML ListView<ProductEntry> orderEntries;

    @FXML Label employee;
    @FXML Label target;
    @FXML Label date;
    @FXML Label productTotal;
    @FXML Label chargeTotal;
    @FXML Label gratuityTotal;
    @FXML Label taxTotal;
    @FXML Label grandTotal;

    @FXML
    public void initialize() {

        spItems.getChildren().clear();

        GridPane.setHalignment(productTotal, HPos.RIGHT);
        GridPane.setHalignment(chargeTotal, HPos.RIGHT);
        GridPane.setHalignment(gratuityTotal, HPos.RIGHT);
        GridPane.setHalignment(taxTotal, HPos.RIGHT);
        GridPane.setHalignment(grandTotal, HPos.RIGHT);

        gvCategories.setPage(0);
        gvCategories.setRows(App.properties.getInt("order-category-rows"));
        gvCategories.setCols(App.properties.getInt("order-category-cols"));
        gvCategories.setHorizontal(true);
        gvCategories.setCellFactory(param -> {
            ViewCategory presenter = Presenter.load("/view/order/view_category.fxml");
            presenter.onClick(event -> Platform.runLater(() -> showMenuItems(presenter.getItem())));
            return presenter;
        });

        gvMenuItems.setPage(0);
        gvMenuItems.setRows(App.properties.getInt("order-menu-item-rows"));
        gvMenuItems.setCols(App.properties.getInt("order-menu-item-cols"));
        gvMenuItems.setHorizontal(true);
        gvMenuItems.setCellFactory(param -> {
            ViewMenuItem presenter = Presenter.load("/view/order/view_menu_item.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                getItem().addMenuItem(presenter.getItem());
                int lastIdx = getItem().productEntriesProperty().size() - 1;
                orderEntries.scrollTo(lastIdx);
            }));
            return presenter;
        });

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
            unsetLabel(target);
            unsetLabel(employee);
            unsetLabel(date);
            unsetLabel(productTotal);
            unsetLabel(chargeTotal);
            unsetLabel(gratuityTotal);
            unsetLabel(taxTotal);
            unsetLabel(grandTotal);
            unsetListView(orderEntries);
        } else {
            setLabel(employee, Bindings.concat("Employee : ", newItem.employeeNameProperty()));
            setLabel(target, Bindings.concat("Customer : ", newItem.targetNameProperty()));
            setLabel(date, newItem.prettyTimeProperty());
            setLabel(productTotal, newItem.productTotalProperty().asString());
            setLabel(chargeTotal, newItem.chargeTotalProperty().asString());
            setLabel(grandTotal, newItem.grandTotalProperty().asString());
            setLabel(gratuityTotal, newItem.gratuityTotalProperty().asString());
            setLabel(taxTotal, newItem.taxTotalProperty().asString());
            setListView(orderEntries, newItem.productEntriesProperty());
            Platform.runLater(() -> {
                if (newItem.canHaveGratuity()) {
                    if (newItem.hasGratuity())
                        disableGratuity();
                    else
                        enableGratuity();
                }
            });
        }
    }

    @Override
    public void refresh() {
        gvCategories.setItems(FXCollections.emptyObservableList());
        gvMenuItems.setItems(FXCollections.emptyObservableList());

        App.api.getCategories(new AlertCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories, Response response) {
                gvCategories.setItems(FXCollections.observableList(categories));
                showCategories();
            }
        });
    }

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {
        unsetLabel(target);
        unsetLabel(employee);
        unsetLabel(date);
        unsetLabel(productTotal);
        unsetLabel(grandTotal);
        unsetLabel(taxTotal);
        unsetLabel(gratuityTotal);
        unsetListView(orderEntries);
    }

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    private final Action back = new Action("Back", ActionType.BUTTON, event -> Platform.runLater(this::showCategories));
    private final Action gratuityOn = new Action("Add Grat", ActionType.BUTTON, event -> Platform.runLater(this::enableGratuity));
    private final Action gratuityOff = new Action("No Grat", ActionType.BUTTON, event -> Platform.runLater(this::disableGratuity));

    private final ObservableList<Action> menu = FXCollections.observableArrayList(

            new Action("Send", ActionType.FINISH, event -> {
                if (getItem().getProductEntries().isEmpty()) {
                    Platform.runLater(() -> App.warn.showAndWait("Nothing to send."));
                } else {
                    App.api.postSalesOrder(getItem(), (AlertCallback<Long>) (aLong, response) -> {
                        Platform.runLater(() -> {
                            App.main.backRefresh();
                            App.warn.showAndWait("Sales Order " + aLong);
                        });
                    });
                }
            }),
            new Action("Cancel", ActionType.FINISH, event -> Platform.runLater(App.main::backRefresh)),
            new Action("Order", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.orderEditorPresenter, getItem())
            )),
            new Action("Pay", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.paymentEditorPresenter, getItem())
            )),
            new Action("Print", ActionType.BUTTON, event -> Platform.runLater(() ->
                            App.printer.printCheck(getItem())
            ))
    );

    private void showCategories() {
        menu.remove(back);
        gvMenuItems.setItems(FXCollections.emptyObservableList());
        spItems.getChildren().setAll(gvCategories);
    }

    private void showMenuItems(Category category) {
        menu.add(back);
        gvMenuItems.setItems(category.getMenuItems());
        spItems.getChildren().setAll(gvMenuItems);
    }

    private void enableGratuity() {
        menu.remove(gratuityOn);
        if (!menu.contains(gratuityOff))
            menu.add(4, gratuityOff);

        getItem().setGratuityPercent(App.properties.getBd("gratuity-percent"));
    }

    private void disableGratuity() {
        menu.remove(gratuityOff);
        if (!menu.contains(gratuityOn))
            menu.add(4, gratuityOn);

        getItem().setGratuityPercent(BigDecimal.ZERO);
    }

}
