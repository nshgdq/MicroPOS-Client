package ow.micropos.client.desktop.presenter.database;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class DbMenuItemsPresenter extends ItemPresenter<MenuItem> {

    @FXML TextField tfName;
    @FXML TextField tfTag;
    @FXML TextField tfPrice;
    @FXML TextField tfCategory;
    @FXML TextField tfPrinters;
    @FXML TableView<MenuItem> menuItemsTable;
    @FXML TableColumn<MenuItem, String> name;
    @FXML TableColumn<MenuItem, String> tag;
    @FXML TableColumn<MenuItem, String> price;
    @FXML TableColumn<MenuItem, String> category;
    @FXML TableColumn<MenuItem, String> date;
    @FXML TableColumn<MenuItem, String> archived;
    @FXML TableColumn<MenuItem, String> archiveDate;
    @FXML TableColumn<MenuItem, String> printers;

    @Override
    public void initialize() {
        menuItemsTable.setItems(FXCollections.emptyObservableList());
        menuItemsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                setItem(newValue);
        });

        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        price.setCellValueFactory(param -> param.getValue().priceProperty().asString());
        category.setCellValueFactory(param -> param.getValue().getCategory().nameProperty());
        date.setCellValueFactory(param -> param.getValue().dateProperty().asString());
        archived.setCellValueFactory(param -> param.getValue().archivedProperty().asString());
        archiveDate.setCellValueFactory(param -> param.getValue().archiveDateProperty().asString());
        printers.setCellValueFactory(param -> param.getValue().printersProperty().asString());

    }

    @Override
    protected void updateItem(MenuItem currentItem, MenuItem newItem) {
        if (newItem == null) {
            tfName.setText("");
            tfTag.setText("");
            tfPrice.setText("");
            tfCategory.setText("");
            tfPrinters.setText("");
        } else {
            tfName.setText(newItem.getName());
            tfTag.setText(newItem.getTag());
            tfPrice.setText(newItem.getPrice().toString());
            tfCategory.setText(newItem.getCategory().getId().toString());
            tfPrinters.setText(newItem.getPrinters().toString());

        }
    }

    @Override
    public void refresh() {
        setItem(null);
        App.api.getMenuItems(new AlertCallback<List<MenuItem>>() {
            @Override
            public void onSuccess(List<MenuItem> menuItems, Response response) {
                menuItemsTable.setItems(FXCollections.observableList(menuItems));
            }
        });
    }

    @Override
    public void dispose() {

    }
}
