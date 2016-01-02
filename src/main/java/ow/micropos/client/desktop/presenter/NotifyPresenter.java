package ow.micropos.client.desktop.presenter;

import javafx.scene.control.Alert;
import ow.micropos.client.desktop.model.enums.Permission;

import java.util.Arrays;

public class NotifyPresenter extends Alert {

    public NotifyPresenter() {
        super(AlertType.WARNING);
    }

    public void showAndWait(String content) {
        showAndWait(null, content);
    }

    public void showAndWait(String header, String content) {
        showAndWait("ALERT", header, content);
    }

    public void showAndWait(String title, String header, String content) {
        setTitle(title);
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public void showAndWait(Permission... permissions) {
        showAndWait("Requires Permissions : " + Arrays.toString(permissions));
    }

}
