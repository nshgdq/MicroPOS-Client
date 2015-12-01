package ow.micropos.client.desktop.presenter.payment;


import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.orders.PaymentEntry;

public class ViewPaymentEntry extends ItemPresenter<PaymentEntry> {

    @FXML Label lblType;
    @FXML Label lblAmount;

    @Override
    protected void updateItem(PaymentEntry currentItem, PaymentEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblType);
            unsetLabel(lblAmount);
        } else {
            setLabel(lblType, newItem.typeTextProperty());
            setLabel(lblAmount, newItem.amountProperty().asString());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblType);
        unsetLabel(lblAmount);
        getView().setOnMouseClicked(null);
    }
}
