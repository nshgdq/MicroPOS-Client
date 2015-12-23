package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.target.Section;
import retrofit.client.Response;

import java.util.List;

public class DbSectionPresenter extends DbEntityPresenter<Section> {

    TextField tfName;
    TextField tfTag;
    TextField tfRows;
    TextField tfCols;
    TextField tfWeight;

    TableColumn<Section, String> id;
    TableColumn<Section, String> name;
    TableColumn<Section, String> tag;
    TableColumn<Section, String> rows;
    TableColumn<Section, String> cols;
    TableColumn<Section, String> weight;

    @Override
    Node[] getTextFields() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfRows = createTextField("Rows");
        tfCols = createTextField("Cols");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Section Information"), tfName, tfTag, tfRows, tfCols, tfWeight};
    }

    @Override
    TableColumn<Section, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString());
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        rows = createTableColumn("Rows", param -> param.getValue().rowsProperty().asString());
        cols = createTableColumn("Cols", param -> param.getValue().colsProperty().asString());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{id, name, tag, rows, cols, weight};
    }

    @Override
    void unbindItem(Section currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfRows.textProperty().unbindBidirectional(currentItem.rowsProperty());
        tfCols.textProperty().unbindBidirectional(currentItem.colsProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Section newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfRows.textProperty().bindBidirectional(newItem.rowsProperty(), numberConverter);
        tfCols.textProperty().bindBidirectional(newItem.colsProperty(), numberConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfRows.setText("");
        tfCols.setText("");
        tfWeight.setText("");
    }

    @Override
    Section createNew() {
        return new Section();
    }

    @Override
    void updateTableContent(TableView<Section> table) {
        App.api.listSections(new AlertCallback<List<Section>>() {
            @Override
            public void onSuccess(List<Section> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Section item) {
        App.api.updateSection(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(Section item) {
        App.api.removeSection(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }
}
