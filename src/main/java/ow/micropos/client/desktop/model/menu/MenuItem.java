package ow.micropos.client.desktop.model.menu;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ow.micropos.client.desktop.model.orders.ProductEntry;

import java.math.BigDecimal;
import java.util.List;

public class MenuItem {

    public MenuItem() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private StringProperty tag = new SimpleStringProperty();

    private ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();

    private ObjectProperty<Category> category = new SimpleObjectProperty<>();

    private ListProperty<ProductEntry> salesOrderEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<String> printers = new SimpleListProperty<>(FXCollections.observableArrayList());

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

    public BigDecimal getPrice() {
        return price.get();
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public Category getCategory() {
        return category.get();
    }

    public ObjectProperty<Category> categoryProperty() {
        return category;
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public ListProperty<ProductEntry> salesOrderEntriesProperty() {
        return salesOrderEntries;
    }

    public void setSalesOrderEntries(List<ProductEntry> salesOrderEntries) {
        this.salesOrderEntries.setAll(salesOrderEntries);
    }

    public ObservableList<String> getPrinters() {
        return printers.get();
    }

    public ListProperty<String> printersProperty() {
        return printers;
    }

    public void setPrinters(List<String> printers) {
        this.printers.setAll(printers);
    }
}
