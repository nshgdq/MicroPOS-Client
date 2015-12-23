package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.menu.Category;
import retrofit.client.Response;

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
    Node[] getTextFields() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Category Information"), tfName, tfTag, tfWeight};
    }

    @Override
    TableColumn<Category, String>[] getTableColumns() {
        id = createTableColumn("ID", param -> param.getValue().idProperty().asString());
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
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
    void clearFields() {
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
        App.api.listCategories(new AlertCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Category item) {
        App.api.updateCategory(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(Category item) {
        App.api.removeCategory(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }
}
