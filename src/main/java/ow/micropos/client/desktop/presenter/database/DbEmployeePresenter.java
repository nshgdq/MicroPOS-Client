package ow.micropos.client.desktop.presenter.database;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.employee.Employee;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DbEmployeePresenter extends DbEntityPresenter<Employee> {

    TextField tfFirst;
    TextField tfLast;
    TextField tfPin;
    TextField tfPositions;

    TableColumn<Employee, String> first;
    TableColumn<Employee, String> last;
    TableColumn<Employee, String> positions;

    @Override
    Node[] getEditControls() {
        tfFirst = createTextField("First Name");
        tfLast = createTextField("Last Name");
        tfPin = createTextField("PIN");
        tfPositions = createTextField("Positions");

        return new Node[]{new Label("Employee Information"), tfFirst, tfLast, tfPin, tfPositions};
    }

    @Override
    TableColumn<Employee, String>[] getTableColumns() {
        first = createTableColumn("First Name", param -> param.getValue().firstNameProperty());
        last = createTableColumn("Last Name", param -> param.getValue().lastNameProperty());
        positions = createTableColumn("Positions", param -> param.getValue().positionSummaryProperty());

        return new TableColumn[]{first, last, positions};
    }

    @Override
    void unbindItem(Employee currentItem) {
        tfFirst.textProperty().unbindBidirectional(currentItem.firstNameProperty());
        tfLast.textProperty().unbindBidirectional(currentItem.lastNameProperty());
        tfPin.textProperty().unbindBidirectional(currentItem.pinProperty());
        tfPositions.textProperty().unbindBidirectional(currentItem.positionsProperty());
    }

    @Override
    void bindItem(Employee newItem) {
        tfFirst.textProperty().bindBidirectional(newItem.firstNameProperty());
        tfLast.textProperty().bindBidirectional(newItem.lastNameProperty());
        tfPin.textProperty().bindBidirectional(newItem.pinProperty());
        tfPositions.textProperty().bindBidirectional(newItem.positionsProperty(), positionsConverter);
    }

    @Override
    void clearControls() {
        tfFirst.setText("");
        tfLast.setText("");
        tfPin.setText("");
        tfPositions.setText("");
    }

    @Override
    Employee createNew() {
        return new Employee();
    }

    @Override
    void updateTableContent(TableView<Employee> table) {
        App.api.listEmployees(new AlertCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> categories, Response response) {
                table.setItems(FXCollections.observableList(categories));
            }
        });
    }

    @Override
    void submitItem(Employee item) {
        App.api.updateEmployee(item, new Callback<Long>() {
            @Override
            public void success(Long aLong, Response response) {
                Platform.runLater(() -> {
                    refresh();
                    App.notify.showAndWait("Updated Employee.");
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Platform.runLater(() -> {
                    refresh();
                    if (error != null)
                        App.notify.showAndWait("ERROR", error.getMessage());
                    else
                        App.notify.showAndWait("ERROR", "Unexpected Error.");
                });
            }
        });
    }

    @Override
    void deleteItem(Employee item) {
        if (Objects.equals(item.getId(), App.employee.getId())) {
            refresh();
            App.notify.showAndWait("Can't remove yourself.");
        } else {
            App.api.removeEmployee(item.getId(), new Callback<Boolean>() {
                @Override
                public void success(Boolean aBoolean, Response response) {
                    Platform.runLater(() -> {
                        refresh();
                        App.notify.showAndWait("Removed Employee.");
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Platform.runLater(() -> {
                        refresh();
                        if (error != null)
                            App.notify.showAndWait("ERROR", error.getMessage());
                        else
                            App.notify.showAndWait("ERROR", "Unexpected Error.");
                    });
                }
            });
        }
    }

    /******************************************************************
     *                                                                *
     * Converters
     *                                                                *
     ******************************************************************/

    private static final StringConverter<ObservableList<Position>>
            positionsConverter = new StringConverter<ObservableList<Position>>() {
        @Override
        public String toString(ObservableList<Position> object) {
            return StringUtils.join(
                    object.stream().map(Position::getId).collect(Collectors.toList()),
                    ","
            );
        }

        @Override
        public ObservableList<Position> fromString(String string) {
            ObservableList<Position> positions = FXCollections.observableArrayList();
            for (String posId : StringUtils.split(string, ",")) {
                try {
                    Position position = new Position();
                    position.setId(Long.parseLong(posId));
                    positions.add(position);
                } catch (Exception e) {
                    // skip
                }
            }
            return positions;
        }
    };

}
