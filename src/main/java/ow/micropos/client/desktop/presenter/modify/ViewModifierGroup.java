package ow.micropos.client.desktop.presenter.modify;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.menu.ModifierGroup;

public class ViewModifierGroup extends ItemPresenter<ModifierGroup> {

    @FXML Label lblTag;
    @FXML Label lblName;

    @Override
    protected void updateItem(ModifierGroup currentItem, ModifierGroup newItem) {
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

    }
}
