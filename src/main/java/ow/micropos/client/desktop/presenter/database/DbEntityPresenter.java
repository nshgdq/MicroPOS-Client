package ow.micropos.client.desktop.presenter.database;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;

public abstract class DbEntityPresenter<T> extends ItemPresenter<T> {

    TableView<T> table;

    protected DbEntityPresenter() {
        GridPane root = new GridPane();
        root.getStylesheets().add("/css/database.css");

        RowConstraints rc1 = new RowConstraints();
        rc1.setVgrow(Priority.ALWAYS);
        root.getRowConstraints().setAll(rc1);

        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc1.setPercentWidth(80);
        cc2.setPercentWidth(20);
        root.getColumnConstraints().setAll(cc1, cc2);

        table = new TableView<>(FXCollections.emptyObservableList());
        table.setId("content");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {if (n != null) setItem(n);});
        table.getColumns().addAll(getTableColumns());
        GridPane.setConstraints(table, 0, 0);

        VBox side = new VBox(10);
        side.setId("side");
        side.getChildren().addAll(getTextFields());
        GridPane.setConstraints(side, 1, 0);

        root.getChildren().setAll(table, side);

        Presenter.injectView(this, root);

    }

    abstract Node[] getTextFields();

    abstract TableColumn<T, String>[] getTableColumns();

    abstract void updateTableContent(TableView<T> table);

    abstract void unbindItem(T currentItem);

    abstract void bindItem(T newItem);

    abstract void clearFields();

    abstract T createNew();

    abstract void submitItem(T item);

    abstract void deleteItem(T item);

    @Override
    protected void updateItem(T currentItem, T newItem) {
        if (currentItem != null)
            unbindItem(currentItem);

        if (newItem == null) {
            clearFields();
        } else {
            bindItem(newItem);
        }
    }

    @Override
    public void refresh() {
        setItem(createNew());
        updateTableContent(table);
    }

    @Override
    public void dispose() {
        setItem(null);
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
            new Action("New", ActionType.BUTTON, event -> Platform.runLater(() -> setItem(createNew()))),
            new Action("Submit", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (getItem() == null)
                    return;

                if (App.apiIsBusy.compareAndSet(false, true)) {
                    submitItem(getItem());
                }
            })),
            new Action("Delete", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (getItem() == null)
                    return;

                if (App.apiIsBusy.compareAndSet(false, true)) {
                    deleteItem(getItem());
                }
            }))
    );

}
