package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class DbModifierGroupPresenter extends DbEntityPresenter<ModifierGroup> {

    private TextField tfName;
    private TextField tfTag;

    private TableColumn<ModifierGroup, String> tcId;
    private TableColumn<ModifierGroup, String> tcName;
    private TableColumn<ModifierGroup, String> tcTag;

    @Override
    Node[] getTextFields() {
        tfName = new TextField();
        tfTag = new TextField();

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");
        return new Node[]{new Label("Modifier Group Information"), tfName, tfTag};
    }

    @Override
    TableColumn<ModifierGroup, String>[] getTableColumns() {
        tcId = new TableColumn<>("ID");
        tcName = new TableColumn<>("Name");
        tcTag = new TableColumn<>("Tag");

        tcId.setCellValueFactory(param -> param.getValue().idProperty().asString());
        tcName.setCellValueFactory(param -> param.getValue().nameProperty());
        tcTag.setCellValueFactory(param -> param.getValue().tagProperty());
        return new TableColumn[]{tcId, tcName, tcTag};
    }

    @Override
    void unbindItem(ModifierGroup currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
    }

    @Override
    void bindItem(ModifierGroup newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
    }

    @Override
    ModifierGroup createNew() {
        return new ModifierGroup();
    }

    @Override
    void updateTableContent(TableView<ModifierGroup> table) {
        App.api.getModifierGroups(new AlertCallback<List<ModifierGroup>>() {
            @Override
            public void onSuccess(List<ModifierGroup> modifierGroups, Response response) {
                table.setItems(FXCollections.observableList(modifierGroups));
            }
        });
    }

    @Override
    void submitItem(ModifierGroup item) {
        App.api.updateModifierGroup(item, new AlertCallback<Long>() {
            @Override
            public void onSuccess(Long aLong, Response response) {
                refresh();
                App.notify.showAndWait("Modifier Group Submitted.");
            }
        });
    }

    @Override
    void deleteItem(ModifierGroup item) {
        App.api.removeModifierGroup(item.getId(), new AlertCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean, Response response) {
                refresh();
                App.notify.showAndWait("Modifier Group Removed.");
            }
        });

    }
}
