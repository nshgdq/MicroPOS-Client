package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.LabelUtils;
import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.model.orders.ProductEntry;

public class ViewProductEntry extends ItemPresenter<ProductEntry> {

    @FXML GridPane gpParent;
    @FXML Label lblSoeMenuItem;
    @FXML Label lblSoeTotal;
    @FXML Label lblSoeModifiers;

    @Override
    public void initialize() {
        StackPane.setAlignment(lblSoeMenuItem, Pos.CENTER_LEFT);
        LabelUtils.fitToText(lblSoeTotal);
    }

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblSoeMenuItem);
            unsetLabel(lblSoeModifiers);
            unsetLabel(lblSoeTotal);
        } else {
            setLabel(lblSoeTotal, newItem.totalProperty().asString());
            setLabel(lblSoeMenuItem, Bindings.concat(
                    newItem.quantityProperty().asString(),
                    " - ",
                    newItem.getMenuItem().tagProperty(),
                    " ",
                    newItem.menuItemNameProperty()
            ));
            setLabel(lblSoeModifiers, newItem.modifiersTextProperty());
        }

    }

    @Override
    public void dispose() {
        unsetLabel(lblSoeMenuItem);
        unsetLabel(lblSoeModifiers);
        unsetLabel(lblSoeTotal);
    }

    /******************************************************************
     *                                                                *
     * Fit width of view to width of ListView
     *                                                                *
     ******************************************************************/

    public final void fixWidth(ListView<?> lv) {
        ListViewUtils.fitToListView(gpParent, lv);
    }

}
