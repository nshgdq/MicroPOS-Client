package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.menu.Charge;
import retrofit.client.Response;

import java.util.List;

public class DbChargePresenter extends DbEntityPresenter<Charge> {

    TextField tfName;
    TextField tfTag;
    TextField tfType;
    TextField tfAmount;
    TextField tfWeight;

    TableColumn<Charge, String> name;
    TableColumn<Charge, String> tag;
    TableColumn<Charge, String> type;
    TableColumn<Charge, String> amount;
    TableColumn<Charge, String> weight;

    @Override
    Node[] getTextFields() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfType = createTextField("Type");
        tfAmount = createTextField("Amount");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Charge Information"), tfName, tfTag, tfType, tfAmount, tfWeight};
    }

    @Override
    TableColumn<Charge, String>[] getTableColumns() {
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        type = createTableColumn("Type", param -> param.getValue().typeProperty().asString());
        amount = createTableColumn("Amount", param -> param.getValue().amountProperty().asString());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{name, tag, type, amount, weight};
    }

    @Override
    void unbindItem(Charge currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfType.textProperty().unbindBidirectional(currentItem.typeProperty());
        tfAmount.textProperty().unbindBidirectional(currentItem.amountProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Charge newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfType.textProperty().bindBidirectional(newItem.typeProperty(), chargeTypeConverter);
        tfAmount.textProperty().bindBidirectional(newItem.amountProperty(), priceConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfType.setText("");
        tfAmount.setText("");
        tfWeight.setText("");
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

    /******************************************************************
     *                                                                *
     * Converter
     *                                                                *
     ******************************************************************/

    private static final StringConverter<ChargeType> chargeTypeConverter = new StringConverter<ChargeType>() {
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
    };
}
