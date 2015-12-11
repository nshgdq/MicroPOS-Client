package ow.micropos.client.desktop.model.auth;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.Permission;

import java.util.List;

public class Position {

    public Position() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private ListProperty<Permission> permissions = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<Employee> employees = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableList<Permission> getPermissions() {
        return permissions.get();
    }

    public ListProperty<Permission> permissionsProperty() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions.setAll(permissions);
    }

    public ObservableList<Employee> getEmployees() {
        return employees.get();
    }

    public ListProperty<Employee> employeesProperty() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees.setAll(employees);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermissions(Permission... permissions) {
        if (permissions == null)
            return true;

        for (Permission permission : permissions)
            if (!hasPermission(permission))
                return false;

        return true;
    }

}
