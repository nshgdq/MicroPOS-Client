package ow.micropos.client.desktop.presenter.database;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;

import java.math.BigDecimal;
import java.util.Iterator;

public abstract class DbEntityPresenter<T> extends ItemPresenter<T> {

    TableView<T> table;

    protected DbEntityPresenter() {
        GridPane root = new GridPane();
        root.getStylesheets().add("/css/database.css");
        root.setHgap(10);
        root.setVgap(10);

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
        side.getChildren().addAll(getEditControls());
        GridPane.setConstraints(side, 1, 0);

        root.getChildren().setAll(table, side);

        Presenter.injectView(this, root);

    }

    abstract Node[] getEditControls();

    abstract TableColumn<T, String>[] getTableColumns();

    abstract void updateTableContent(TableView<T> table);

    abstract void unbindItem(T currentItem);

    abstract void bindItem(T newItem);

    abstract void clearControls();

    abstract T createNew();

    abstract void submitItem(T item);

    abstract void deleteItem(T item);

    protected void onDone() {
        App.main.backRefresh();
    }

    @Override
    protected void updateItem(T currentItem, T newItem) {
        if (currentItem != null)
            unbindItem(currentItem);

        if (newItem == null) {
            clearControls();
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
     * Node Factory Methods
     *                                                                *
     ******************************************************************/

    protected final <S> ComboBox<S> createComboBox(String prompt, S... choices) {
        ComboBox<S> cb = new ComboBox<>(FXCollections.observableArrayList(choices));
        cb.setPromptText(prompt);
        return cb;
    }

    protected final TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        return tf;
    }

    protected final TableColumn<T, String> createTableColumn(
            String name,
            Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> callback
    ) {
        TableColumn<T, String> tc = new TableColumn<>(name);
        tc.setCellValueFactory(callback);
        return tc;
    }

    /******************************************************************
     *                                                                *
     * Common Converters
     *                                                                *
     ******************************************************************/

    // TODO : Import Apache Commons Validator instead of relying on Exceptions
    protected static final StringConverter<Long> idConverter = new StringConverter<Long>() {
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
    };

    protected static final StringConverter<BigDecimal> priceConverter = new StringConverter<BigDecimal>() {
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
    };

    protected static final StringConverter<ObservableList<String>> listConverter = new StringConverter<ObservableList<String>>() {
        @Override
        public String toString(ObservableList<String> object) {
            StringBuilder sb = new StringBuilder("");

            Iterator<String> itr = object.iterator();
            if (itr.hasNext()) {
                sb.append(itr.next());
                while (itr.hasNext())
                    sb.append(",").append(itr.next());
            }

            return sb.toString();
        }

        @Override
        public ObservableList<String> fromString(String string) {
            return FXCollections.observableArrayList(string.split(","));
        }
    };

    protected static final StringConverter<Number> numberConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object) {
            return (object == null) ? "" : Integer.toString(object.intValue());
        }

        @Override
        public Number fromString(String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    };

    protected static final StringConverter<Color> colorConverter = new StringConverter<Color>() {
        @Override
        public String toString(Color object) {
            return String.format("#%02X%02X%02X",
                    (int) (object.getRed() * 255),
                    (int) (object.getGreen() * 255),
                    (int) (object.getBlue() * 255));
        }

        @Override
        public Color fromString(String string) {
            return Color.web(string);
        }
    };

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
            new Action("Done", ActionType.FINISH, event -> Platform.runLater(this::onDone)),
            new Action("New", ActionType.BUTTON, event -> Platform.runLater(() -> {
                table.getSelectionModel().clearSelection();
                setItem(createNew());
            })),
            new Action("Submit", ActionType.BUTTON, event -> Platform.runLater(() -> {
                if (getItem() == null)
                    return;

                if (App.apiIsBusy.compareAndSet(false, true)) {
                    submitItem(getItem());
                }
            })),
            new Action("Delete", ActionType.BUTTON, event -> {
                if (getItem() == null)
                    return;

                App.confirm.showAndWait("Are you sure you want to delete this item?", () -> {
                    if (App.apiIsBusy.compareAndSet(false, true)) {
                        deleteItem(getItem());
                    }
                });
            })
    );

}
