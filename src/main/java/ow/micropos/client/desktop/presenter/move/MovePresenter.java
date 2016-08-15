package ow.micropos.client.desktop.presenter.move;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.misc.Action;
import ow.micropos.client.desktop.misc.ActionType;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.common.ViewProductEntry;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MovePresenter extends ItemPresenter<List<SalesOrder>> {

    @FXML public StackPane doneOption;
    @FXML public StackPane cancelOption;
    @FXML public StackPane newOption;
    @FXML public StackPane spNext;
    @FXML public StackPane spBack;
    @FXML public ListView<ProductEntry> lvContext;
    @FXML public GridView<SalesOrder> gvOrderGrid;

    @Override
    public void initialize() {

        // New SalesOrders inherit from first SalesOrder.
        newOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (!gvOrderGrid.getItems().isEmpty()) {
                SalesOrder salesOrder = SalesOrder.fromModel(App.employee, gvOrderGrid.getItems().get(0));
                gvOrderGrid.getItems().add(salesOrder);
            } else {
                App.notify.showAndWait("No orders to split.");
            }
        }));

        doneOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (!moveEntryList.isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("There are still unassigned entries."));

            } else {

                List<SalesOrder> nonEmpty = getItem()
                        .stream()
                        .filter(so -> so.getId() != null || !so.getProductEntries().isEmpty())
                        .collect(Collectors.toList());

                App.apiProxy.postSalesOrders(nonEmpty, new RunLaterCallback<List<Long>>() {
                    @Override
                    public void laterSuccess(List<Long> longs) {
                        App.main.backToRefresh(2);
                        App.notify.showAndWait("Sales Orders " + longs.toString());
                    }
                });
            }
        }));

        cancelOption.setOnMouseClicked(event -> Platform.runLater(() -> App.main.backToRefresh(2)));

        spNext.setOnMouseClicked(event -> Platform.runLater(gvOrderGrid::nextPage));

        spBack.setOnMouseClicked(event -> Platform.runLater(gvOrderGrid::prevPage));

        gvOrderGrid.setPage(0);
        gvOrderGrid.setRows(App.properties.getInt("multi-rows"));
        gvOrderGrid.setCols(App.properties.getInt("multi-cols"));
        gvOrderGrid.setHorizontal(true);
        gvOrderGrid.setCellFactory(param -> {
            ViewSalesOrder presenter = Presenter.load("/view/common/view_sales_order.fxml");
            presenter.onTopClick(event -> tapSalesOrder(presenter.getItem()));
            presenter.onSubItemClick((so, pe) -> tapProductEntry(so.getItem(), pe.getItem()));
            return presenter;
        });

        lvContext.setItems(moveEntryList);
        lvContext.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/common/view_product_entry.fxml");
            presenter.fixWidth(lvContext);
            presenter.onClick(event -> tapContextEntry(presenter.getItem()));
            return new PresenterCellAdapter<>(presenter);
        });
    }

    @Override
    protected void updateItem(List<SalesOrder> currentItem, List<SalesOrder> newItem) {
    }

    @Override
    public void refresh() {
        gvOrderGrid.setPage(0);
        gvOrderGrid.setItems(FXCollections.emptyObservableList());
        moveEntryList.get().clear();

        Platform.runLater(() -> gvOrderGrid.setItems(FXCollections.observableList(getItem())));
    }

    @Override
    public void dispose() {

    }

    /*=======================================================================*
     =                                                                       =
     = Edit Context
     =                                                                       =
     *=======================================================================*/

    private final ListProperty<ProductEntry> moveEntryList = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    private final HashMap<ProductEntry, SalesOrder> moveEntryContext = new HashMap<>();

    private void tapProductEntry(SalesOrder salesOrder, ProductEntry productEntry) {
        if (salesOrder == null || productEntry == null)
            return;

        Platform.runLater(() -> {
            ProductEntry pe = productEntry.tryExtract(BigDecimal.ONE);

            if (pe != null) {
                moveEntryContext.put(pe, salesOrder);
                pe.mergeInto(moveEntryList);

            } else {
                moveEntryContext.put(productEntry, salesOrder);
                salesOrder.getProductEntries().remove(productEntry);
                productEntry.mergeInto(moveEntryList);
            }
        });
    }

    private void tapSalesOrder(SalesOrder salesOrder) {
        if (salesOrder == null)
            return;

        Platform.runLater(() -> {
            moveEntryList.forEach(pe -> pe.mergeInto(salesOrder.getProductEntries()));
            moveEntryList.get().clear();
            moveEntryContext.clear();
        });
    }

    private void tapContextEntry(ProductEntry productEntry) {
        if (productEntry == null)
            return;

        Platform.runLater(() -> {
            SalesOrder salesOrder = moveEntryContext.get(productEntry);
            productEntry.mergeInto(salesOrder.getProductEntries());
            moveEntryList.remove(productEntry);
            moveEntryContext.remove(productEntry);
        });
    }

    /*=======================================================================*
     =                                                                       =
     = Menu
     =                                                                       =
     *=======================================================================*/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("View", ActionType.TAB_DEFAULT, event -> Platform.runLater(App.main::backRefresh)),
            new Action("Split", ActionType.TAB_SELECT, event -> {})
    );

}
