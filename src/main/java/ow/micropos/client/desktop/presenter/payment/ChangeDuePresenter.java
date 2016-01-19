package ow.micropos.client.desktop.presenter.payment;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.orders.SalesOrder;

public class ChangeDuePresenter extends ItemPresenter<SalesOrder> {

    @FXML public Label lblChange;
    @FXML public Button btnDone;

    @Override
    public void initialize() {
        super.initialize();
        btnDone.setOnMouseClicked(event -> Platform.runLater(App.main::backRefresh));
    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            unsetLabel(lblChange);
        } else {
            setLabel(lblChange, Bindings.concat("$", newItem.changeProperty().asString()));
        }
    }

    @Override
    public void dispose() {

    }


}
