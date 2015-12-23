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
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.report.CurrentSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import retrofit.client.Response;

import java.util.Calendar;
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

            new Action("Shutdown", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Exit Application?", App::exit);
            }),

            new Action("Migration", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Perform Migration?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        App.api.migrateSalesOrders(new AlertCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer aInt, Response response) {
                                App.notify.showAndWait("Migrated " + aInt + " Sales Orders.");
                            }
                        });
                    }
                });
            }),
            new Action("Close Unpaid", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Close All Unpaid Orders?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        App.api.closeUnpaidSalesOrders(new AlertCallback<List<Long>>() {
                            @Override
                            public void onSuccess(List<Long> longs, Response response) {
                                App.notify.showAndWait("Closed Orders : " + longs.toString());
                            }
                        });
                    }
                });
            }),
            new Action("Current Report", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Generate Current Report?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        App.api.getCurrentReport(new AlertCallback<CurrentSalesReport>() {
                            @Override
                            public void onSuccess(CurrentSalesReport report, Response response) {
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.report(report));
                            }
                        });
                    }
                });
            }),
            new Action("Day Report", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Generate Day Report?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        Calendar c = Calendar.getInstance();
                        App.api.getDayReport(
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH),
                                new AlertCallback<DaySalesReport>() {
                                    @Override
                                    public void onSuccess(DaySalesReport report, Response response) {
                                        App.dispatcher.requestPrint("receipt", App.jobBuilder.report(report));
                                    }
                                });
                    }
                });
            }),
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
            new Action("Sales Orders", ActionType.BUTTON, event -> {
                App.confirm.showAndWait(
                        "WARNING - Database maintenance only. Continue?",
                        () -> App.main.nextRefresh(App.dbSalesOrderPresenter)
                );
            })
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
