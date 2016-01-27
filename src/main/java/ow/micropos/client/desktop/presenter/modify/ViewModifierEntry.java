package ow.micropos.client.desktop.presenter.modify;

import email.com.gmail.ttsai0509.javafx.LabelUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.model.menu.Modifier;


public class ViewModifierEntry extends ItemPresenter<Modifier> {

    @FXML Label lblName;
    @FXML Label lblPrice;

    @Override
    public void initialize() {
        StackPane.setAlignment(lblName, Pos.CENTER_LEFT);
        LabelUtils.fitToText(lblPrice);
    }

    @Override
    protected void updateItem(Modifier currentItem, Modifier newItem) {
        if (newItem == null) {
            unsetLabel(lblName);
            unsetLabel(lblPrice);
        } else {
            setLabel(lblName, Bindings.concat(newItem.tagProperty(), " ", newItem.nameProperty()));
            setLabel(lblPrice, newItem.priceProperty().asString());
        }

    }

    @Override
    public void dispose() {
        unsetLabel(lblName);
        unsetLabel(lblPrice);
        getView().setOnMouseClicked(null);
    }
}
