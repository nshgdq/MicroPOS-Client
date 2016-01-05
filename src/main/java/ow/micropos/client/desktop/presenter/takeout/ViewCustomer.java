package ow.micropos.client.desktop.presenter.takeout;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.model.target.Customer;


public class ViewCustomer extends ItemPresenter<Customer> {

    @FXML Label lblPreviousOrder;
    @FXML Label lblFullName;
    @FXML Label lblPhoneNumber;

    @Override
    public void initialize() {
        StackPane.setAlignment(lblFullName, Pos.CENTER_LEFT);
        StackPane.setAlignment(lblPhoneNumber, Pos.CENTER_LEFT);
        StackPane.setAlignment(lblPreviousOrder, Pos.TOP_LEFT);
    }

    @Override
    protected void updateItem(Customer currentItem, Customer newItem) {
        if (newItem == null) {
            unsetLabel(lblFullName);
            unsetLabel(lblPhoneNumber);
            unsetLabel(lblPreviousOrder);
        } else {
            setLabel(lblFullName, newItem.fullNameProperty());
            setLabel(lblPhoneNumber, newItem.phoneNumberProperty());
            setLabel(lblPreviousOrder, newItem.previousOrderProperty());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblFullName);
        unsetLabel(lblPhoneNumber);
        unsetLabel(lblPreviousOrder);
        getView().setOnMouseClicked(null);
    }
}
