package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbModifierPresenter extends DbEntityPresenter<Modifier> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    ComboBox<ModifierType> cbType;
    TextField tfGroup;
    TextField tfWeight;

    TableColumn<Modifier, String> name;
    TableColumn<Modifier, String> tag;
    TableColumn<Modifier, String> price;
    TableColumn<Modifier, String> type;
    TableColumn<Modifier, String> group;
    TableColumn<Modifier, String> weight;

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfPrice = createTextField("Price");
        cbType = createComboBox("Type", ModifierType.values());
        tfGroup = createTextField("Group");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Modifier Information"), tfName, tfTag, tfPrice, cbType, tfGroup, tfWeight};
    }

    @Override
    TableColumn<Modifier, String>[] getTableColumns() {
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        price = createTableColumn("Price", param -> param.getValue().priceProperty().asString());
        type = createTableColumn("Type", param -> param.getValue().typeProperty().asString());
        group = createTableColumn("Group", param -> param.getValue().modifierGroupSummaryProperty());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{name, tag, price, type, group, weight};
    }

    @Override
    void unbindItem(Modifier currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        cbType.valueProperty().unbindBidirectional(currentItem.typeProperty());
        tfGroup.textProperty().unbindBidirectional(currentItem.getModifierGroup().nameProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Modifier newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), priceConverter);
        cbType.valueProperty().bindBidirectional(newItem.typeProperty());
        tfGroup.textProperty().bindBidirectional(newItem.getModifierGroup().idProperty(), idConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearControls() {
        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        cbType.getSelectionModel().clearSelection();
        tfGroup.setText("");
        tfWeight.setText("");
    }

    @Override
    Modifier createNew() {
        Modifier modifier = new Modifier();
        modifier.setModifierGroup(new ModifierGroup());
        return modifier;
    }

    @Override
    void updateTableContent(TableView<Modifier> table) {
        App.apiProxy.listModifiers(new RunLaterCallback<List<Modifier>>() {
            @Override
            public void laterSuccess(List<Modifier> modifiers) {
                table.setItems(FXCollections.observableList(modifiers));
            }
        });
    }

    @Override
    void submitItem(Modifier item) {
        App.apiProxy.updateModifier(item, RunLaterCallback.submitCallback());
    }

    @Override
    void deleteItem(Modifier item) {
        App.apiProxy.removeModifier(item.getId(), RunLaterCallback.deleteCallback());
    }
}
