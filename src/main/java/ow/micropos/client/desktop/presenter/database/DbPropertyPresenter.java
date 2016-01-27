package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.property.Property;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DbPropertyPresenter extends DbEntityPresenter<Property> {

    private TextField tfKey;
    private TextField tfValue;
    private ColorPicker cpColor;

    private TableColumn<Property, String> tcKey;
    private TableColumn<Property, String> tcValue;

    @Override
    Label getTitleLabel() {
        return new Label("Property Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{new Label("Key"), new Label("Value")};
    }


    @Override
    Node[] getEditControls() {
        tfKey = createTextField("Key");
        tfValue = createTextField("Value");
        cpColor = new ColorPicker();

        return new Node[]{tfKey, tfValue, cpColor};
    }

    @Override
    TableColumn<Property, String>[] getTableColumns() {
        tcKey = createTableColumn("Key", param -> param.getValue().keyProperty());
        tcValue = createTableColumn("Value", param -> param.getValue().valueProperty());
        return new TableColumn[]{tcKey, tcValue};
    }

    @Override
    void unbindItem(Property currentItem) {
        tfKey.textProperty().unbindBidirectional(currentItem.keyProperty());
        tfValue.textProperty().unbindBidirectional(currentItem.valueProperty());
        currentItem.valueProperty().unbindBidirectional(cpColor.valueProperty());
    }

    @Override
    void bindItem(Property newItem) {
        tfKey.textProperty().bindBidirectional(newItem.keyProperty());
        if (newItem.getValue().matches("#[0-9a-fA-F]{6}")) {
            cpColor.setVisible(true);
            tfValue.setVisible(false);
            cpColor.setValue(Color.web(newItem.getValue()));
            newItem.valueProperty().bindBidirectional(cpColor.valueProperty(), colorConverter);
        } else {
            cpColor.setVisible(false);
            tfValue.setVisible(true);
            tfValue.textProperty().bindBidirectional(newItem.valueProperty());
        }
    }

    @Override
    void toggleControls(boolean visible) {
        tfKey.setVisible(visible);
        tfValue.setVisible(visible);

        tfKey.setText("");
        tfValue.setText("");
    }

    @Override
    Property createNew() {
        return new Property("", "");
    }

    @Override
    void updateTableContent(TableView<Property> table) {
        if (!App.employee.hasPermission(Permission.CLIENT_SETTINGS)) {
            App.notify.showAndWait(Permission.CLIENT_SETTINGS);
        } else {
            List<Property> properties = App.properties.getProperties()
                    .stream()
                    .map(key -> new Property(key, App.properties.getStr(key)))
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(properties));
        }
    }

    @Override
    void submitItem(Property item) {
        if (!App.employee.hasPermission(Permission.CLIENT_SETTINGS)) {
            App.notify.showAndWait(Permission.CLIENT_SETTINGS);
        } else {
            App.confirm.showAndWait("Are you sure you want to make these changes?", () -> {
                App.properties.add(item.getKey(), item.getValue());
                App.properties.save(new File(System.getProperty("user.dir") + "/app.properties"));
            });
        }
    }

    @Override
    void deleteItem(Property item) {
        App.notify.showAndWait("Deleting properties disabled.");
    }

    @Override
    protected void onDone() {
        App.confirm.showAndWait("You will have to restart for changes to take effect.", App.main::backRefresh);
    }
}
