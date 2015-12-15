package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class DbMenuItemsPresenter extends DbEntityPresenter<MenuItem> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    TextField tfCategory;
    TextField tfPrinters;

    TableColumn<MenuItem, String> name;
    TableColumn<MenuItem, String> tag;
    TableColumn<MenuItem, String> price;
    TableColumn<MenuItem, String> category;
    TableColumn<MenuItem, String> printers;

    @Override
    Node[] getTextFields() {
        tfName = new TextField("Name");
        tfTag = new TextField("Tag");
        tfPrice = new TextField("Price");
        tfCategory = new TextField("Categories");
        tfPrinters = new TextField("Printers");

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");
        tfPrice.setPromptText("Price");
        tfCategory.setPromptText("Categories");
        tfPrinters.setPromptText("Printers");

        return new Node[]{new Label("Menu Item Information"), tfName, tfTag, tfPrice, tfCategory, tfPrinters};
    }

    @Override
    TableColumn<MenuItem, String>[] getTableColumns() {
        name = new TableColumn<>("Name");
        tag = new TableColumn<>("Tag");
        price = new TableColumn<>("Price");
        category = new TableColumn<>("Category");
        printers = new TableColumn<>("Printers");

        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        price.setCellValueFactory(param -> param.getValue().priceProperty().asString());
        category.setCellValueFactory(param -> param.getValue().getCategory().nameProperty());
        printers.setCellValueFactory(param -> param.getValue().printersProperty().asString());

        return new TableColumn[]{name, tag, price, category, printers};
    }

    @Override
    void unbindItem(MenuItem currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        tfCategory.textProperty().unbindBidirectional(currentItem.getCategory().idProperty());
        tfPrinters.textProperty().unbindBidirectional(currentItem.printersProperty());
    }

    @Override
    void bindItem(MenuItem newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), priceConverter);
        tfCategory.textProperty().bindBidirectional(newItem.getCategory().idProperty(), idConverter);
        tfPrinters.textProperty().bindBidirectional(newItem.printersProperty(), listConverter);
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        tfCategory.setText("");
        tfPrinters.setText("");
    }

    @Override
    MenuItem createNew() {
        MenuItem menuItem = new MenuItem();
        menuItem.setCategory(new Category());
        return menuItem;
    }

    @Override
    void updateTableContent(TableView<MenuItem> table) {
        App.api.listMenuItems(new AlertCallback<List<MenuItem>>() {
            @Override
            public void onSuccess(List<MenuItem> menuItems, Response response) {
                table.setItems(FXCollections.observableList(menuItems));
            }
        });
    }

    @Override
    void submitItem(MenuItem item) {
        App.api.updateMenuItem(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(MenuItem item) {
        App.api.removeMenuItem(getItem().getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }

    /******************************************************************
     *                                                                *
     * Bindings
     *                                                                *
     ******************************************************************/

    // TODO : Import Apache Commons Validator instead of relying on Exceptions
    private final StringConverter<Long> idConverter = new StringConverter<Long>() {
        @Override
        public String toString(Long object) {
            return (object == null) ? "" : object.toString();
        }

        @Override
        public Long fromString(String string) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
    };

    private final StringConverter<BigDecimal> priceConverter = new StringConverter<BigDecimal>() {
        @Override
        public String toString(BigDecimal object) {
            return (object == null) ? "" : object.toString();
        }

        @Override
        public BigDecimal fromString(String string) {
            try {
                return new BigDecimal(string);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
    };

    private final StringConverter<ObservableList<String>> listConverter = new StringConverter<ObservableList<String>>() {
        @Override
        public String toString(ObservableList<String> object) {
            StringBuilder sb = new StringBuilder("");

            Iterator<String> itr = object.iterator();
            if (itr.hasNext()) {
                sb.append(itr.next());
                while (itr.hasNext())
                    sb.append(",").append(itr.next());
            }

            return sb.toString();
        }

        @Override
        public ObservableList<String> fromString(String string) {
            return FXCollections.observableArrayList(string.split(","));
        }
    };

}
