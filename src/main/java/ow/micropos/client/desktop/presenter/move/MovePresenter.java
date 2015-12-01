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
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.common.ViewProductEntry;
import ow.micropos.client.desktop.presenter.common.ViewSalesOrder;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovePresenter extends ItemPresenter<List<SalesOrder>> {

    @FXML ListView<ProductEntry> lvContext;
    @FXML GridView<SalesOrder> gvOrderGrid;

    @Override
    public void initialize() {

        mode.set(Mode.MOVE);

        gvOrderGrid.setPage(0);
        gvOrderGrid.setRows(App.properties.getInt("multi-rows"));
        gvOrderGrid.setCols(App.properties.getInt("multi-cols"));
        gvOrderGrid.setHorizontal(true);
        gvOrderGrid.setCellFactory(param -> {
            ViewSalesOrder presenter = Presenter.load("/view/common/view_sales_order.fxml");
            presenter.onAnyClick(event -> Platform.runLater(() -> {
                SalesOrder order = presenter.getItem();

                if (mode.get() == Mode.REMOVE) {
                    if (order.getId() != null || !order.getProductEntries().isEmpty() || !entriesContext.isEmpty())
                        App.warn.showAndWait("This order can not be removed.");
                    else
                        gvOrderGrid.getItems().remove(order);
                } else if (mode.get() == Mode.MOVE) {
                    if (orderContext.get() != order && !entriesContext.isEmpty()) {
                        order.getProductEntries().addAll(entriesContext.get());
                        entriesContext.get().clear();
                        orderContext.set(null);
                    }

                }
            }));
            presenter.onSubItemClick((sop, pep) -> Platform.runLater(() -> {
                if (mode.get() == Mode.MOVE) {
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
                }
            }));
            return presenter;
        });

        lvContext.setItems(entriesContext);
        lvContext.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/common/view_product_entry.fxml");
            presenter.fixWidth(lvContext);
            presenter.onClick(event -> Platform.runLater(() -> {
                if (mode.get() == Mode.MOVE) {
                    orderContext.get().getProductEntries().add(presenter.getItem());
                    entriesContext.remove(presenter.getItem());
                    if (entriesContext.isEmpty())
                        orderContext.set(null);
                }
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
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {

    }

    /*=======================================================================*
     =                                                                       =
     = Edit Context =
     =                                                                       =
     *=======================================================================*/

    private final ObjectProperty<SalesOrder> orderContext = new SimpleObjectProperty<>();

    private final ListProperty<ProductEntry> entriesContext = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    /*=======================================================================*
     =                                                                       =
     = Mode =
     =                                                                       =
     *=======================================================================*/

    private final ObjectProperty<Mode> mode = new SimpleObjectProperty<>();

    private static enum Mode {
        MOVE, REMOVE
    }

    /*=======================================================================*
     =                                                                       =
     = Menu =
     =                                                                       =
     *=======================================================================*/

    private final ObservableList<Action> menu = FXCollections.observableArrayList(

            new Action("Send", ActionType.FINISH, event -> {

                if (!entriesContext.isEmpty()) {
                    Platform.runLater(() -> App.warn.showAndWait("There are still unassigned entries."));

                } else if (App.apiIsBusy.compareAndSet(false, true)) {

                    List<SalesOrder> nonEmpty = getItem()
                            .stream()
                            .filter(so -> so.getId() != null || !so.getProductEntries().isEmpty())
                            .collect(Collectors.toList());

                    App.api.postSalesOrders(nonEmpty, (AlertCallback<List<Long>>) (longs, response) -> {
                        App.main.backRefresh();
                        App.warn.showAndWait("Sales Orders " + longs.toString());
                    });
                }
            }),

            new Action("Cancel", ActionType.FINISH, event -> Platform.runLater(App.main::backRefresh)),

            new Action("Move", ActionType.TAB_SELECT, event -> Platform.runLater(() -> mode.set(Mode.MOVE))),

            new Action("Remove", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> mode.set(Mode.REMOVE))),

            new Action("Add", ActionType.BUTTON, event -> Platform.runLater(() -> {
                SalesOrder salesOrder = SalesOrder.fromModel(App.employee, gvOrderGrid.getItems().get(0));
                gvOrderGrid.getItems().add(salesOrder);
            })),

            new Action("Prev Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::prevPage)),

            new Action("Next Pg", ActionType.BUTTON, event -> Platform.runLater(gvOrderGrid::nextPage))

    );

}
