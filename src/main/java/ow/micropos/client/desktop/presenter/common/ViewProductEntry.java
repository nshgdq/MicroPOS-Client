package ow.micropos.client.desktop.presenter.common;


import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ow.micropos.client.desktop.model.orders.ProductEntry;

public class ViewProductEntry extends ItemPresenter<ProductEntry> {

    @FXML Label lblMenuItem;
    @FXML Label lblTotal;
    @FXML Label lblModifiers;
    @FXML Label lblStatus;

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblMenuItem);
            unsetLabel(lblModifiers);
            unsetLabel(lblTotal);
            unsetLabel(lblStatus);
            //currentItem.modifiersTextProperty().removeListener(modifiersListener);
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
            //setLabel(lblStatus, newItem.statusTextProperty());
            //newItem.modifiersTextProperty().addListener(modifiersListener);
        }
    }

    @Override
    public void dispose() {
        //if (getItem() != null)
        //    getItem().modifiersTextProperty().removeListener(modifiersListener);
        unsetLabel(lblMenuItem);
        unsetLabel(lblModifiers);
        unsetLabel(lblTotal);
        unsetLabel(lblStatus);
        getView().setOnMouseClicked(null);
    }

    private ChangeListener<String> modifiersListener = (obs, oldVal, newVal) -> {
        if (newVal == null || newVal.length() == 0) {
            lblModifiers.setVisible(false);
        } else {
            lblModifiers.setVisible(true);
        }
    };

    /******************************************************************
     *                                                                *
     * Fit to Width Hack
     *                                                                *
     ******************************************************************/

    public void fixWidth(ListView<?> lv) {
        ListViewUtils.fitToListView(lblMenuItem, lv);
        ListViewUtils.fitToListView(lblModifiers, lv);
    }

}
