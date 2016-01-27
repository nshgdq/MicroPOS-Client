package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
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
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class OrderEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML public StackPane categoriesOption;
    @FXML public StackPane sendOption;
    @FXML public StackPane backOption;
    @FXML public StackPane cancelOption;
    @FXML public StackPane printOption;
    @FXML public StackPane nextOption;
    @FXML public Label time;
    @FXML public StackPane spItems;
    @FXML public GridView<Category> gvCategories;
    @FXML public GridView<MenuItem> gvMenuItems;
    @FXML public ListView<ProductEntry> orderEntries;

    @FXML public StackPane upOption;
    @FXML public StackPane downOption;

    @FXML public Label employee;
    @FXML public Label target;
    @FXML public Label info;
    @FXML public Label status;
    @FXML public Label productTotal;
    @FXML public Label chargeTotal;
    @FXML public Label gratuityTotal;
    @FXML public Label taxTotal;
    @FXML public Label grandTotal;

    private int sentWeight;
    private int holdWeight;
    private int voidWeight;
    private int rSentWeight;
    private int rEditWeight;
    private int rHoldWeight;
    private int rVoidWeight;

    @FXML
    public void initialize() {

        // TODO : Not sure if we should have print option here.
        printOption.setVisible(false);

        sentWeight = App.properties.getInt("pe-status-weight-sent");
        holdWeight = App.properties.getInt("pe-status-weight-hold");
        voidWeight = App.properties.getInt("pe-status-weight-void");
        rSentWeight = App.properties.getInt("pe-status-weight-request-sent");
        rEditWeight = App.properties.getInt("pe-status-weight-request-edit");
        rHoldWeight = App.properties.getInt("pe-status-weight-request-hold");
        rVoidWeight = App.properties.getInt("pe-status-weight-request-void");

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

        categoriesOption.setOnMouseClicked(event -> Platform.runLater(this::showCategories));

        upOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, -2))
        );

        downOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, 2))
        );

        backOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (spItems.getChildren().contains(gvCategories)) {
                gvCategories.prevPage();
            } else if (spItems.getChildren().contains(gvMenuItems)) {
                gvMenuItems.prevPage();
            }
        }));

        nextOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (spItems.getChildren().contains(gvCategories)) {
                gvCategories.nextPage();
            } else if (spItems.getChildren().contains(gvMenuItems)) {
                gvMenuItems.nextPage();
            }
        }));

        cancelOption.setOnMouseClicked(
                event -> App.confirm.showAndWait("Are you sure? Any changes will not be saved.", App.main::backRefresh)
        );

        sendOption.setOnMouseClicked(event -> {
            if (getItem().getProductEntries().isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("Nothing to send."));
            } else {
                App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                    @Override
                    public void laterSuccess(Long aLong) {
                        App.main.backRefresh();
                        App.notify.showAndWait("Sales Order " + aLong);
                    }
                });
            }
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
                sortProductEntries(orderEntries.getItems());
            }));
            return presenter;
        });

        orderEntries.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/order/view_product_entry.fxml");
            presenter.fixWidth(orderEntries);
            presenter.onClick(event -> {
                if (presenter.getItem().getStatus() != ProductEntryStatus.VOID
                        && presenter.getItem().getStatus() != ProductEntryStatus.REQUEST_VOID
                        && presenter.getItem().getStatus() != ProductEntryStatus.REQUEST_HOLD_VOID) {
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
            setListView(orderEntries, newItem.getProductEntries());

            sortProductEntries(orderEntries.getItems());
        }
    }

    @Override
    public void refresh() {
        sortProductEntries(orderEntries.getItems());
        gvCategories.setItems(FXCollections.emptyObservableList());
        gvMenuItems.setItems(FXCollections.emptyObservableList());

        App.apiProxy.getCategories(new RunLaterCallback<List<Category>>() {
            @Override
            public void laterSuccess(List<Category> categories) {
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
        gvMenuItems.setPage(0);
        spItems.getChildren().setAll(gvCategories);
    }

    private void showMenuItems(Category category) {
        gvMenuItems.setItems(category.getMenuItems());
        gvMenuItems.setPage(0);
        spItems.getChildren().setAll(gvMenuItems);
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
            if (pe1.getStatus() == pe2.getStatus()) {
                return pe1.getMenuItem().getWeight() - pe2.getMenuItem().getWeight();
            } else {
                return getStatusWeight(pe1) - getStatusWeight(pe2);
            }
        });
    }

}
