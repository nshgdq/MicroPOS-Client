package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.List;

public class DbModifierPresenter extends DbEntityPresenter<Modifier> {

    TextField tfName;
    TextField tfTag;
    TextField tfPrice;
    TextField tfType;
    TextField tfGroup;
    TableColumn<Modifier, String> name;
    TableColumn<Modifier, String> tag;
    TableColumn<Modifier, String> price;
    TableColumn<Modifier, String> type;
    TableColumn<Modifier, String> group;

    @Override
    Node[] getTextFields() {
        tfName = new TextField();
        tfTag = new TextField();
        tfPrice = new TextField();
        tfType = new TextField();
        tfGroup = new TextField();

        tfName.setPromptText("Name");
        tfTag.setPromptText("Tag");
        tfPrice.setPromptText("Price");
        tfType.setPromptText("Type");
        tfGroup.setPromptText("Group");

        return new Node[]{new Label("Modifier Information"), tfName, tfTag, tfPrice, tfType, tfGroup};
    }

    @Override
    TableColumn<Modifier, String>[] getTableColumns() {
        name = new TableColumn<>("Name");
        tag = new TableColumn<>("Tag");
        price = new TableColumn<>("Price");
        type = new TableColumn<>("Type");
        group = new TableColumn<>("Group");

        name.setCellValueFactory(param -> param.getValue().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        price.setCellValueFactory(param -> param.getValue().priceProperty().asString());
        type.setCellValueFactory(param -> param.getValue().typeProperty().asString());
        group.setCellValueFactory(param -> param.getValue().getModifierGroup().nameProperty());

        return new TableColumn[]{name, tag, price, type, group};
    }

    @Override
    void unbindItem(Modifier currentItem) {
        tfName.textProperty().unbindBidirectional(currentItem.nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfPrice.textProperty().unbindBidirectional(currentItem.priceProperty());
        tfType.textProperty().unbindBidirectional(currentItem.typeProperty());
        tfGroup.textProperty().unbindBidirectional(currentItem.getModifierGroup().nameProperty());
    }

    @Override
    void bindItem(Modifier newItem) {
        tfName.textProperty().bindBidirectional(newItem.nameProperty());
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfPrice.textProperty().bindBidirectional(newItem.priceProperty(), new StringConverter<BigDecimal>() {
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
        tfType.textProperty().bindBidirectional(newItem.typeProperty(), new StringConverter<ModifierType>() {
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
        });
        tfGroup.textProperty().bindBidirectional(newItem.getModifierGroup().idProperty(), new StringConverter<Long>() {
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
        });
    }

    @Override
    void clearFields() {
        tfName.setText("");
        tfTag.setText("");
        tfPrice.setText("");
        tfType.setText("");
        tfGroup.setText("");
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
}
