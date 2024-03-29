package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.target.Section;
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

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
    Label getTitleLabel() {
        return new Label("Section Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{new Label("Name"), new Label("Tag"), new Label("Rows"), new Label("Cols"), new Label("Weight")};
    }

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfRows = createTextField("Rows");
        tfCols = createTextField("Cols");
        tfWeight = createTextField("Weight");

        return new Node[]{tfName, tfTag, tfRows, tfCols, tfWeight};
    }

    @Override
    TableColumn<Section, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString(), ComparatorUtils.idComparator);
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty(), ComparatorUtils.tagComparator);
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
    void toggleControls(boolean visible) {
        tfName.setVisible(visible);
        tfTag.setVisible(visible);
        tfRows.setVisible(visible);
        tfCols.setVisible(visible);
        tfWeight.setVisible(visible);

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
        App.apiProxy.listSections(new RunLaterCallback<List<Section>>() {
            @Override
            public void laterSuccess(List<Section> sections) {
                table.setItems(FXCollections.observableList(sections));
            }
        });
    }

    @Override
    void submitItem(Section item) {
        App.apiProxy.updateSection(item, RunLaterCallback.submitCallback(item, table, (id, o) -> o.setId(id)));
    }

    @Override
    void deleteItem(Section item) {
        App.apiProxy.removeSection(item.getId(), RunLaterCallback.deleteCallback(item, table));
    }
}
