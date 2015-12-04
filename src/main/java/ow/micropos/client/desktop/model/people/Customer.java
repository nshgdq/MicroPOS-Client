package ow.micropos.client.desktop.model.people;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ow.micropos.client.desktop.model.orders.SalesOrder;

import java.util.List;

public class Customer {

    public Customer() {}

    public Customer(String firstName, String lastName, String phoneNumber) {
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty firstName = new SimpleStringProperty();

    private StringProperty lastName = new SimpleStringProperty();

    private StringProperty phoneNumber = new SimpleStringProperty();

    private ListProperty<SalesOrder> salesOrders = new SimpleListProperty<>(FXCollections.observableArrayList());

    private StringProperty previousOrder = new SimpleStringProperty();

    public Long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public ListProperty<SalesOrder> salesOrdersProperty() {
        return salesOrders;
    }

    public ObservableList<SalesOrder> getSalesOrders() {
        return salesOrders.get();
    }

    public void setSalesOrders(List<SalesOrder> salesOrders) {
        this.salesOrders.setAll(salesOrders);
    }

    public String getPreviousOrder() {
        return previousOrder.get();
    }

    public StringProperty previousOrderProperty() {
        return previousOrder;
    }

    public void setPreviousOrder(String previousOrder) {
        this.previousOrder.set(previousOrder);
    }

    public boolean contains(String firstName, String lastName, String phoneNumber) {
        return this.firstName.get().toLowerCase().contains(firstName.toLowerCase())
                && this.lastName.get().toLowerCase().contains(lastName.toLowerCase())
                && this.phoneNumber.get().toLowerCase().contains(phoneNumber.toLowerCase());
    }

    /******************************************************************
     *                                                                *
     * Full Name                                                      *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper fullName;

    public ReadOnlyStringProperty fullNameProperty() {
        if (fullName == null) {
            fullName = new ReadOnlyStringWrapper();
            fullName.bind(Bindings.concat(firstName, " ", lastName));
        }
        return fullName.getReadOnlyProperty();
    }

}
