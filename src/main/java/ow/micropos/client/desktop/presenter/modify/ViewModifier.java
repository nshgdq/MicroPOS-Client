package ow.micropos.client.desktop.presenter.modify;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.menu.Modifier;


public class ViewModifier extends ItemPresenter<Modifier> {

    @FXML Label lblName;
    @FXML Label lblTag;
    @FXML Label lblPrice;

    @Override
    protected void updateItem(Modifier currentItem, Modifier newItem) {
        if (newItem == null) {
            unsetLabel(lblName);
            unsetLabel(lblTag);
            unsetLabel(lblPrice);
        } else {
            setLabel(lblName, newItem.nameProperty());
            setLabel(lblTag, newItem.tagProperty());
            setLabel(lblPrice, newItem.priceProperty().asString());
        }

    }

    @Override
    public void dispose() {
        unsetLabel(lblName);
        unsetLabel(lblTag);
        unsetLabel(lblPrice);
        getView().setOnMouseClicked(null);
    }
}
