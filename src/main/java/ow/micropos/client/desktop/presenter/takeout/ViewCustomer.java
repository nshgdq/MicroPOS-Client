package ow.micropos.client.desktop.presenter.takeout;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.people.Customer;


public class ViewCustomer extends ItemPresenter<Customer> {

    @FXML Label lblPreviousOrder;
    @FXML Label lblFullName;
    @FXML Label lblPhoneNumber;

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
