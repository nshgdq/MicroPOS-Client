package ow.micropos.client.desktop.model.seating;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ow.micropos.client.desktop.model.orders.SalesOrder;

import java.util.List;

public class Seat {

    public Seat() {}

    public Seat(Seat seat) {
        setId(seat.getId());
        setTag(seat.getTag());
        setArchived(seat.getArchived());
        setSection(seat.getSection());
        setRow(seat.getRow());
        setCol(seat.getCol());
    }

    public Seat(Long id, String tag) {
        setId(id);
        setTag(tag);
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty tag = new SimpleStringProperty();

    private BooleanProperty archived = new SimpleBooleanProperty();

    private ObjectProperty<Section> section = new SimpleObjectProperty<>();

    private ListProperty<SalesOrder> salesOrders = new SimpleListProperty<>(FXCollections.observableArrayList());

    private IntegerProperty row = new SimpleIntegerProperty();

    private IntegerProperty col = new SimpleIntegerProperty();

    public int getRow() {
        return row.get();
    }

    public IntegerProperty rowProperty() {
        return row;
    }

    public void setRow(int row) {
        this.row.set(row);
    }

    public int getCol() {
        return col.get();
    }

    public IntegerProperty colProperty() {
        return col;
    }

    public void setCol(int col) {
        this.col.set(col);
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

    public String getTag() {
        return tag.get();
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public boolean getArchived() {
        return archived.get();
    }

    public BooleanProperty archivedProperty() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived.set(archived);
    }

    public Section getSection() {
        return section.get();
    }

    public ObjectProperty<Section> sectionProperty() {
        return section;
    }

    public void setSection(Section section) {
        this.section.set(section);
    }

    public ObservableList<SalesOrder> getSalesOrders() {
        return salesOrders.get();
    }

    public ListProperty<SalesOrder> salesOrdersProperty() {
        return salesOrders;
    }

    public void setSalesOrders(List<SalesOrder> salesOrders) {
        this.salesOrders.setAll(salesOrders);
    }

}
