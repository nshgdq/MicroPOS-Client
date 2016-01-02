package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.target.Customer;
import retrofit.client.Response;

import java.util.List;

public class DbCustomerPresenter extends DbEntityPresenter<Customer> {

    TextField tfFirst;
    TextField tfLast;
    TextField tfPhone;

    TableColumn<Customer, String> first;
    TableColumn<Customer, String> last;
    TableColumn<Customer, String> phone;

    @Override
    Node[] getEditControls() {
        tfFirst = createTextField("First Name");
        tfLast = createTextField("Last Name");
        tfPhone = createTextField("Phone Number");

        return new Node[]{new Label("Customer Information"), tfFirst, tfLast, tfPhone};
    }

    @Override
    TableColumn<Customer, String>[] getTableColumns() {
        first = createTableColumn("First Name", param -> param.getValue().firstNameProperty());
        last = createTableColumn("Last Name", param -> param.getValue().lastNameProperty());
        phone = createTableColumn("Phone Number", param -> param.getValue().phoneNumberProperty());

        return new TableColumn[]{first, last, phone};
    }

    @Override
    void unbindItem(Customer currentItem) {
        tfFirst.textProperty().unbindBidirectional(currentItem.firstNameProperty());
        tfLast.textProperty().unbindBidirectional(currentItem.lastNameProperty());
        tfPhone.textProperty().unbindBidirectional(currentItem.phoneNumberProperty());
    }

    @Override
    void bindItem(Customer newItem) {
        tfFirst.textProperty().bindBidirectional(newItem.firstNameProperty());
        tfLast.textProperty().bindBidirectional(newItem.lastNameProperty());
        tfPhone.textProperty().bindBidirectional(newItem.phoneNumberProperty());
    }

    @Override
    void clearControls() {
        tfFirst.setText("");
        tfLast.setText("");
        tfPhone.setText("");
    }

    @Override
    Customer createNew() {
        return new Customer();
    }

    @Override
    void updateTableContent(TableView<Customer> table) {
        App.api.listCustomers(new AlertCallback<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Customer item) {
        App.api.updateCustomer(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Customer.");
                }
        );
    }

    @Override
    void deleteItem(Customer item) {
        App.api.removeCustomer(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Customer.");
        });
    }
}
