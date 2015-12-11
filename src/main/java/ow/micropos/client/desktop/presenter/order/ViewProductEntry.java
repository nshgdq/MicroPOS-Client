package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import ow.micropos.client.desktop.model.orders.ProductEntry;

public class ViewProductEntry extends ItemPresenter<ProductEntry> {

    @FXML GridPane gpParent;
    @FXML Label lblSoeMenuItem;
    @FXML Label lblSoeTotal;
    @FXML Label lblSoeModifiers;
    @FXML Label lblStatus;

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            if (currentItem != null && currentItem.modifiersTextProperty() != null)
                currentItem.modifiersTextProperty().removeListener(updateModifierViewListener);
            updateModifierView(null);
            unsetLabel(lblSoeMenuItem);
            unsetLabel(lblSoeModifiers);
            unsetLabel(lblSoeTotal);
            unsetLabel(lblStatus);
        } else {
            updateModifierView(newItem);
            newItem.modifiersTextProperty().addListener(updateModifierViewListener);
            setLabel(lblSoeMenuItem, Bindings.concat(
                    newItem.quantityProperty().asString(),
                    " - ",
                    newItem.getMenuItem().tagProperty(),
                    " ",
                    newItem.menuItemNameProperty()
            ));
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

    /******************************************************************
     *                                                                *
     * Hide modifiers when empty
     *                                                                *
     ******************************************************************/

    private ChangeListener<String> updateModifierViewListener = (obs, oldVal, newVal) -> updateModifierView(getItem());

    private void updateModifierView(ProductEntry pe) {
        Platform.runLater(() -> {
            boolean visible = gpParent.getChildren().contains(lblSoeModifiers);

            if (!visible && (pe != null && !pe.getModifiers().isEmpty())) {
                gpParent.getChildren().add(lblSoeModifiers);

            } else if (visible && (pe == null || pe.getModifiers().isEmpty())) {
                gpParent.getChildren().remove(lblSoeModifiers);
            }

        });
    }

    /******************************************************************
     *                                                                *
     * Fit width of label to width of ListView
     *                                                                *
     ******************************************************************/

    public final void fixWidth(ListView<?> lv) {
        ListViewUtils.bindFitToListView(lblSoeMenuItem, lv);
        ListViewUtils.bindFitToListView(lblSoeModifiers, lv);
    }

}
