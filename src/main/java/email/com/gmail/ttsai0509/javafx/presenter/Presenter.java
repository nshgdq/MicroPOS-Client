package email.com.gmail.ttsai0509.javafx.presenter;

import email.com.gmail.ttsai0509.javafx.control.TiledListView;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import ow.micropos.client.desktop.utils.Action;

import java.io.IOException;


/******************************************************************
 *                                                                *
 * Combines the View/Controller into a single class, Presenter.   *
 * Allows us to use the factory method load and gives us access   *
 * to both View and Controller (instead of passing around both    *
 * instances). This works because JavaFX Controllers and FXML are *
 * tightly coupled when using fx:controller property.             *
 *                                                                *
 * Root view injection is free when using the load factory.       *
 * Alternatively, we can injectView for presenters created with   *
 * a constructor.                                                 *
 *                                                                *
 ******************************************************************/
public abstract class Presenter {

    public static <P extends Presenter> P load(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Presenter.class.getResource(fxml));
            loader.load();

            P presenter = loader.getController();
            presenter.setView(loader.getRoot());
            presenter.ready();
            return presenter;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Unable to load resource : " + fxml);
        }
    }

    // Use this in code-based, non-fxml presenters (in constructor)
    public static <P extends Presenter> P injectView(P presenter, Parent view) {
        presenter.setView(view);
        return presenter;
    }

    private Parent view;

    void setView(Parent view) {
        this.view = view;
    }

    /**************************************************************************
     *                                                                        *
     * Methods to implement                                                   *
     *                                                                        *
     **************************************************************************/

    @FXML
    public void initialize() {}

    public void clear() {}

    public void refresh() {}

    public void ready() {}

    public ObservableList<Action> menu() {
        return FXCollections.emptyObservableList();
    }

    public abstract void dispose();

    public final Parent getView() {
        return view;
    }

    public final boolean isVisible() {
        return view.visibleProperty().get();
    }

    public final void onClick(EventHandler<? super MouseEvent> handler) {
        view.setOnMouseClicked(handler);
    }

    /**************************************************************************
     *                                                                        *
     * Quick binding and unbinding methods.                                   *
     *                                                                        *
     **************************************************************************/

    protected final void setTextProperty(StringProperty textProperty, ObservableValue<? extends String> property) {
        if (textProperty != null & property != null)
            textProperty.bind(property);
    }

    protected final void unsetTextProperty(StringProperty textProperty) {
        unsetTextProperty(textProperty, "");
    }

    protected final void unsetTextProperty(StringProperty textProperty, String emptyValue) {
        if (textProperty != null) {
            textProperty.unbind();
            textProperty.set(emptyValue);
        }
    }

    protected final void setLabel(Label label, ObservableValue<? extends String> property) {
        if (label != null && property != null) {
            label.textProperty().bind(property);
        }
    }

    protected final void unsetLabel(Label label) {
        if (label != null) {
            label.textProperty().unbind();
            label.setText("");
        }
    }

    protected final <T> void setListView(ListView<T> view, ObservableList<T> list) {
        if (view != null && list != null) {
            view.getSelectionModel().clearSelection();
            view.setItems(list);
        }
    }

    protected final <T> void unsetListView(ListView<T> view) {
        if (view != null) {
            view.getSelectionModel().clearSelection();
            view.setItems(FXCollections.emptyObservableList());
        }
    }

    protected final <T> void setTiledListView(TiledListView<T> view, ObservableList<T> list) {
        if (view != null && list != null) {
            view.setItems(list);
        }
    }

    protected final <T> void unsetTiledListView(TiledListView<T> view) {
        if (view != null) {
            view.setItems(FXCollections.emptyObservableList());
        }
    }

}
