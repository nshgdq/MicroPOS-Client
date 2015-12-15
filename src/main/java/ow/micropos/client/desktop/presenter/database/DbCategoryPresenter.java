package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class DbCategoryPresenter extends DbEntityPresenter<Category> {

    TextField tfName;
    TextField tfTag;
    TableColumn<Category, String> id;
    TableColumn<Category, String> name;
    TableColumn<Category, String> tag;

    @Override
    Node[] getTextFields() {
        tfName = new TextField();
        tfTag = new TextField();

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");

        return new Node[]{new Label("Category Information"), tfName, tfTag};
    }

    @Override
    TableColumn<Category, String>[] getTableColumns() {
        id = new TableColumn<>("ID");
        name = new TableColumn<>("Name");
        tag = new TableColumn<>("Tag");

        id.setCellValueFactory(param -> param.getValue().idProperty().asString());
        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());

        return new TableColumn[]{id, name, tag};
    }

    @Override
    void unbindItem(Category currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
    }

    @Override
    void bindItem(Category newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
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
