package ow.micropos.client.desktop.model.timecard;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ow.micropos.client.desktop.model.employee.Employee;

import java.util.Date;

public class TimeCardEntry {

    public TimeCardEntry() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private ObjectProperty<Boolean> clockin = new SimpleObjectProperty<>();

    private ObjectProperty<Date> date = new SimpleObjectProperty<>();

    private ObjectProperty<Employee> employee = new SimpleObjectProperty<>();

    private ObjectProperty<Employee> verifier = new SimpleObjectProperty<>();

    public Long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public Boolean getClockin() {
        return clockin.get();
    }

    public ObjectProperty<Boolean> clockinProperty() {
        return clockin;
    }

    public void setClockin(Boolean clockin) {
        this.clockin.set(clockin);
    }

    public Date getDate() {
        return date.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public Employee getEmployee() {
        return employee.get();
    }

    public ObjectProperty<Employee> employeeProperty() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee.set(employee);
    }

    public Employee getVerifier() {
        return verifier.get();
    }

    public ObjectProperty<Employee> verifierProperty() {
        return verifier;
    }

    public void setVerifier(Employee verifier) {
        this.verifier.set(verifier);
    }
}
