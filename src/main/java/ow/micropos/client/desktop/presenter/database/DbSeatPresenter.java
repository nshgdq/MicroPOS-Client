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
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

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
    Label getTitleLabel() {
        return new Label("Seat Information");
    }

    @Override
    Label[] getEditLabels() {
        return new Label[]{new Label("Section"), new Label("Tag"), new Label("Row"), new Label("Col")};
    }

    @Override
    Node[] getEditControls() {
        tfSection = createTextField("Section");
        tfTag = createTextField("Tag");
        tfRow = createTextField("Row");
        tfCol = createTextField("Col");

        return new Node[]{tfTag, tfSection, tfRow, tfCol};
    }

    @Override
    TableColumn<Seat, String>[] getTableColumns() {
        section = createTableColumn("Section", param -> param.getValue().sectionSummaryProperty());
        tag = createTableColumn("Tag", param -> param.getValue().tagProperty(), ComparatorUtils.tagComparator);
        row = createTableColumn("Row", param -> param.getValue().rowProperty().asString());
        col = createTableColumn("Col", param -> param.getValue().colProperty().asString());

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
    void toggleControls(boolean visible) {
        tfSection.setVisible(visible);
        tfTag.setVisible(visible);
        tfRow.setVisible(visible);
        tfCol.setVisible(visible);

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
        App.apiProxy.listSeats(new RunLaterCallback<List<Seat>>() {
            @Override
            public void laterSuccess(List<Seat> seats) {
                table.setItems(FXCollections.observableList(seats));
            }
        });
    }

    @Override
    void submitItem(Seat item) {
        App.apiProxy.updateSeat(item, RunLaterCallback.submitCallback(item, table, (id, o) -> o.setId(id)));
    }

    @Override
    void deleteItem(Seat item) {
        App.apiProxy.removeSeat(item.getId(), RunLaterCallback.deleteCallback(item, table));
    }
}
