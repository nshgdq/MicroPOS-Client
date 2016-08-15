package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class CookPresenter extends ItemPresenter<SalesOrder> {

    @FXML
    public Label tfCookTime;
    @FXML
    public StackPane btn7;
    @FXML
    public StackPane btn8;
    @FXML
    public StackPane btn9;
    @FXML
    public StackPane btn4;
    @FXML
    public StackPane btn5;
    @FXML
    public StackPane btn6;
    @FXML
    public StackPane btn1;
    @FXML
    public StackPane btn2;
    @FXML
    public StackPane btn3;
    @FXML
    public StackPane btn0;
    @FXML
    public StackPane btnAdd10;
    @FXML
    public StackPane btnAdd15;
    @FXML
    public StackPane btnAdd30;
    @FXML
    public StackPane btnAdd100;
    @FXML
    public StackPane btnAmPm;
    @FXML
    public StackPane btnClear;
    @FXML
    public StackPane btnSetTime;

    @FXML
    public StackPane cancelOption;
    @FXML
    public StackPane sendOption;

    private SimpleDateFormat stdTimeFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
    private SimpleDateFormat rawTimeFormat = new SimpleDateFormat("ahhmm");
    private Pattern rawTimePattern = Pattern.compile("[AP]M(0[1-9]|1[0-2])[0-5][0-9]");
    private StringProperty rawTime = new SimpleStringProperty("");

    @Override
    public void initialize() {
        btnAdd10.setOnMouseClicked(event -> tryIncrementTime(10));
        btnAdd15.setOnMouseClicked(event -> tryIncrementTime(15));
        btnAdd30.setOnMouseClicked(event -> tryIncrementTime(30));
        btnAdd100.setOnMouseClicked(event -> tryIncrementTime(60));

        tfCookTime.textProperty().bind(new StringBinding() {
            {
                bind(rawTime);
            }

            @Override
            protected String computeValue() {
                String time = rawTime.get();
                if (time.length() == 2)
                    return "_:__ " + time;
                else if (time.length() == 3)
                    return "_:_" + time.charAt(2) + " " + time.substring(0, 2);
                else if (time.length() == 4)
                    return "_:" + time.substring(2, 4) + " " + time.substring(0, 2);
                else if (time.length() == 5)
                    return time.charAt(2) + ":" + time.substring(3, 5) + " " + time.substring(0, 2);
                else if (time.length() == 6)
                    return time.substring(2, 4) + ":" + time.substring(4, 6) + " " + time.substring(0, 2);
                else
                    return time;
            }
        });

        cancelOption.setOnMouseClicked(
                event -> App.confirm.showAndWait("Are you sure? Any changes will not be saved.", App.main::backRefresh)
        );

        sendOption.setOnMouseClicked(event -> {
            if (getItem().getProductEntries().isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("Nothing to send."));
            } else {
                if (setCookTime()) {
                    App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                        @Override
                        public void laterSuccess(Long aLong) {
                            getItem().setId(aLong);
                            App.main.backRefresh();

                            if (App.properties.getBool("print-send-takeout") && getItem().hasType(SalesOrderType.TAKEOUT))
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));

                            if (App.properties.getBool("print-send-dinein") && getItem().hasType(SalesOrderType.DINEIN))
                                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));

                            if (getItem().getCookTime() == null)
                                App.notify.showAndWait("Sales Order " + aLong);
                            else
                                App.notify.showAndWait("Sales Order " + aLong + " @ " + stdTimeFormat.format(getItem().getCookTime()));

                        }
                    });
                }
            }
        });

    }

    @Override
    public void refresh() {
        resetTime();
    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            btnAmPm.setOnMouseClicked(null);
            btn0.setOnMouseClicked(null);
            btn1.setOnMouseClicked(null);
            btn2.setOnMouseClicked(null);
            btn3.setOnMouseClicked(null);
            btn4.setOnMouseClicked(null);
            btn5.setOnMouseClicked(null);
            btn6.setOnMouseClicked(null);
            btn7.setOnMouseClicked(null);
            btn8.setOnMouseClicked(null);
            btn9.setOnMouseClicked(null);
            btnClear.setOnMouseClicked(null);
        } else {
            btn0.setOnMouseClicked(event -> tryAppendTime("0"));
            btn1.setOnMouseClicked(event -> tryAppendTime("1"));
            btn2.setOnMouseClicked(event -> tryAppendTime("2"));
            btn3.setOnMouseClicked(event -> tryAppendTime("3"));
            btn4.setOnMouseClicked(event -> tryAppendTime("4"));
            btn5.setOnMouseClicked(event -> tryAppendTime("5"));
            btn6.setOnMouseClicked(event -> tryAppendTime("6"));
            btn7.setOnMouseClicked(event -> tryAppendTime("7"));
            btn8.setOnMouseClicked(event -> tryAppendTime("8"));
            btn9.setOnMouseClicked(event -> tryAppendTime("9"));
            btnAmPm.setOnMouseClicked(event -> tryToggleAmPm());
            btnClear.setOnMouseClicked(event -> resetTime());
        }

    }

    @Override
    public void dispose() {

    }

    private boolean setCookTime() {
        if (!getItem().hasStatus(SalesOrderStatus.REQUEST_OPEN)) {
            App.notify.showAndWait("Order was already sent. Cook time can not be changed.");
            resetTime();
            return false;
        }

        if (rawTime.get().length() == 2) {
            getItem().setCookTime(null);
            return true;
        }

        String raw = parseRawTime();

        if (raw.length() != 6) {
            App.notify.showAndWait("Invalid Cook Time");
            resetTime();
            return false;
        }

        try {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(rawTimeFormat.parse(raw));
            c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
            c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
            c2.set(Calendar.DATE, c1.get(Calendar.DATE));
            getItem().setCookTime(c2.getTime());
            return true;

        } catch (ParseException e) {
            App.notify.showAndWait("Invalid Cook Time");
            resetTime();
            return false;
        }
    }

    private void tryIncrementTime(int minutes) {
        String raw = parseRawTime();

        if (raw.length() != 6) {
            raw = rawTimeFormat.format(new Date());
        }

        try {
            Date prev = rawTimeFormat.parse(raw);
            Date next = new Date(prev.getTime() + (minutes * 60000));
            rawTime.set(rawTimeFormat.format(next));

        } catch (ParseException e) {
            App.notify.showAndWait("Invalid Time");
            resetTime();
        }
    }

    private void tryAppendTime(String val) {
        String inputTime = rawTime.get();
        if (inputTime.length() < 6) {
            rawTime.set(inputTime + val);
        }
    }

    private void tryToggleAmPm() {
        String inputTime = rawTime.get();
        if (inputTime.startsWith("AM"))
            rawTime.set(inputTime.replace("AM", "PM"));
        else if (inputTime.startsWith("PM"))
            rawTime.set(inputTime.replace("PM", "AM"));
        else
            rawTime.set(getCurrentAmPm() + inputTime);
    }

    private String getCurrentAmPm() {
        switch (Calendar.getInstance().get(Calendar.AM_PM)) {
            case Calendar.AM:
                return "AM";
            case Calendar.PM:
                return "PM";
            default:
                return "PM";
        }
    }

    private String parseRawTime() {
        String raw = rawTime.get();

        if (raw.length() == 5) {
            raw = raw.substring(0, 2) + "0" + raw.substring(2);
        }

        if (!rawTimePattern.matcher(raw).find() || raw.length() != 6) {
            raw = "";
        }

        return raw;
    }

    private void resetTime() {
        rawTime.set(getCurrentAmPm());
    }

}
