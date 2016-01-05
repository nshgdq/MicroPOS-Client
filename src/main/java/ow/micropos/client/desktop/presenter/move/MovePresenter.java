package ow.micropos.client.desktop.presenter.move;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.common.ViewProductEntry;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;

import java.util.ArrayList;
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

        // Adding an order is based off the first sales order.
        newOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            SalesOrder salesOrder = SalesOrder.fromModel(App.employee, gvOrderGrid.getItems().get(0));
            gvOrderGrid.getItems().add(salesOrder);
        }));

        doneOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (!entriesContext.isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("There are still unassigned entries."));

            } else if (App.apiIsBusy.compareAndSet(false, true)) {

                List<SalesOrder> nonEmpty = getItem()
                        .stream()
                        .filter(so -> so.getId() != null || !so.getProductEntries().isEmpty())
                        .map(so -> {
                            // Void empty sales orders
                            if (so.getId() != null && so.getProductEntries().isEmpty())
                                so.setStatus(SalesOrderStatus.REQUEST_VOID);
                            return so;
                        })
                        .collect(Collectors.toList());

                App.api.postSalesOrders(nonEmpty, (AlertCallback<List<Long>>) (longs, response) -> {
                    App.main.backToRefresh(2);
                    App.notify.showAndWait("Sales Orders " + longs.toString());
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
            presenter.onAnyClick(event -> Platform.runLater(() -> {
                SalesOrder order = presenter.getItem();
                if (orderContext.get() != order && !entriesContext.isEmpty()) {
                    order.getProductEntries().addAll(entriesContext.get());
                    entriesContext.get().clear();
                    orderContext.set(null);
                }
            }));
            presenter.onSubItemClick((sop, pep) -> Platform.runLater(() -> {
                SalesOrder order = sop.getItem();
                ProductEntry entry = pep.getItem();
                if (orderContext.get() == null) {
                    orderContext.set(order);
                    entriesContext.add(entry);
                    order.getProductEntries().remove(entry);
                } else if (orderContext.get() == order) {
                    entriesContext.add(entry);
                    order.getProductEntries().remove(entry);
                }

            }));
            return presenter;
        });

        lvContext.setItems(entriesContext);
        lvContext.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/common/view_product_entry.fxml");
            presenter.fixWidth(lvContext);
            presenter.onClick(event -> Platform.runLater(() -> {
                orderContext.get().getProductEntries().add(presenter.getItem());
                entriesContext.remove(presenter.getItem());
                if (entriesContext.isEmpty())
                    orderContext.set(null);
            }));
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
        orderContext.set(null);
        entriesContext.get().clear();

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

    private final ObjectProperty<SalesOrder> orderContext = new SimpleObjectProperty<>();

    private final ListProperty<ProductEntry> entriesContext = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

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
