package ow.micropos.client.desktop.model.employee;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;
import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.orders.SalesOrder;

import java.util.List;
import java.util.Objects;

public class Employee {

    public Employee() {}

    public Employee(long id) {
        setId(id);
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty firstName = new SimpleStringProperty();

    private StringProperty lastName = new SimpleStringProperty();

    private StringProperty pin = new SimpleStringProperty();

    private ListProperty<Position> positions = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<SalesOrder> salesOrders = new SimpleListProperty<>(FXCollections.observableArrayList());

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

    public ObservableList<Position> getPositions() {
        return positions.get();
    }

    public ListProperty<Position> positionsProperty() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions.setAll(positions);
    }

    public String getPin() {
        return pin.get();
    }

    public StringProperty pinProperty() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin.set(pin);
    }

    public ListProperty<SalesOrder> salesOrdersProperty() {
        return salesOrders;
    }

    public void setSalesOrders(List<SalesOrder> salesOrders) {
        this.salesOrders.setAll(salesOrders);
    }

    public boolean hasPermission(Permission permission) {
        for (Position position : positions)
            if (position.hasPermission(permission))
                return true;
        return false;
    }

    public boolean hasPermissions(Permission... permissions) {
        for (Permission reqPermission : permissions)
            if (!hasPermission(reqPermission))
                return false;
        return true;
    }

    public boolean owns(SalesOrder salesOrder) {
        return (id != null)
                && (salesOrder.getEmployee() != null)
                && Objects.equals(getId(), salesOrder.getEmployee().getId());
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

    /******************************************************************
     *                                                                *
     * Positions                                                      *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper positionSummary;

    public ReadOnlyStringProperty positionSummaryProperty() {
        if (positionSummary == null) {
            positionSummary = new ReadOnlyStringWrapper();
            positionSummary.bind(new StringBinding() {
                {bind(positions);}

                @Override
                protected String computeValue() {
                    return StringUtils.join(
                            positions.stream().map(p -> p.getName() + "(" + p.getId() + ")").toArray(),
                            ","
                    );
                }
            });
        }
        return positionSummary.getReadOnlyProperty();
    }

}
