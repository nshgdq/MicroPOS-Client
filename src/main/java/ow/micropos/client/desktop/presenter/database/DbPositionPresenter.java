package ow.micropos.client.desktop.presenter.database;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.common.AlertCallback;
import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.enums.Permission;

import java.util.List;

public class DbPositionPresenter extends ItemPresenter<Position> {

    private TextField tfName;
    private ListView<Position> lvPositions;
    private ListView<Permission> lvPermissionsUsed;

    public DbPositionPresenter() {

        GridPane root = new GridPane();
        root.getStylesheets().add("/css/database.css");

        RowConstraints rc1 = new RowConstraints();
        rc1.setVgrow(Priority.NEVER);
        RowConstraints rc2 = new RowConstraints();
        rc2.setVgrow(Priority.ALWAYS);
        root.getRowConstraints().setAll(rc1, rc2);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(20);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(40);
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setPercentWidth(40);
        root.getColumnConstraints().setAll(cc1, cc2, cc3);

        root.add(new StackPane(new Label("Positions")), 0, 0);
        lvPositions = new ListView<>();
        lvPositions.getStyleClass().add("fontSizeHack");
        lvPositions.setCellFactory(param -> new ListCell<Position>() {
            @Override
            protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        lvPositions.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            Platform.runLater(() -> setItem(n));
        });
        root.add(lvPositions, 0, 1);

        root.add(new StackPane(new Label("Available")), 1, 0);
        ListView<Permission> lvPermissionsAvailable = new ListView<>();
        lvPermissionsAvailable.getStyleClass().add("fontSizeHack");
        lvPermissionsAvailable.setItems(FXCollections.observableList(Permission.asList));
        lvPermissionsAvailable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            Platform.runLater(() -> {
                if (n != null && getItem() != null && !getItem().hasPermission(n)) {
                    lvPermissionsAvailable.getSelectionModel().clearSelection();
                    getItem().getPermissions().add(n);
                }
            });
        });
        root.add(lvPermissionsAvailable, 1, 1);


        tfName = new TextField();
        tfName.setPromptText("Position");
        root.add(new StackPane(tfName), 2, 0);

        lvPermissionsUsed = new ListView<>();
        lvPermissionsUsed.getStyleClass().add("fontSizeHack");
        lvPermissionsUsed.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            Platform.runLater(() -> {
                if (n != null && getItem() != null && getItem().hasPermission(n)) {
                    lvPermissionsUsed.getSelectionModel().clearSelection();
                    getItem().getPermissions().remove(n);
                }
            });
        });
        root.add(lvPermissionsUsed, 2, 1);

        Presenter.injectView(this, root);
    }

    @Override
    public void clear() {
        lvPositions.setItems(FXCollections.emptyObservableList());
        setItem(new Position());
    }

    @Override
    public void refresh() {
        clear();
        App.api.listPositions((AlertCallback<List<Position>>) (positions, response) -> Platform.runLater(() -> {
            lvPositions.setItems(FXCollections.observableList(positions));
        }));
    }

    @Override
    protected void updateItem(Position currentItem, Position newItem) {
        if (newItem == null) {
            if (currentItem != null)
                tfName.textProperty().unbindBidirectional(currentItem.nameProperty());

            tfName.setText("");
            lvPermissionsUsed.setItems(FXCollections.emptyObservableList());
        } else {
            tfName.textProperty().bindBidirectional(newItem.nameProperty());
            lvPermissionsUsed.setItems(newItem.getPermissions());
        }
    }

    @Override
    public void dispose() {

    }

    /******************************************************************
     *                                                                *
     * Menu
     *                                                                *
     ******************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Done", ActionType.FINISH, event -> Platform.runLater(App.main::backRefresh)),
            new Action("New", ActionType.BUTTON, event -> Platform.runLater(() -> {
                lvPositions.getSelectionModel().clearSelection();
                setItem(new Position());
            })),
            new Action("Submit", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (getItem() == null)
                    return;

                if (App.apiIsBusy.compareAndSet(false, true)) {
                    App.api.updatePosition(getItem(), (AlertCallback<Long>) (aLong, response) -> {
                        refresh();
                        App.notify.showAndWait("Updated Position.");
                    });
                }
            })),
            new Action("Delete", ActionType.BUTTON, event -> {
                if (getItem() == null)
                    return;

                App.confirm.showAndWait("Are you sure you want to delete this item?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        App.api.removePosition(getItem().getId(), (AlertCallback<Boolean>) (aBool, response) -> {
                            refresh();
                            App.notify.showAndWait("Position " + getItem().getId() + " removed.");
                        });
                    }
                });
            })
    );
}
