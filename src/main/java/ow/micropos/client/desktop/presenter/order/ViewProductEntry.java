package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.LabelUtils;
import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
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
        if (currentItem != null)
            currentItem.statusProperty().removeListener(backgroundUpdater);

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

            updateBackground(newItem.getStatus());
            newItem.statusProperty().addListener(backgroundUpdater);
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

    private final ChangeListener<ProductEntryStatus> backgroundUpdater = (obs, o, n) -> updateBackground(n);

    private void updateBackground(ProductEntryStatus status) {
        switch (status) {
            case SENT:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-sent"));
                break;
            case HOLD:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-hold"));
                break;
            case VOID:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-void"));
                break;
            case REQUEST_SENT:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-request-sent"));
                break;
            case REQUEST_EDIT:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-request-edit"));
                break;
            case REQUEST_HOLD:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-request-hold"));
                break;
            case REQUEST_HOLD_VOID:
            case REQUEST_VOID:
                getView().setStyle("-fx-background-color:" + App.properties.getStr("pe-status-color-request-void"));
                break;
        }
    }

}
