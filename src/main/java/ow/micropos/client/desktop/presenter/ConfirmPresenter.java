package ow.micropos.client.desktop.presenter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmPresenter extends Alert {

    public ConfirmPresenter() {
        super(AlertType.CONFIRMATION);
    }

    public void showAndWait(String content, Runnable action) {
        showAndWait(null, content, action);
    }

    public void showAndWait(String header, String content, Runnable action) {
        showAndWait("CONFIRM", header, content, action);
    }

    public void showAndWait(String title, String header, String content, Runnable action) {
        setTitle(title);
        setHeaderText(header);
        setContentText(content);

        Optional<ButtonType> result = showAndWait();
        if (result.get() == ButtonType.OK)
            Platform.runLater(action::run);
    }

}
