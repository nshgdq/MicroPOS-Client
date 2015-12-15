package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.target.Section;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class DbSectionPresenter extends DbEntityPresenter<Section> {

    TextField tfName;
    TextField tfTag;
    TextField tfRows;
    TextField tfCols;
    TableColumn<Section, String> id;
    TableColumn<Section, String> name;
    TableColumn<Section, String> tag;
    TableColumn<Section, String> rows;
    TableColumn<Section, String> cols;

    @Override
    Node[] getTextFields() {
        tfName = new TextField();
        tfTag = new TextField();
        tfRows = new TextField();
        tfCols = new TextField();

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");
        tfRows.setPromptText("Rows");
        tfCols.setPromptText("Cols");

        return new Node[]{new Label("Section Information"), tfName, tfTag, tfRows, tfCols};
    }

    @Override
    TableColumn<Section, String>[] getTableColumns() {
        id = new TableColumn<>("ID");
        name = new TableColumn<>("Name");
        tag = new TableColumn<>("Tag");
        rows = new TableColumn<>("Rows");
        cols = new TableColumn<>("Cols");

        id.setCellValueFactory(param -> param.getValue().idProperty().asString());
        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        rows.setCellValueFactory(param -> param.getValue().rowsProperty().asString());
        cols.setCellValueFactory(param -> param.getValue().colsProperty().asString());

        return new TableColumn[]{id, name, tag, rows, cols};
    }

    @Override
    void unbindItem(Section currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfRows.textProperty().unbindBidirectional(currentItem.rowsProperty());
        tfCols.textProperty().unbindBidirectional(currentItem.colsProperty());
    }

    @Override
    void bindItem(Section newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfRows.textProperty().bindBidirectional(newItem.rowsProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object == null)
                    return "";
                else
                    return Integer.toString(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
        tfCols.textProperty().bindBidirectional(newItem.colsProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object == null)
                    return "";
                else
                    return Integer.toString(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfRows.setText("");
        tfCols.setText("");
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
