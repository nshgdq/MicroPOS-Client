package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbMenuItemsPresenter extends DbEntityPresenter<MenuItem> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    TextField tfCategory;
    TextField tfPrinters;
    TextField tfWeight;

    TableColumn<MenuItem, String> name;
    TableColumn<MenuItem, String> tag;
    TableColumn<MenuItem, String> price;
    TableColumn<MenuItem, String> category;
    TableColumn<MenuItem, String> printers;
    TableColumn<MenuItem, String> weight;

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfPrice = createTextField("Price");
        tfCategory = createTextField("Categories");
        tfPrinters = createTextField("Printers");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Menu Item Information"), tfName, tfTag, tfPrice, tfCategory, tfPrinters, tfWeight};
    }

    @Override
    TableColumn<MenuItem, String>[] getTableColumns() {

        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        price = createTableColumn("Price", param -> param.getValue().priceProperty().asString());
        category = createTableColumn("Category", param -> param.getValue().categorySummaryProperty());
        printers = createTableColumn("Printers", param -> param.getValue().printersProperty().asString());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{name, tag, price, category, printers, weight};
    }

    @Override
    void unbindItem(MenuItem currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        tfCategory.textProperty().unbindBidirectional(currentItem.getCategory().idProperty());
        tfPrinters.textProperty().unbindBidirectional(currentItem.printersProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(MenuItem newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), priceConverter);
        tfCategory.textProperty().bindBidirectional(newItem.getCategory().idProperty(), idConverter);
        tfPrinters.textProperty().bindBidirectional(newItem.printersProperty(), listConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearControls() {
        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        tfCategory.setText("");
        tfPrinters.setText("");
        tfWeight.setText("");
    }

    @Override
    MenuItem createNew() {
        MenuItem menuItem = new MenuItem();
        menuItem.setCategory(new Category());
        return menuItem;
    }

    @Override
    void updateTableContent(TableView<MenuItem> table) {
        App.apiProxy.listMenuItems(new RunLaterCallback<List<MenuItem>>() {
            @Override
            public void laterSuccess(List<MenuItem> menuItems) {
                table.setItems(FXCollections.observableList(menuItems));
            }
        });
    }

    @Override
    void submitItem(MenuItem item) {
        App.apiProxy.updateMenuItem(item, RunLaterCallback.submitCallback());
    }

    @Override
    void deleteItem(MenuItem item) {
        App.apiProxy.removeMenuItem(item.getId(), RunLaterCallback.deleteCallback());
    }

}
