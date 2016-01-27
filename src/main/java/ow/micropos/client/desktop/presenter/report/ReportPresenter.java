package ow.micropos.client.desktop.presenter.report;

import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.time.LocalDate;

public class ReportPresenter extends Presenter {

    public DatePicker dpDate;
    public Button btnGenerate;

    @Override
    public void initialize() {
        super.initialize();

        btnGenerate.setOnMouseClicked(event -> Platform.runLater(() -> {
            LocalDate date = dpDate.getValue();
            App.apiProxy.getDayReport(
                    date.getYear(),
                    date.getMonthValue() - 1,
                    date.getDayOfMonth(),
                    new RunLaterCallback<DaySalesReport>() {
                        @Override
                        public void laterSuccess(DaySalesReport report) {
                            System.out.println(report.toString());
                        }
                    });
        }));

    }

    @Override
    public void dispose() {

    }

}
