package ow.micropos.client.desktop.presenter.payment;


import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.charge.Charge;

public class ViewCharge extends ItemPresenter<Charge> {

    @FXML Label lblChargeName;

    @Override
    protected void updateItem(Charge currentItem, Charge newItem) {
        if (newItem == null) {
            unsetLabel(lblChargeName);
        } else {
            setLabel(lblChargeName, newItem.nameProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblChargeName);
        getView().setOnMouseClicked(null);
    }
}
