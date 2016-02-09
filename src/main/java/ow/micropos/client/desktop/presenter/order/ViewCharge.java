package ow.micropos.client.desktop.presenter.order;


import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ow.micropos.client.desktop.model.menu.Charge;

public class ViewCharge extends ItemPresenter<Charge> {

    @FXML Label lblChargeTag;

    @Override
    protected void updateItem(Charge currentItem, Charge newItem) {
        if (newItem == null) {
            unsetLabel(lblChargeTag);
        } else {
            setLabel(lblChargeTag, newItem.nameProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblChargeTag);
        getView().setOnMouseClicked(null);
    }


    /******************************************************************
     *                                                                *
     * Fit width of label to width of ListView
     *                                                                *
     ******************************************************************/

    public final void fixWidth(ListView<?> lv) {
        ListViewUtils.fitToListView(lblChargeTag, lv);
    }
}
