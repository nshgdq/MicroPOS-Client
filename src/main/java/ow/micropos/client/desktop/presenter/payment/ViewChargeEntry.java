package ow.micropos.client.desktop.presenter.payment;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.orders.ChargeEntry;

public class ViewChargeEntry extends ItemPresenter<ChargeEntry> {

    @FXML Label lblChargeName;

    @Override
    protected void updateItem(ChargeEntry currentItem, ChargeEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblChargeName);
        } else {
            if (newItem.getCharge() != null)
                setLabel(lblChargeName, newItem.getCharge().nameProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblChargeName);
        getView().setOnMouseClicked(null);
    }
}
