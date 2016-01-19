package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbModifierGroupPresenter extends DbEntityPresenter<ModifierGroup> {

    private TextField tfName;
    private TextField tfTag;
    private TextField tfWeight;

    private TableColumn<ModifierGroup, String> tcId;
    private TableColumn<ModifierGroup, String> tcName;
    private TableColumn<ModifierGroup, String> tcTag;
    private TableColumn<ModifierGroup, String> tcWeight;

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Modifier Group Information"), tfName, tfTag, tfWeight};
    }

    @Override
    TableColumn<ModifierGroup, String>[] getTableColumns() {
        tcId = createTableColumn("ID", param -> param.getValue().idProperty().asString());
        tcName = createTableColumn("Name", param -> param.getValue().nameProperty());
        tcTag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        tcWeight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{tcId, tcName, tcTag, tcWeight};
    }

    @Override
    void unbindItem(ModifierGroup currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(ModifierGroup newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearControls() {
        tfName.setText("");
        tfTag.setText("");
        tfWeight.setText("");
    }

    @Override
    ModifierGroup createNew() {
        return new ModifierGroup();
    }

    @Override
    void updateTableContent(TableView<ModifierGroup> table) {
        App.apiProxy.getModifierGroups(new RunLaterCallback<List<ModifierGroup>>() {
            @Override
            public void laterSuccess(List<ModifierGroup> modifierGroups) {
                table.setItems(FXCollections.observableList(modifierGroups));
            }
        });
    }

    @Override
    void submitItem(ModifierGroup item) {
        App.apiProxy.updateModifierGroup(item, RunLaterCallback.submitCallback());
    }

    @Override
    void deleteItem(ModifierGroup item) {
        App.apiProxy.removeModifierGroup(item.getId(), RunLaterCallback.deleteCallback());
    }
}
