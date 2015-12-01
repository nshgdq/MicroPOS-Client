package ow.micropos.client.desktop.presenter.manager;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionLabel;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class ManagerPresenter extends Presenter {

    @FXML GridView<Action> gvManagerLayout;

    @FXML
    public void initialize() {

        gvManagerLayout.setPage(0);
        gvManagerLayout.setCols(App.properties.getInt("manager-cols"));
        gvManagerLayout.setRows(App.properties.getInt("manager-rows"));
        gvManagerLayout.setCellFactory(params -> new ActionLabel());
        gvManagerLayout.setItems(managerMenu);

    }

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {

    }

    /******************************************************************
     *                                                                *
     * Manager Menu
     *                                                                *
     ******************************************************************/

    private final ObservableList<Action> managerMenu = FXCollections.observableArrayList(
            new Action("Shutdown", ActionType.BUTTON, event -> Platform.runLater(App::exit)),
            new Action("Migration", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (App.apiIsBusy.compareAndSet(false, true)) {
                    App.api.migrateSalesOrders(new AlertCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer aInt, Response response) {
                            App.warn.showAndWait("Migrated " + aInt + " Sales Orders.");
                        }
                    });
                }
            })),
            new Action("Close Unpaid", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (App.apiIsBusy.compareAndSet(false, true)) {
                    App.api.closeUnpaidSalesOrders(new AlertCallback<List<Long>>() {
                        @Override
                        public void onSuccess(List<Long> longs, Response response) {
                            App.warn.showAndWait("Closed Orders : " + longs.toString());
                        }
                    });
                }
            }))
    );

    /******************************************************************
     *                                                                *
     * Top Bar Menu
     *                                                                *
     ******************************************************************/

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Dine In", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.dineInPresenter))
            ),
            new Action("Take Out", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.takeOutPresenter))
            ),
            new Action("Finder", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.finderPresenter))
            ),
            new Action("Manager", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.managerPresenter))
            )
    );

}
