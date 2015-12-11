package ow.micropos.client.desktop.presenter.dinein;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.target.Section;

public class ViewSection extends ItemPresenter<Section> {

    @FXML Label lblName;
    @FXML Label lblTag;

    @Override
    protected void updateItem(Section currentItem, Section newItem) {
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
