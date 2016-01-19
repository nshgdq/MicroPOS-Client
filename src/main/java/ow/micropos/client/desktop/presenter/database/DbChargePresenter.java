package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.menu.Charge;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.util.List;

public class DbChargePresenter extends DbEntityPresenter<Charge> {

    TextField tfName;
    TextField tfTag;
    TextField tfAmount;
    TextField tfWeight;
    ComboBox<ChargeType> cbType;

    TableColumn<Charge, String> name;
    TableColumn<Charge, String> tag;
    TableColumn<Charge, String> type;
    TableColumn<Charge, String> amount;
    TableColumn<Charge, String> weight;

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        cbType = createComboBox("Type", ChargeType.FIXED_AMOUNT, ChargeType.PERCENTAGE);
        tfAmount = createTextField("Amount");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Charge Information"), tfName, tfTag, cbType, tfAmount, tfWeight};
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
        cbType.valueProperty().unbindBidirectional(currentItem.typeProperty());
        tfAmount.textProperty().unbindBidirectional(currentItem.amountProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Charge newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        cbType.valueProperty().bindBidirectional(newItem.typeProperty());
        tfAmount.textProperty().bindBidirectional(newItem.amountProperty(), priceConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearControls() {
        tfName.setText("");
        tfTag.setText("");
        cbType.getSelectionModel().clearSelection();
        tfAmount.setText("");
        tfWeight.setText("");
    }

    @Override
    Charge createNew() {
        return new Charge();
    }

    @Override
    void updateTableContent(TableView<Charge> table) {
        App.apiProxy.listCharges(new RunLaterCallback<List<Charge>>() {
            @Override
            public void laterSuccess(List<Charge> charges) {
                table.setItems(FXCollections.observableList(charges));
            }
        });
    }

    @Override
    void submitItem(Charge item) {
        App.apiProxy.updateCharge(item, RunLaterCallback.submitCallback());
    }

    @Override
    void deleteItem(Charge item) {
        App.apiProxy.removeCharge(item.getId(), RunLaterCallback.deleteCallback());
    }

}
