package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.List;


public class MenuPresenter extends ItemPresenter<SalesOrder> {

    @FXML public StackPane categoriesOption;
    @FXML public StackPane sendOption;
    @FXML public StackPane backOption;
    @FXML public StackPane gratuityOption;
    @FXML public StackPane printOption;
    @FXML public StackPane cancelOption;
    @FXML public StackPane nextOption;
    @FXML public StackPane spItems;
    @FXML public GridView<Category> gvCategories;
    @FXML public GridView<MenuItem> gvMenuItems;

    @Override
    public void initialize() {

        categoriesOption.setOnMouseClicked(event -> Platform.runLater(this::showCategories));

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
                        getItem().setId(aLong);
                        App.main.backRefresh();

                        if (App.properties.getBool("print-send-takeout") && getItem().hasType(SalesOrderType.TAKEOUT))
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));

                        if (App.properties.getBool("print-send-dinein") && getItem().hasType(SalesOrderType.DINEIN))
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));

                        App.notify.showAndWait("Sales Order " + aLong);
                    }
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

        printOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (getItem().canPrint()) {
                App.main.backRefresh();
                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));
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
                ProductEntry pe = getItem().addMenuItem(presenter.getItem());
                App.orderEditorPresenter.scrollToEntry(pe);
            }));
            return presenter;
        });

    }

    @Override
    public void refresh() {
        gvCategories.setItems(FXCollections.emptyObservableList());
        gvMenuItems.setItems(FXCollections.emptyObservableList());

        App.apiProxy.getCategories(new RunLaterCallback<List<Category>>() {
            @Override
            public void laterSuccess(List<Category> categories) {
                categories.sort((o1, o2) -> {
                    if (o1.getWeight() != o2.getWeight())
                        return o1.getWeight() - o2.getWeight();
                    else
                        return ComparatorUtils.tagComparator.compare(o1.getTag(), o2.getTag());
                });
                categories.forEach(c -> c.getMenuItems().sort((o1, o2) -> {
                    if (o1.getWeight() != o2.getWeight())
                        return o1.getWeight() - o2.getWeight();
                    else
                        return ComparatorUtils.tagComparator.compare(o1.getTag(), o2.getTag());
                }));
                gvCategories.setItems(FXCollections.observableList(categories));
                showCategories();
            }
        });
    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {

    }

    @Override
    public void dispose() {

    }

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


}
