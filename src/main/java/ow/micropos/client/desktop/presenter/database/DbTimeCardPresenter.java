package ow.micropos.client.desktop.presenter.database;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.timecard.TimeCardEntry;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DbTimeCardPresenter extends DbEntityPresenter<TimeCardEntry> {


    TextField tfEmployee;
    TextField tfDate;
    CheckBox cbClockin;

    TableColumn<TimeCardEntry, String> employee;
    TableColumn<TimeCardEntry, String> date;
    TableColumn<TimeCardEntry, String> clockin;

    @Override
    Label getTitleLabel() {
        return new Label("Time Card Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{new Label("Employee"), new Label("Date"), new Label("Clock In")};
    }

    @Override
    Node[] getEditControls() {
        tfEmployee = createTextField("Employee");
        tfDate = createTextField("Date");
        cbClockin = createCheckBox("Clock In");

        return new Node[]{tfEmployee, tfDate, cbClockin};
    }

    @Override
    protected Node[] getExtraControls() {
        TextField employeeId = createTextField("Employee Id");
        DatePicker dpStart = new DatePicker();
        DatePicker dpEnd = new DatePicker();
        Button btnUpdate = new Button();

        btnUpdate.setOnMouseClicked(event -> Platform.runLater(() -> {
            Long id;
            try {
                id = Long.parseLong(employeeId.getText());
            } catch (Exception e) {
                id = null;
            }

            Date start, end;
            LocalDate ldStart = dpStart.getValue();
            LocalDate ldEnd = dpEnd.getValue();
            if (ldStart == null || ldEnd == null || ldStart.isAfter(ldEnd)) {
                start = null;
                end = null;
            } else {
                start = Date.from(Instant.from(ldStart.atStartOfDay(ZoneId.systemDefault())));
                end = Date.from(Instant.from(ldEnd.atStartOfDay(ZoneId.systemDefault()).plusDays(1)));
            }

            App.apiProxy.listTimeCardEntries(id, start, end, new RunLaterCallback<List<TimeCardEntry>>() {
                @Override
                public void laterSuccess(List<TimeCardEntry> timeCardEntries) {
                    table.setItems(FXCollections.observableList(timeCardEntries));
                }
            });
        }));

        return new Node[]{employeeId, dpStart, dpEnd, btnUpdate};
    }

    @Override
    TableColumn<TimeCardEntry, String>[] getTableColumns() {
        employee = createTableColumn("Employee", param -> param.getValue().getEmployee().fullNameProperty());
        date = createTableColumn("Date", param -> param.getValue().dateProperty().asString());
        clockin = createTableColumn("Clock In", param -> param.getValue().clockinProperty().asString());

        return new TableColumn[]{employee, date, clockin};
    }

    @Override
    void unbindItem(TimeCardEntry currentItem) {
        tfEmployee.textProperty().unbindBidirectional(currentItem.getEmployee().idProperty());
        tfDate.textProperty().unbindBidirectional(currentItem.dateProperty());
        cbClockin.selectedProperty().unbindBidirectional(currentItem.clockinProperty());
    }

    @Override
    void bindItem(TimeCardEntry newItem) {
        tfEmployee.textProperty().bindBidirectional(newItem.getEmployee().idProperty(), idConverter);
        tfDate.textProperty().bindBidirectional(newItem.dateProperty(), dateTimeConverter);
        cbClockin.selectedProperty().bindBidirectional(newItem.clockinProperty());
    }

    @Override
    void toggleControls(boolean visible) {
        tfEmployee.setVisible(visible);
        tfDate.setVisible(visible);
        cbClockin.setVisible(visible);
    }

    @Override
    TimeCardEntry createNew() {
        TimeCardEntry timeCardEntry = new TimeCardEntry();
        timeCardEntry.setEmployee(new Employee());
        return timeCardEntry;
    }

    @Override
    void updateTableContent(TableView<TimeCardEntry> table) {
        App.apiProxy.listTimeCardEntries(null, null, null, new RunLaterCallback<List<TimeCardEntry>>() {
            @Override
            public void laterSuccess(List<TimeCardEntry> timeCardEntries) {
                table.setItems(FXCollections.observableList(timeCardEntries));
            }
        });
    }

    @Override
    void submitItem(TimeCardEntry item) {
        App.apiProxy.updateTimeCardEntry(item, RunLaterCallback.submitCallback(item, table, (id, o) -> o.setId(id)));
    }

    @Override
    void deleteItem(TimeCardEntry item) {
        App.apiProxy.removeTimeCardEntry(item.getId(), RunLaterCallback.deleteCallback(item, table));
    }
}
