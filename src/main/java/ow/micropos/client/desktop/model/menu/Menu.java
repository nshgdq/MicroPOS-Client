package ow.micropos.client.desktop.model.menu;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Menu {

    public Menu() {}

    private ListProperty<Category> categories = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<ModifierGroup> modifierGroups = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObservableList<Category> getCategories() {
        return categories.get();
    }

    public ListProperty<Category> categoriesProperty() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories.setAll(categories);
    }

    public ObservableList<ModifierGroup> getModifierGroups() {
        return modifierGroups.get();
    }

    public ListProperty<ModifierGroup> modifierGroupsProperty() {
        return modifierGroups;
    }

    public void setModifierGroups(List<ModifierGroup> modifierGroups) {
        this.modifierGroups.setAll(modifierGroups);
    }
}
