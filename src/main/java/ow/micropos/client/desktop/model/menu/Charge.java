package ow.micropos.client.desktop.model.menu;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.orders.ChargeEntry;

import java.math.BigDecimal;
import java.util.List;

public class Charge {

    public Charge() {}

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private StringProperty tag = new SimpleStringProperty();

    private IntegerProperty weight = new SimpleIntegerProperty();

    private ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    private ObjectProperty<ChargeType> type = new SimpleObjectProperty<>();

    private ListProperty<ChargeEntry> chargeEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

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

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public ChargeType getType() {
        return type.get();
    }

    public ObjectProperty<ChargeType> typeProperty() {
        return type;
    }

    public void setType(ChargeType type) {
        this.type.set(type);
    }

    public boolean hasType(ChargeType type) {
        return this.type.get() == type;
    }

    public ObservableList<ChargeEntry> getChargeEntries() {
        return chargeEntries.get();
    }

    public ListProperty<ChargeEntry> chargeEntriesProperty() {
        return chargeEntries;
    }

    public void setChargeEntries(List<ChargeEntry> chargeEntries) {
        this.chargeEntries.setAll(chargeEntries);
    }

    public BigDecimal applyAmountTo(BigDecimal price) {
        switch (type.get()) {
            case FIXED_AMOUNT:
                return getAmount();
            case PERCENTAGE:
                return price.multiply(getAmount());
            default:
                throw new RuntimeException("Charge must have type.");
        }
    }

}
