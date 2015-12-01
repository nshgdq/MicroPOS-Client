package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ow.micropos.client.desktop.model.orders.ProductEntry;

public class ViewProductEntry extends ItemPresenter<ProductEntry> {

    @FXML Label lblSoeMenuItem;
    @FXML Label lblSoeTotal;
    @FXML Label lblSoeModifiers;
    @FXML Label lblStatus;

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblSoeMenuItem);
            unsetLabel(lblSoeModifiers);
            unsetLabel(lblSoeTotal);
            unsetLabel(lblStatus);
        } else {
            setLabel(lblSoeMenuItem, Bindings.concat(newItem.quantityProperty().asString(), " ", newItem.menuItemNameProperty()));
            setLabel(lblSoeTotal, newItem.totalProperty().asString());
            setLabel(lblSoeModifiers, newItem.modifiersTextProperty());
            setLabel(lblStatus, newItem.statusTextProperty());
        }

    }

    @Override
    public void dispose() {
        unsetLabel(lblSoeMenuItem);
        unsetLabel(lblSoeModifiers);
        unsetLabel(lblSoeTotal);
        unsetLabel(lblStatus);
    }

    /*****************************************************************************
     *                                                                           *
     * Fit width of label to ~width of ListView                                 *
     *                                                                           *
     *****************************************************************************/

    private static final double fixWidthPad = -10;

    public final void fixWidth(ListView<?> lv) {
        ListViewUtils.bindFitToListView(lblSoeMenuItem, lv, fixWidthPad);
        ListViewUtils.bindFitToListView(lblSoeModifiers, lv, fixWidthPad);
    }

}
