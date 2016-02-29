package ow.micropos.client.desktop.presenter.login;

import com.github.sarxos.webcam.Webcam;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.model.timecard.TimeCardEntry;
import ow.micropos.client.desktop.service.RunLaterCallback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginPresenter extends Presenter {

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, MM/dd/yy hh:mm a");

    @FXML public ImageView imgLogo;
    @FXML public Label lblPin;
    @FXML public GridPane gpKeypad;
    @FXML public StackPane btn0;
    @FXML public StackPane btn1;
    @FXML public StackPane btn2;
    @FXML public StackPane btn3;
    @FXML public StackPane btn4;
    @FXML public StackPane btn5;
    @FXML public StackPane btn6;
    @FXML public StackPane btn7;
    @FXML public StackPane btn8;
    @FXML public StackPane btn9;
    @FXML public StackPane btnClear;
    @FXML public StackPane btnLogin;
    @FXML public GridPane gpLogin;

    @FXML public StackPane btnClockIn;
    @FXML public StackPane btnClockOut;
    @FXML public StackPane btnTimeCard;

    private StringProperty rawPin;

    @Override
    public void initialize() {

        rawPin = new SimpleStringProperty("");

        imgLogo.setImage(new Image(App.properties.getStr("logo")));
        imgLogo.setFitWidth(App.properties.getDbl("logo-size"));
        imgLogo.setPreserveRatio(true);
        imgLogo.setSmooth(true);
        imgLogo.setCache(true);

        setLabel(lblPin, new StringBinding() {
            {
                bind(rawPin);
            }

            @Override
            protected String computeValue() {
                return StringUtils.repeat('\u25CF', rawPin.get().length());
            }
        });

        btnClear.setOnMouseClicked(event -> rawPin.set(""));
        btn0.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "0"));
        btn1.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "1"));
        btn2.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "2"));
        btn3.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "3"));
        btn4.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "4"));
        btn5.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "5"));
        btn6.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "6"));
        btn7.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "7"));
        btn8.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "8"));
        btn9.setOnMouseClicked(event -> rawPin.set(rawPin.get() + "9"));
        btnLogin.setOnMouseClicked(event -> App.apiProxy.getEmployee(rawPin.get(), new RunLaterCallback<Employee>() {
            @Override
            public void laterSuccess(Employee employee) {
                if (employee == null)
                    return;
                App.employee = employee;
                App.main.nextRefresh(App.homePresenter);
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                refresh();
            }
        }));

        btnClockIn.setOnMouseClicked(event -> App.apiProxy.clockIn(
                rawPin.get(),
                trySnapshot(true),
                new RunLaterCallback<TimeCardEntry>() {
                    @Override
                    public void laterSuccess(TimeCardEntry timeCardEntry) {
                        refresh();
                        if (timeCardEntry == null) {
                            App.notify.showAndWait("Unable to clock in.");
                        } else {
                            App.notify.showAndWait("Clocked in at " + timeFormat.format(timeCardEntry.getDate()));
                        }
                    }

                    @Override
                    public void laterFailure(ErrorInfo error) {
                        refresh();
                        App.notify.showAndWait(error.getMessage());
                    }
                }));

        btnClockOut.setOnMouseClicked(event -> App.apiProxy.clockOut(
                rawPin.get(),
                trySnapshot(false),
                new RunLaterCallback<TimeCardEntry>() {
                    @Override
                    public void laterSuccess(TimeCardEntry timeCardEntry) {
                        refresh();
                        if (timeCardEntry == null) {
                            App.notify.showAndWait("Unable to clock out.");
                        } else {
                            App.notify.showAndWait("Clocked out at " + timeFormat.format(timeCardEntry.getDate()));
                        }
                    }

                    @Override
                    public void laterFailure(ErrorInfo error) {
                        refresh();
                        App.notify.showAndWait(error.getMessage());
                    }
                }));

        btnTimeCard.setOnMouseClicked(event -> App.apiProxy.viewTimeCard(
                rawPin.get(),
                new RunLaterCallback<java.util.List<TimeCardEntry>>() {
                    @Override
                    public void laterSuccess(List<TimeCardEntry> timeCardEntries) {
                        refresh();
                        String result = "";
                        for (TimeCardEntry e : timeCardEntries)
                            result += e.getClockin().toString() + timeFormat.format(e.getDate()) + "\n";
                        App.notify.showAndWait(result);
                    }

                    @Override
                    public void laterFailure(ErrorInfo error) {
                        refresh();
                        App.notify.showAndWait(error.getMessage());
                    }
                }
        ));
    }

    @Override
    public void dispose() {
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
        btnLogin.setOnMouseClicked(null);
    }

    @Override
    public void refresh() {
        rawPin.set("");
    }

    private String trySnapshot(boolean clockin) {

        if (!App.properties.getBool("time-card-snapshot"))
            return "";

        Integer width = App.properties.getInt("time-card-snapshot-width");
        Integer height = App.properties.getInt("time-card-snapshot-height");
        if (width == null || height == null)
            return "";

        String type = App.properties.getStr("time-card-snapshot-type");
        if (type == null || type.isEmpty())
            return "";

        App.notify.setTitle("ALERT");
        App.notify.setHeaderText(clockin ? "Clock In" : "Clock Out");
        App.notify.setContentText("Please wait...");
        App.notify.show();

        String result = "";

        try {

            Webcam webcam = Webcam.getDefault(10, TimeUnit.SECONDS);
            webcam.setViewSize(new Dimension(width, height));
            webcam.open();

            BufferedImage image = webcam.getImage();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, type, out);
            result = Base64.encodeBase64String(out.toByteArray());

            webcam.close();

        } catch (Exception e) {

            // suppress

        }

        App.notify.close();

        return result;

    }

    private String formatTimeCard(List<TimeCardEntry> timeCardEntries) {

        timeCardEntries.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));

        String result = "";

        boolean isWorking = false;

        Date date = new Date(0);

        for (TimeCardEntry e : timeCardEntries) {

            if (!DateUtils.isSameDay(date, e.getDate())) {
                date = e.getDate();
                result += "\n";
            }

            boolean clockIn = e.getClockin();

            if (isWorking) {
                if (clockIn) {

                } else {

                }

            } else {
                if (clockIn) {

                } else {

                }
            }
        }

        for (TimeCardEntry e : timeCardEntries) {
            result += e.getClockin().toString() + timeFormat.format(e.getDate()) + "\n";
        }

        return result;
    }

}
