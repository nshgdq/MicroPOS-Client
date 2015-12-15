package ow.micropos.client.desktop.model.menu;

import javafx.beans.property.*;
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.orders.ProductEntry;

import java.math.BigDecimal;
import java.util.List;

public class Modifier {

    public Modifier() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private StringProperty tag = new SimpleStringProperty();

    private ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();

    private ObjectProperty<ModifierType> type = new SimpleObjectProperty<>();

    private ObjectProperty<ModifierGroup> modifierGroup = new SimpleObjectProperty<>();

    private ListProperty<ProductEntry> salesOrderEntries = new SimpleListProperty<>();

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

    public ModifierType getType() {
        return type.get();
    }

    public ObjectProperty<ModifierType> typeProperty() {
        return type;
    }

    public void setType(ModifierType type) {
        this.type.set(type);
    }

    public ModifierGroup getModifierGroup() {
        return modifierGroup.get();
    }

    public ObjectProperty<ModifierGroup> modifierGroupProperty() {
        return modifierGroup;
    }

    public void setModifierGroup(ModifierGroup modifierGroup) {
        this.modifierGroup.set(modifierGroup);
    }

    public ListProperty<ProductEntry> salesOrderEntriesProperty() {
        return salesOrderEntries;
    }

    public void setSalesOrderEntries(List<ProductEntry> salesOrderEntries) {
        this.salesOrderEntries.setAll(salesOrderEntries);
    }

}
