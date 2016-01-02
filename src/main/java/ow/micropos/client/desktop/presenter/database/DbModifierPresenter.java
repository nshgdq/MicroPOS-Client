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
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import retrofit.client.Response;

import java.util.List;

public class DbModifierPresenter extends DbEntityPresenter<Modifier> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    TextField tfType;
    TextField tfGroup;
    TextField tfWeight;

    TableColumn<Modifier, String> name;
    TableColumn<Modifier, String> tag;
    TableColumn<Modifier, String> price;
    TableColumn<Modifier, String> type;
    TableColumn<Modifier, String> group;
    TableColumn<Modifier, String> weight;

    @Override
    Node[] getEditControls() {
        tfName = createTextField("Name");
        tfTag = createTextField("Tag");
        tfPrice = createTextField("Price");
        tfType = createTextField("Type");
        tfGroup = createTextField("Group");
        tfWeight = createTextField("Weight");

        return new Node[]{new Label("Modifier Information"), tfName, tfTag, tfPrice, tfType, tfGroup, tfWeight};
    }

    @Override
    TableColumn<Modifier, String>[] getTableColumns() {
        name = createTableColumn("Name", param -> param.getValue().nameProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty());
        price = createTableColumn("Price", param -> param.getValue().priceProperty().asString());
        type = createTableColumn("Type", param -> param.getValue().typeProperty().asString());
        group = createTableColumn("Group", param -> param.getValue().getModifierGroup().nameProperty());
        weight = createTableColumn("Weight", param -> param.getValue().weightProperty().asString());

        return new TableColumn[]{name, tag, price, type, group, weight};
    }

    @Override
    void unbindItem(Modifier currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        tfType.textProperty().unbindBidirectional(currentItem.typeProperty());
        tfGroup.textProperty().unbindBidirectional(currentItem.getModifierGroup().nameProperty());
        tfWeight.textProperty().unbindBidirectional(currentItem.weightProperty());
    }

    @Override
    void bindItem(Modifier newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), priceConverter);
        tfType.textProperty().bindBidirectional(newItem.typeProperty(), modifierTypeConverter);
        tfGroup.textProperty().bindBidirectional(newItem.getModifierGroup().idProperty(), idConverter);
        tfWeight.textProperty().bindBidirectional(newItem.weightProperty(), numberConverter);
    }

    @Override
    void clearControls() {
        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        tfType.setText("");
        tfGroup.setText("");
        tfWeight.setText("");
    }

    @Override
    Modifier createNew() {
        Modifier modifier = new Modifier();
        modifier.setModifierGroup(new ModifierGroup());
        return modifier;
    }

    @Override
    void updateTableContent(TableView<Modifier> table) {
        App.api.listModifiers(new AlertCallback<List<Modifier>>() {
            @Override
            public void onSuccess(List<Modifier> modifiers, Response response) {
                table.setItems(FXCollections.observableList(modifiers));
            }
        });
    }

    @Override
    void submitItem(Modifier item) {
        App.api.updateModifier(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(Modifier item) {
        App.api.removeModifier(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }

    /******************************************************************
     *                                                                *
     * Converter
     *                                                                *
     ******************************************************************/

    private static final StringConverter<ModifierType> modifierTypeConverter = new StringConverter<ModifierType>() {
        @Override
        public String toString(ModifierType object) {
            if (object == null)
                return "";

            switch (object) {
                case ADDITION:
                    return "A";
                case SUBSTITUTION:
                    return "S";
                case EXCLUSION:
                    return "E";
                case INSTRUCTION:
                    return "I";
                case VARIATION:
                    return "V";
                default:
                    return "";
            }
        }

        @Override
        public ModifierType fromString(String string) {
            if (string == null || string.length() == 0)
                return null;
            else if (string.equalsIgnoreCase("A"))
                return ModifierType.ADDITION;
            else if (string.equalsIgnoreCase("S"))
                return ModifierType.SUBSTITUTION;
            else if (string.equalsIgnoreCase("E"))
                return ModifierType.EXCLUSION;
            else if (string.equalsIgnoreCase("I"))
                return ModifierType.INSTRUCTION;
            else if (string.equalsIgnoreCase("V"))
                return ModifierType.VARIATION;
            else
                return null;
        }
    };
}
