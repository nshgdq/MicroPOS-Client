package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.menu.MenuItem;

public class ViewMenuItem extends ItemPresenter<MenuItem> {

    @FXML Label lblName;
    @FXML Label lblTag;
    @FXML Label lblPrice;

    @Override
    protected void updateItem(MenuItem currentItem, MenuItem newItem) {
        if (newItem == null) {
            unsetLabel(lblName);
            unsetLabel(lblTag);
            unsetLabel(lblPrice);
        } else {
            setLabel(lblTag, newItem.tagProperty());
            setLabel(lblName, newItem.nameProperty());
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
