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
import ow.micropos.client.desktop.model.report.ActiveSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.Calendar;

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
            new Action("Current Report", ActionType.FINISH, event -> {
                App.confirm.showAndWait("Generate Current Report?", () -> {
                    App.apiProxy.getCurrentReport(new RunLaterCallback<ActiveSalesReport>() {
                        @Override
                        public void laterSuccess(ActiveSalesReport activeSalesReport) {
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.report(activeSalesReport));
                        }
                    });
                });
            }),
            new Action("Day Report", ActionType.FINISH, event -> {
                Calendar c = Calendar.getInstance();
                App.confirm.showAndWait(
                        "Generate Day Report?",
                        () -> App.apiProxy.getDayReport(
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH),
                                new RunLaterCallback<DaySalesReport>() {
                                    @Override
                                    public void laterSuccess(DaySalesReport report) {
                                        App.dispatcher.requestPrint("receipt", App.jobBuilder.report(report));
                                    }
                                })
                );
            }),
            new Action("Migration", ActionType.FINISH, event -> {
                App.confirm.showAndWait(
                        "Migration can not be undone. Please verify all orders before continuing.",
                        () -> App.apiProxy.migrateSalesOrders(
                                new RunLaterCallback<Integer>() {
                                    @Override
                                    public void laterSuccess(Integer integer) {
                                        App.notify.showAndWait("Migrated " + integer + " Sales Orders.");
                                    }
                                }
                        )
                );
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
            new Action("Employees", ActionType.TAB_SELECT, event -> {
                App.main.nextRefresh(App.dbEmployeePresenter);
            }),
            new Action("Positions", ActionType.TAB_SELECT, event -> {
                App.main.nextRefresh(App.dbPositionPresenter);
            }),
            new Action("Sales Orders", ActionType.TAB_DEFAULT, event -> {
                App.confirm.showAndWait(
                        "WARNING - Database maintenance only. Continue?",
                        () -> App.main.nextRefresh(App.dbSalesOrderPresenter)
                );
            }),
            new Action("Properties", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
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
            new Action("Manager", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                    App.main.swapRefresh(App.managerPresenter))
            )
    );

}
