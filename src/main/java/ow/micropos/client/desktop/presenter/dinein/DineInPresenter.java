package ow.micropos.client.desktop.presenter.dinein;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.model.target.Section;

import java.math.BigDecimal;
import java.util.List;

public class DineInPresenter extends Presenter {

    @FXML GridView<Seat> gvRestaurantLayout;

    @FXML
    public void initialize() {
        gvRestaurantLayout.setPage(0);
        gvRestaurantLayout.setPageResolver(s -> 0);
        gvRestaurantLayout.setRowResolver(Seat::getRow);
        gvRestaurantLayout.setColResolver(Seat::getCol);
        gvRestaurantLayout.setCellFactory(param -> {
            ViewSeat presenter = Presenter.load("/view/dinein/view_seat.fxml");
            presenter.onClick(event -> {
                List<SalesOrder> orders = presenter.getItem().getSalesOrders();
                if (orders.size() == 0) {
                    App.main.setNextRefresh(
                            App.orderEditorPresenter,
                            SalesOrder.forSeat(
                                    App.employee,
                                    presenter.getItem(),
                                    App.properties.getBd("tax-percent"),
                                    BigDecimal.ZERO
                            )
                    );
                } else {
                    App.targetPresenter.setItem(presenter.getItem());
                    App.main.nextRefresh(App.targetPresenter);
                }
            });
            return presenter;
        });
    }

    @Override
    public void refresh() {
        gvRestaurantLayout.setItems(FXCollections.emptyObservableList());

        // Oriental Wok does not use sections feature, so all seats belong to one section.
        long id = App.properties.getLng("default-section");

        App.api.getSection(id, (AlertCallback<Section>) (section, response) -> {
            gvRestaurantLayout.setRows(section.getRows());
            gvRestaurantLayout.setCols(section.getCols());
            gvRestaurantLayout.setItems(section.getSeats());
        });
    }

    @Override
    public void dispose() {
        gvRestaurantLayout.getChildren().clear();
    }

    /*****************************************************************************
     *                                                                           *
     * Menu                                                                      *
     *                                                                           *
     *****************************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Dine In", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.dineInPresenter))
            ),

            new Action("Take Out", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.takeOutPresenter))
            ),

            new Action("Finder", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.finderPresenter))
            ),

            new Action("Manager", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.managerPresenter))
            )
    );

}
