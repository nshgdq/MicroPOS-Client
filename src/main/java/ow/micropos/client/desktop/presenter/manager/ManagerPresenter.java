package ow.micropos.client.desktop.presenter.manager;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionLabel;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.service.RunLaterCallback;

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

            new Action("Customers", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbCustomerPresenter);
            })),
            new Action("Menu Items", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbMenuItemsPresenter);
            })),
            new Action("Categories", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbCategoriesPresenter);
            })),
            new Action("Modifiers", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbModifierPresenter);
            })),
            new Action("Modifier Groups", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbModifierGroupPresenter);
            })),
            new Action("Sections", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbSectionPresenter);
            })),
            new Action("Seats", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbSeatPresenter);
            })),
            new Action("Charges", ActionType.BUTTON, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.dbChargePresenter);
            })),
            new Action("Employees", ActionType.TAB_DEFAULT, event -> {
                App.main.nextRefresh(App.dbEmployeePresenter);
            }),
            new Action("Sales Orders", ActionType.TAB_DEFAULT, event -> {
                App.confirm.showAndWait(
                        "WARNING - Database maintenance only. Continue?",
                        () -> App.main.nextRefresh(App.dbSalesOrderPresenter)
                );
            }),
            new Action("Time Cards", ActionType.TAB_DEFAULT, event -> {
                App.main.nextRefresh(App.dbTimeCardPresenter);
            }),
            new Action("Report", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
                App.main.nextRefresh(App.reportPresenter);
            })),
            new Action("Migration", ActionType.TAB_DEFAULT,
                    // Warning 1
                    event -> App.confirm.showAndWait(
                            "Migrated orders will no longer be accessible. Only viewed as records.",

                            // Warning 2
                            () -> App.confirm.showAndWait(
                                    "This can NOT be undone. If there are no more open orders, continue...",

                                    // Migration
                                    () -> App.apiProxy.migrateSalesOrders(
                                            new RunLaterCallback<Integer>() {
                                                @Override
                                                public void laterSuccess(Integer integer) {
                                                    App.notify.showAndWait("Migrated " + integer + " Sales Orders.");
                                                }
                                            }
                                    )
                            )
                    )
            ),
            new Action("Shutdown", ActionType.TAB_DEFAULT, event -> {
                App.confirm.showAndWait("Exit Application?", App::exit);
            }),
            new Action("Positions", ActionType.FINISH, event -> {
                App.main.nextRefresh(App.dbPositionPresenter);
            }),
            new Action("Properties", ActionType.FINISH, event -> Platform.runLater(() -> {
                App.confirm.showAndWait(
                        "WARNING - Database maintenance only. Continue?",
                        () -> App.main.nextRefresh(App.dbPropertyPresenter)
                );
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
            new Action("Manage", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.managerPresenter))
            )
    );

}
