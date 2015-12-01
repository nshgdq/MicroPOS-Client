package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.menu.Category;


public class ViewCategory extends ItemPresenter<Category> {

    @FXML Label lblName;
    @FXML Label lblTag;

    @Override
    protected void updateItem(Category currentItem, Category newItem) {
        if (newItem == null) {
            unsetLabel(lblName);
            unsetLabel(lblTag);
        } else {
            setLabel(lblName, newItem.nameProperty());
            setLabel(lblTag, newItem.tagProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblName);
        unsetLabel(lblTag);
        getView().setOnMouseClicked(null);
    }
}
