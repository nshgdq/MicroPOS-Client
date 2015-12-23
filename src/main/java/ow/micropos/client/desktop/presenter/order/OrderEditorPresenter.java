package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
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
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.List;

public class OrderEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML StackPane sendOption;
    @FXML StackPane backButton;
    @FXML StackPane gratuityOption;
    @FXML StackPane voidOption;
    @FXML StackPane cancelOption;
    @FXML StackPane printOption;
    @FXML StackPane nextButton;
    @FXML Label time;
    @FXML StackPane spItems;
    @FXML GridView<Category> gvCategories;
    @FXML GridView<MenuItem> gvMenuItems;
    @FXML ListView<ProductEntry> orderEntries;

    @FXML Label employee;
    @FXML Label target;
    @FXML Label info;
    @FXML Label status;
    @FXML Label productTotal;
    @FXML Label chargeTotal;
    @FXML Label gratuityTotal;
    @FXML Label taxTotal;
    @FXML Label grandTotal;

    @FXML
    public void initialize() {

        spItems.getChildren().clear();

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

        backButton.setOnMouseClicked(event -> Platform.runLater(this::showCategories));

        cancelOption.setOnMouseClicked(
                event -> App.confirm.showAndWait("Are you sure? Any changes will not be saved.", App.main::backRefresh)
        );

        sendOption.setOnMouseClicked(event -> {
            if (getItem().getProductEntries().isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("Nothing to send."));
            } else if (App.apiIsBusy.compareAndSet(false, true)) {
                App.api.postSalesOrder(getItem(), (AlertCallback<Long>) (aLong, response) -> {
                    Platform.runLater(() -> {
                        App.main.backRefresh();
                        App.notify.showAndWait("Sales Order " + aLong);
                    });
                });
            }
        });

        gratuityOption.setOnMouseClicked(event -> {
            if (getItem().canHaveGratuity() && !getItem().hasGratuity()) {
                getItem().setGratuityPercent(App.properties.getBd("gratuity-percent"));
            } else {
                getItem().setGratuityPercent(BigDecimal.ZERO);
            }
        });

        voidOption.setOnMouseClicked(event -> {
            App.confirm.showAndWait("Void Sales Order?", () -> {
                if (App.apiIsBusy.compareAndSet(false, true)) {

                    SalesOrderStatus prevStatus = getItem().getStatus();
                    getItem().setStatus(SalesOrderStatus.REQUEST_VOID);

                    App.api.postSalesOrder(getItem(), new AlertCallback<Long>() {
                        @Override
                        public void onSuccess(Long aLong, Response response) {
                            Platform.runLater(() -> {
                                App.main.backRefresh();
                                App.notify.showAndWait("Sales Order " + aLong + " Voided.");
                            });
                        }

                        @Override
                        public void onFailure(RetrofitError error) {
                            getItem().setStatus(prevStatus);
                        }
                    });
                }
            });
        });

        printOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (getItem().canPrint()) {
                App.main.backRefresh();
                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem()));
            } else {
                App.notify.showAndWait("Changes must be sent before printing.");
            }
        }));

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
            setLabel(time, newItem.prettyTimeProperty());
            setLabel(productTotal, newItem.productTotalProperty().asString());
            setLabel(chargeTotal, newItem.chargeTotalProperty().asString());
            setLabel(grandTotal, newItem.grandTotalProperty().asString());
            setLabel(gratuityTotal, newItem.gratuityTotalProperty().asString());
            setLabel(taxTotal, newItem.taxTotalProperty().asString());
            setListView(orderEntries, newItem.productEntriesProperty());

        }
    }

    @Override
    public void refresh() {
        gvCategories.setItems(FXCollections.emptyObservableList());
        gvMenuItems.setItems(FXCollections.emptyObservableList());

        App.api.getCategories(new AlertCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories, Response response) {
                categories.sort((o1, o2) -> o1.getWeight() - o2.getWeight());
                categories.forEach(c -> c.getMenuItems().sort((o1, o2) -> o1.getWeight() - o2.getWeight()));
                gvCategories.setItems(FXCollections.observableList(categories));
                showCategories();
            }
        });
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

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Order", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.orderEditorPresenter, getItem())
            )),
            new Action("Pay", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.paymentEditorPresenter, getItem())
            ))
    );

    private void showCategories() {
        gvMenuItems.setItems(FXCollections.emptyObservableList());
        spItems.getChildren().setAll(gvCategories);
    }

    private void showMenuItems(Category category) {
        gvMenuItems.setItems(category.getMenuItems());
        spItems.getChildren().setAll(gvMenuItems);
    }

}
