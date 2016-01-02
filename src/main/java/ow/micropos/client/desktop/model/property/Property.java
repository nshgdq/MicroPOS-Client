package ow.micropos.client.desktop.model.property;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Property {

    private StringProperty key = new SimpleStringProperty();

    private StringProperty value = new SimpleStringProperty();

    public Property(String key, String value) {
        this.key.set(key);
        this.value.set(value);
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
