package ow.micropos.client.desktop.model.menu;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Category {

    public Category() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private StringProperty tag = new SimpleStringProperty();

    private IntegerProperty weight = new SimpleIntegerProperty();

    private ListProperty<MenuItem> menuItems = new SimpleListProperty<>(FXCollections.observableArrayList());

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
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

    public ObservableList<MenuItem> getMenuItems() {
        return menuItems.get();
    }

    public ListProperty<MenuItem> menuItemsProperty() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems.setAll(menuItems);
    }

}
