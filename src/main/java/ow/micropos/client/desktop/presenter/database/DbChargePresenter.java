package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.menu.Charge;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.List;

public class DbChargePresenter extends DbEntityPresenter<Charge> {

    TextField tfName;
    TextField tfTag;
    TextField tfType;
    TextField tfAmount;

    TableColumn<Charge, String> name;
    TableColumn<Charge, String> tag;
    TableColumn<Charge, String> type;
    TableColumn<Charge, String> amount;

    @Override
    Node[] getTextFields() {
        tfName = new TextField();
        tfTag = new TextField();
        tfType = new TextField();
        tfAmount = new TextField();

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");
        tfType.setPromptText("Type");
        tfAmount.setPromptText("Amount");

        return new Node[]{new Label("Charge Information"), tfName, tfTag, tfType, tfAmount};
    }

    @Override
    TableColumn<Charge, String>[] getTableColumns() {
        name = new TableColumn<>("Name");
        tag = new TableColumn<>("Tag");
        type = new TableColumn<>("Type");
        amount = new TableColumn<>("Amount");

        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        type.setCellValueFactory(param -> param.getValue().typeProperty().asString());
        amount.setCellValueFactory(param -> param.getValue().amountProperty().asString());

        return new TableColumn[]{name, tag, type, amount};
    }

    @Override
    void unbindItem(Charge currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfType.textProperty().unbindBidirectional(currentItem.typeProperty());
        tfAmount.textProperty().unbindBidirectional(currentItem.amountProperty());
    }

    @Override
    void bindItem(Charge newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfType.textProperty().bindBidirectional(newItem.typeProperty(), new StringConverter<ChargeType>() {
            @Override
            public String toString(ChargeType object) {
                if (object == null)
                    return "";
                switch (object) {
                    case PERCENTAGE:
                        return "P";
                    case FIXED_AMOUNT:
                        return "F";
                    default:
                        return "";
                }
            }

            @Override
            public ChargeType fromString(String string) {
                if (string == null || string.length() == 0) {
                    return null;
                } else if (string.equalsIgnoreCase("P")) {
                    return ChargeType.PERCENTAGE;
                } else if (string.equalsIgnoreCase("F")) {
                    return ChargeType.FIXED_AMOUNT;
                } else {
                    return null;
                }
            }
        });
        tfAmount.textProperty().bindBidirectional(newItem.amountProperty(), new StringConverter<BigDecimal>() {
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
        });
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfType.setText("");
        tfAmount.setText("");
    }

    @Override
    Charge createNew() {
        return new Charge();
    }

    @Override
    void updateTableContent(TableView<Charge> table) {
        App.api.listCharges(new AlertCallback<List<Charge>>() {
            @Override
            public void onSuccess(List<Charge> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Charge item) {
        App.api.updateCharge(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(Charge item) {
        App.api.removeCharge(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }
}
