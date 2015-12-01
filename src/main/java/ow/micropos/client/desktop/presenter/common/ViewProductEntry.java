package ow.micropos.client.desktop.presenter.common;


import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ow.micropos.client.desktop.model.orders.ProductEntry;

public class ViewProductEntry extends ItemPresenter<ProductEntry> {

    private static final double padding = -6.00;

    @FXML Label lblMenuItem;
    @FXML Label lblTotal;
    @FXML Label lblModifiers;
    @FXML Label lblStatus;

    public void fixWidth(ListView<?> lv) {
        ListViewUtils.bindFitToListView(lblMenuItem, lv, padding);
        ListViewUtils.bindFitToListView(lblModifiers, lv, padding);
    }

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblMenuItem);
            unsetLabel(lblModifiers);
            unsetLabel(lblTotal);
            unsetLabel(lblStatus);
        } else {
            setLabel(lblMenuItem, newItem.menuItemNameProperty());
            setLabel(lblTotal, newItem.totalProperty().asString());
            setLabel(lblModifiers, newItem.modifiersTextProperty());
            setLabel(lblStatus, newItem.statusTextProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblMenuItem);
        unsetLabel(lblModifiers);
        unsetLabel(lblTotal);
        unsetLabel(lblStatus);
        getView().setOnMouseClicked(null);
    }
}
