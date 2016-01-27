package ow.micropos.client.desktop.presenter.login;

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
import org.apache.commons.lang3.StringUtils;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.service.RunLaterCallback;

public class LoginPresenter extends Presenter {


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

}
