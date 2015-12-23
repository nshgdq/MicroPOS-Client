package ow.micropos.client.desktop.presenter.login;

import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.employee.Employee;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginPresenter extends Presenter {

    @FXML ImageView imgLogo;
    @FXML Label lblPin;
    @FXML GridPane gpKeypad;
    @FXML Button btn0;
    @FXML Button btn1;
    @FXML Button btn2;
    @FXML Button btn3;
    @FXML Button btn4;
    @FXML Button btn5;
    @FXML Button btn6;
    @FXML Button btn7;
    @FXML Button btn8;
    @FXML Button btn9;
    @FXML Button btnClear;
    @FXML Button btnLogin;

    private StringProperty rawPin;

    @Override
    public void initialize() {

        rawPin = new SimpleStringProperty("");

        imgLogo.setImage(new Image(App.properties.getStr("logo")));
        imgLogo.setFitWidth(500);
        imgLogo.setPreserveRatio(true);
        imgLogo.setSmooth(true);
        imgLogo.setCache(true);

        setLabel(lblPin, new StringBinding() {
            {
                bind(rawPin);
            }

            @Override
            protected String computeValue() {
                return StringUtils.repeat('.', rawPin.get().length());
            }
        });

        btn0.setOnAction(event -> rawPin.set(rawPin.get() + "0"));
        btn1.setOnAction(event -> rawPin.set(rawPin.get() + "1"));
        btn2.setOnAction(event -> rawPin.set(rawPin.get() + "2"));
        btn3.setOnAction(event -> rawPin.set(rawPin.get() + "3"));
        btn4.setOnAction(event -> rawPin.set(rawPin.get() + "4"));
        btn5.setOnAction(event -> rawPin.set(rawPin.get() + "5"));
        btn6.setOnAction(event -> rawPin.set(rawPin.get() + "6"));
        btn7.setOnAction(event -> rawPin.set(rawPin.get() + "7"));
        btn8.setOnAction(event -> rawPin.set(rawPin.get() + "8"));
        btn9.setOnAction(event -> rawPin.set(rawPin.get() + "9"));
        btnClear.setOnAction(event -> rawPin.set(""));
        btnLogin.setOnAction(loginEvent);
    }

    @Override
    public void dispose() {
        btn0.setOnAction(null);
        btn1.setOnAction(null);
        btn2.setOnAction(null);
        btn3.setOnAction(null);
        btn4.setOnAction(null);
        btn5.setOnAction(null);
        btn6.setOnAction(null);
        btn7.setOnAction(null);
        btn8.setOnAction(null);
        btn9.setOnAction(null);
        btnClear.setOnAction(null);
        btnLogin.setOnAction(null);
    }

    @Override
    public void refresh() {
        rawPin.set("");
    }

    private final EventHandler<ActionEvent> loginEvent = (event) -> {
            App.api.getEmployee(
                    rawPin.get(),
                    new AlertCallback<Employee>() {
                        @Override
                        public void onSuccess(Employee employee, Response response) {
                            if (employee != null) {
                                App.employee = employee;
                                App.main.nextRefresh(App.homePresenter);
                            }
                        }

                        @Override
                        public void onFailure(RetrofitError error) {
                            refresh();
                        }
                    });
    };


}
