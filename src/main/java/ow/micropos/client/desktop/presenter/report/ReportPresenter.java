package ow.micropos.client.desktop.presenter.report;

import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.comtel2000.keyboard.control.VkProperties;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.report.MonthlySalesReport;
import ow.micropos.client.desktop.model.report.SalesReport;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.Arrays;
import java.util.Calendar;

public class ReportPresenter extends Presenter {

    public StackPane generateActiveReport;

    public StackPane generateReport;
    public StackPane clearReport;
    public TextField tfDay1;
    public TextField tfMonth1;
    public TextField tfYear1;
    public TextField tfDay2;
    public TextField tfMonth2;
    public TextField tfYear2;
    public ComboBox<SalesOrderStatus> cbStatus;
    public ComboBox<SalesOrderType> cbType;

    public TextField tfMonth;
    public TextField tfYear;
    public StackPane clearMonthlyReport;
    public StackPane generateMonthlyReport;


    @Override
    public void initialize() {
        super.initialize();

        tfDay1.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfDay2.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        tfMonth.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfMonth1.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfMonth2.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        tfYear.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfYear1.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        tfYear2.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        cbStatus.setItems(FXCollections.observableList(Arrays.asList(null, SalesOrderStatus.OPEN, SalesOrderStatus.CLOSED, SalesOrderStatus.VOID)));
        cbType.setItems(FXCollections.observableList(Arrays.asList(null, SalesOrderType.DINEIN, SalesOrderType.TAKEOUT)));

        clearReport.setOnMouseClicked(event -> {
            cbStatus.getSelectionModel().select(SalesOrderStatus.CLOSED);
            cbType.getSelectionModel().clearSelection();

            Calendar c = Calendar.getInstance();

            tfDay1.setText(c.get(Calendar.DATE) + "");
            tfDay2.setText(c.get(Calendar.DATE) + "");

            tfMonth1.setText(c.get(Calendar.MONTH) + 1 + "");
            tfMonth2.setText(c.get(Calendar.MONTH) + 1 + "");

            tfYear1.setText(c.get(Calendar.YEAR) + "");
            tfYear2.setText(c.get(Calendar.YEAR) + "");
        });

        generateReport.setOnMouseClicked(event -> Platform.runLater(() -> {

            if (!tfDay1.getText().matches("[1-9][0-9]?") || !tfDay2.getText().matches("[1-9][0-9]?")) {
                App.notify.showAndWait("Please select a day.");

            } else if (!tfMonth1.getText().matches("[1-9][0-2]?") || !tfMonth2.getText().matches("[1-9][0-2]?")) {
                App.notify.showAndWait("Please select a month.");

            } else if (!tfYear1.getText().matches("[0-9]{4}") || !tfYear2.getText().matches("[0-9]{4}")) {
                App.notify.showAndWait("Invalid year.");

            } else {

                int year1 = Integer.parseInt(tfYear1.getText());
                int month1 = Integer.parseInt(tfMonth1.getText()) - 1;
                int day1 = Integer.parseInt(tfDay1.getText());

                int year2 = Integer.parseInt(tfYear2.getText());
                int month2 = Integer.parseInt(tfMonth2.getText()) - 1;
                int day2 = Integer.parseInt(tfDay2.getText());

                Calendar c1 = Calendar.getInstance();
                c1.set(year1, month1, day1, 0, 0, 0);

                Calendar c2 = Calendar.getInstance();
                c2.set(year2, month2, day2, 0, 0, 0);
                c2.add(Calendar.DATE, 1);

                App.apiProxy.getSalesReport(
                        c1.getTime(),
                        c2.getTime(),
                        cbStatus.getValue(),
                        cbType.getValue(),
                        new RunLaterCallback<SalesReport>() {
                            @Override
                            public void laterSuccess(SalesReport salesReport) {
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.salesReport(salesReport));
                            }
                        }
                );
            }
        }));


        clearMonthlyReport.setOnMouseClicked(event -> {
            Calendar c = Calendar.getInstance();
            tfMonth.setText(c.get(Calendar.MONTH) + 1 + "");
            tfYear.setText(c.get(Calendar.YEAR) + "");
        });

        generateMonthlyReport.setOnMouseClicked(event -> Platform.runLater(() -> {

            if (!tfMonth.getText().matches("[1-9][0-2]?")) {
                App.notify.showAndWait("Please select a month.");

            } else if (!tfYear.getText().matches("[0-9]{4}")) {
                App.notify.showAndWait("Invalid year.");

            } else {

                int year = Integer.parseInt(tfYear.getText());
                int month = Integer.parseInt(tfMonth.getText()) - 1;

                Calendar c = Calendar.getInstance();
                c.set(year, month, 1);

                App.apiProxy.getMonthlySalesReport(
                        c.getTime(),
                        new RunLaterCallback<MonthlySalesReport>() {
                            @Override
                            public void laterSuccess(MonthlySalesReport monthlySalesReport) {
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.monthlySalesReport(monthlySalesReport));
                            }
                        }
                );
            }
        }));

        generateActiveReport.setOnMouseClicked(event -> Platform.runLater(() ->
                App.apiProxy.getSalesReport(
                        null,
                        null,
                        cbStatus.getValue(),
                        cbType.getValue(),
                        new RunLaterCallback<SalesReport>() {
                            @Override
                            public void laterSuccess(SalesReport salesReport) {
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.salesReport(salesReport));
                            }
                        }
                )));


    }

    @Override
    public void refresh() {
        cbStatus.getSelectionModel().select(SalesOrderStatus.CLOSED);
        cbType.getSelectionModel().clearSelection();

        Calendar c = Calendar.getInstance();

        tfDay1.setText(c.get(Calendar.DATE) + "");
        tfDay2.setText(c.get(Calendar.DATE) + "");

        tfMonth.setText(c.get(Calendar.MONTH) + 1 + "");
        tfMonth1.setText(c.get(Calendar.MONTH) + 1 + "");
        tfMonth2.setText(c.get(Calendar.MONTH) + 1 + "");

        tfYear.setText(c.get(Calendar.YEAR) + "");
        tfYear1.setText(c.get(Calendar.YEAR) + "");
        tfYear2.setText(c.get(Calendar.YEAR) + "");
    }

    @Override
    public void dispose() {

    }

    /******************************************************************
     *                                                                *
     * Menu
     *                                                                *
     ******************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    protected final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Done", ActionType.FINISH, event -> Platform.runLater(App.main::backRefresh))
    );

}
