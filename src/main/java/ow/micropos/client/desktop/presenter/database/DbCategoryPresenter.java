package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbCategoryPresenter extends DbEntityPresenter<Category> {

    TextField tfName;
    TextField tfTag;
    TextField tfWeight;
    TableColumn<Category, String> id;
    TableColumn<Category, String> name;
    TableColumn<Category, String> tag;
    TableColumn<Category, String> weight;

    @Override
    Label getTitleLabel() {
        return new Label("Category Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{new Label("Name"), new Label("Tag"), new Label("Weight")};
    }

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfWeight = createTextField("Weight");

        return new Node[]{tfName, tfTag, tfWeight};
    }

    @Override
    TableColumn<Category, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString(), ComparatorUtils.idComparator);
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty(), ComparatorUtils.tagComparator);
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{id, name, tag, weight};
    }

    @Override
    void unbindItem(Category currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Category newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void toggleControls(boolean visible) {
        tfName.setVisible(visible);
        tfTag.setVisible(visible);
        tfWeight.setVisible(visible);
        tfName.setText("");
        tfTag.setText("");
        tfWeight.setText("");
    }

    @Override
    Category createNew() {
        return new Category();
    }

    @Override
    void updateTableContent(TableView<Category> table) {
        App.apiProxy.listCategories(new RunLaterCallback<List<Category>>() {
            @Override
            public void laterSuccess(List<Category> categories) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Category item) {
        App.apiProxy.updateCategory(item, RunLaterCallback.submitCallback(item, table, (id, o) -> o.setId(id)));
    }

    @Override
    void deleteItem(Category item) {
        App.apiProxy.removeCategory(item.getId(), RunLaterCallback.deleteCallback(item, table));
    }
}
