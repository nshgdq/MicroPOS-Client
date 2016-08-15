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
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbMenuItemsPresenter extends DbEntityPresenter<MenuItem> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    TextField tfTaxed;
    TextField tfCategory;
    TextField tfPrinters;
    TextField tfWeight;

    TableColumn<MenuItem, String> name;
    TableColumn<MenuItem, String> tag;
    TableColumn<MenuItem, String> price;
    TableColumn<MenuItem, String> taxed;
    TableColumn<MenuItem, String> category;
    TableColumn<MenuItem, String> printers;
    TableColumn<MenuItem, String> weight;

    @Override
    Label getTitleLabel() {
        return new Label("Menu Item Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{
                new Label("Name"),
                new Label("Tag"),
                new Label("Price"),
                new Label("Taxed"),
                new Label("Categories"),
                new Label("Printers"),
                new Label("Weight")
        };
    }


    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfPrice = createTextField("Price");
        tfTaxed = createTextField("Taxed");
        tfCategory = createTextField("Categories");
        tfPrinters = createTextField("Printers");
        tfWeight = createTextField("Weight");

        return new Node[]{tfName, tfTag, tfPrice, tfTaxed, tfCategory, tfPrinters, tfWeight};
    }

    @Override
    TableColumn<MenuItem, String>[] getTableColumns() {

        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty(), ComparatorUtils.tagComparator);
        price = createTableColumn("Price", param -> param.getValue().priceProperty().asString());
        taxed = createTableColumn("Taxed", param -> param.getValue().taxedProperty().asString());
        category = createTableColumn("Category", param -> param.getValue().categorySummaryProperty());
        printers = createTableColumn("Printers", param -> param.getValue().printersProperty().asString());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        name.setMaxWidth(300);
        name.setMinWidth(300);
        name.setPrefWidth(300);

        return new TableColumn[]{name, tag, price, taxed, category, printers, weight};
    }

    @Override
    void unbindItem(MenuItem currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        tfTaxed.textProperty().unbindBidirectional(currentItem.taxedProperty());
        tfCategory.textProperty().unbindBidirectional(currentItem.getCategory().idProperty());
        tfPrinters.textProperty().unbindBidirectional(currentItem.printersProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(MenuItem newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), priceConverter);
        tfTaxed.textProperty().bindBidirectional(newItem.taxedProperty(), booleanConverter);
        tfCategory.textProperty().bindBidirectional(newItem.getCategory().idProperty(), idConverter);
        tfPrinters.textProperty().bindBidirectional(newItem.printersProperty(), listConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void toggleControls(boolean visible) {
        tfName.setVisible(visible);
        tfTag.setVisible(visible);
        tfPrice.setVisible(visible);
        tfTaxed.setVisible(visible);
        tfCategory.setVisible(visible);
        tfPrinters.setVisible(visible);
        tfWeight.setVisible(visible);

        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        tfTaxed.setText("");
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
        App.apiProxy.updateMenuItem(item, RunLaterCallback.submitCallback(item, table, (id, o) -> o.setId(id)));
    }

    @Override
    void deleteItem(MenuItem item) {
        App.apiProxy.removeMenuItem(item.getId(), RunLaterCallback.deleteCallback(item, table));
    }

}
