package ow.micropos.client.desktop.model.seating;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Section {

    public Section() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private ListProperty<Seat> seats = new SimpleListProperty<>(FXCollections.observableArrayList());

    private StringProperty tag = new SimpleStringProperty();

    private IntegerProperty rows = new SimpleIntegerProperty();

    private IntegerProperty cols = new SimpleIntegerProperty();

    public int getRows() {
        return rows.get();
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows.set(rows);
    }

    public int getCols() {
        return cols.get();
    }

    public IntegerProperty colsProperty() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols.set(cols);
    }

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

    public String getTag() {
        return tag.get();
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public ObservableList<Seat> getSeats() {
        return seats.get();
    }

    public ListProperty<Seat> seatsProperty() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats.setAll(seats);
    }

}
