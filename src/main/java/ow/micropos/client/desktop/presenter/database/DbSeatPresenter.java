package ow.micropos.client.desktop.presenter.database;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.model.target.Section;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.util.List;

public class DbSeatPresenter extends DbEntityPresenter<Seat> {

    TextField tfTag;
    TextField tfRow;
    TextField tfCol;
    TextField tfSection;
    TableColumn<Seat, String> tag;
    TableColumn<Seat, String> section;
    TableColumn<Seat, String> row;
    TableColumn<Seat, String> col;

    @Override
    Node[] getTextFields() {
        tfSection = new TextField();
        tfTag = new TextField();
        tfRow = new TextField();
        tfCol = new TextField();

        tfSection.setPromptText("Section");
        tfTag.setPromptText("Tag");
        tfRow.setPromptText("Row");
        tfCol.setPromptText("Col");

        return new Node[]{new Label("Seat Information"), tfTag, tfSection, tfRow, tfCol};
    }

    @Override
    TableColumn<Seat, String>[] getTableColumns() {
        section = new TableColumn<>("Section");
        tag = new TableColumn<>("Tag");
        row = new TableColumn<>("Row");
        col = new TableColumn<>("Col");

        section.setCellValueFactory(param -> param.getValue().getSection().nameProperty());
        tag.setCellValueFactory(param -> param.getValue().tagProperty());
        row.setCellValueFactory(param -> param.getValue().rowProperty().asString());
        col.setCellValueFactory(param -> param.getValue().colProperty().asString());

        return new TableColumn[]{tag, section, row, col};
    }

    @Override
    void unbindItem(Seat currentItem) {
        tfSection.textProperty().unbindBidirectional(currentItem.getSection().nameProperty());
        tfTag.textProperty().unbindBidirectional(currentItem.tagProperty());
        tfRow.textProperty().unbindBidirectional(currentItem.rowProperty());
        tfCol.textProperty().unbindBidirectional(currentItem.colProperty());
    }

    @Override
    void bindItem(Seat newItem) {
        tfSection.textProperty().bindBidirectional(newItem.getSection().idProperty(), new StringConverter<Long>() {
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
        tfTag.textProperty().bindBidirectional(newItem.tagProperty());
        tfRow.textProperty().bindBidirectional(newItem.rowProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object == null)
                    return "";
                else
                    return Integer.toString(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
        tfCol.textProperty().bindBidirectional(newItem.colProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object == null)
                    return "";
                else
                    return Integer.toString(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    @Override
    void clearFields() {
        tfSection.setText("");
        tfTag.setText("");
        tfRow.setText("");
        tfCol.setText("");
    }

    @Override
    Seat createNew() {
        Seat seat = new Seat();
        seat.setSection(new Section());
        return seat;
    }

    @Override
    void updateTableContent(TableView<Seat> table) {
        App.api.listSeats(new AlertCallback<List<Seat>>() {
            @Override
            public void onSuccess(List<Seat> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Seat item) {
        App.api.updateSeat(
                item,
                (AlertCallback<Long>) (aLong, response) -> {
                    refresh();
                    App.notify.showAndWait("Updated Menu Item.");
                }
        );
    }

    @Override
    void deleteItem(Seat item) {
        App.api.removeSeat(item.getId(), (AlertCallback<Boolean>) (aBool, response) -> {
            refresh();
            App.notify.showAndWait("Removed Menu Item.");
        });
    }
}
