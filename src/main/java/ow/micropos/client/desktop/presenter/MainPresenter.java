package ow.micropos.client.desktop.presenter;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionLabel;

import java.util.Stack;

public class MainPresenter extends Presenter {

    private final Stack<Presenter> backStack = new Stack<>();

    @FXML GridPane topBar;

    @FXML StackPane fore;
    @FXML StackPane back;

    @FXML GridView<Action> gvVisibleMenu;

    @FXML VBox vbDateTime;
    @FXML Label lblDate;
    @FXML Label lblTime;

    @FXML StackPane spContent;

    @Override
    public void initialize() {

        vbDateTime.setAlignment(Pos.CENTER);
        fore.setAlignment(Pos.CENTER);
        back.setAlignment(Pos.CENTER);

        setLabel(lblTime, App.clock.timeProperty());
        setLabel(lblDate, App.clock.dateProperty());

        vbDateTime.setOnMouseClicked(event -> Platform.runLater(() -> {
            App.employee = null;
            App.main.clearToRefresh(App.loginPresenter);
        }));

        back.setOnMouseClicked(event -> Platform.runLater(gvVisibleMenu::prevPage));
        fore.setOnMouseClicked(event -> Platform.runLater(gvVisibleMenu::nextPage));

        gvVisibleMenu.setPage(0);
        gvVisibleMenu.setRows(1);
        gvVisibleMenu.setCols(App.properties.getInt("options-per-page"));
        gvVisibleMenu.setCellFactory(param -> new ActionLabel());
        gvVisibleMenu.setItems(FXCollections.emptyObservableList());

    }

    @Override
    public void dispose() {
        unsetLabel(lblTime);
        unsetLabel(lblDate);
        spContent.getChildren().clear();
        gvVisibleMenu.getChildren().clear();
        backStack.clear();
    }

    @Override
    public void refresh() {
        backStack.peek().refresh();
    }

    public void menuRefresh() {
        Presenter presenter = backStack.peek();
        gvVisibleMenu.setItems(FXCollections.emptyObservableList());
        gvVisibleMenu.setItems(presenter.menu());
        gvVisibleMenu.setPage(0);
    }

    public void clearTo(Presenter presenter) {
        backStack.clear();
        next(presenter);
    }

    public void clearToRefresh(Presenter presenter) {
        presenter.refresh();
        clearTo(presenter);
    }

    public void clearToClearAndRefresh(Presenter presenter) {
        presenter.clear();
        presenter.refresh();
        clearTo(presenter);
    }

    public void next(Presenter presenter) {
        backStack.add(presenter);
        spContent.getChildren().setAll(presenter.getView());
        gvVisibleMenu.setItems(presenter.menu());
        gvVisibleMenu.setPage(0);
    }

    public void nextRefresh(Presenter presenter) {
        presenter.refresh();
        next(presenter);
    }

    public void nextClearAndRefresh(Presenter presenter) {
        presenter.clear();
        presenter.refresh();
        next(presenter);
    }

    public void swap(Presenter presenter) {
        backStack.pop();
        next(presenter);
    }

    public void swapRefresh(Presenter presenter) {
        presenter.refresh();
        swap(presenter);
    }

    public void swapClearAndRefresh(Presenter presenter) {
        presenter.clear();
        presenter.refresh();
        swap(presenter);
    }

    public void back() {
        backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public void backRefresh() {
        backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            presenter.refresh();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public void backClearAndRefresh() {
        backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            presenter.clear();
            presenter.refresh();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public void backTo(int size) {
        while (backStack.size() > size)
            backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public void backToRefresh(int size) {
        while (backStack.size() > size)
            backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            presenter.refresh();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public void backToClearAndRefresh(int size) {
        while (backStack.size() > size)
            backStack.pop();

        if (!backStack.isEmpty()) {
            Presenter presenter = backStack.peek();
            presenter.clear();
            presenter.refresh();
            spContent.getChildren().setAll(presenter.getView());
            gvVisibleMenu.setItems(presenter.menu());
            gvVisibleMenu.setPage(0);
        }
    }

    public <T> void setNextRefresh(ItemPresenter<T> presenter, T item) {
        presenter.setItem(item);
        nextRefresh(presenter);
    }

    public <T> void setSwapRefresh(ItemPresenter<T> presenter, T item) {
        presenter.setItem(item);
        swapRefresh(presenter);
    }

}