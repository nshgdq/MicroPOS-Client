package ow.micropos.client.desktop.presenter.common;


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

    @FXML public GridPane gpParent;
    @FXML public Label lblMenuItem;
    @FXML public Label lblTotal;
    @FXML public Label lblModifiers;

    @Override
    public void initialize() {
        StackPane.setAlignment(lblMenuItem, Pos.CENTER_LEFT);
        LabelUtils.fitToText(lblTotal);
    }

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblMenuItem);
            unsetLabel(lblModifiers);
            unsetLabel(lblTotal);
        } else {
            setLabel(lblMenuItem, Bindings.concat(
                    newItem.quantityProperty().asString(),
                    " - ",
                    newItem.getMenuItem().tagProperty(),
                    " ",
                    newItem.menuItemNameProperty()
            ));
            setLabel(lblTotal, newItem.totalProperty().asString());
            setLabel(lblModifiers, newItem.modifiersTextProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblMenuItem);
        unsetLabel(lblModifiers);
        unsetLabel(lblTotal);
        getView().setOnMouseClicked(null);
    }

    /******************************************************************
     *                                                                *
     * Fit to Width Hack
     *                                                                *
     ******************************************************************/

    public void fixWidth(ListView<?> lv) {
        ListViewUtils.fitToListView(gpParent, lv);
    }

}
