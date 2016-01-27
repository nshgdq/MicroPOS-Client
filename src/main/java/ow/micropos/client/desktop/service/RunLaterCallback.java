package ow.micropos.client.desktop.service;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.error.ErrorInfo;

public abstract class RunLaterCallback<T> extends RestServiceCallback<T> {

    public abstract void laterSuccess(T t);

    // Ignore rejected requests by default.
    public void laterReject() {

    }

    // Notify failed requests by default.
    public void laterFailure(ErrorInfo error) {
        App.notify.showAndWait(error.getStatus(), error.getMessage());
    }

    @Override
    public final void reject() {
        Platform.runLater(this::laterReject);
    }

    @Override
    public final void success(T t) {
        Platform.runLater(() -> laterSuccess(t));
    }

    @Override
    public final void failure(ErrorInfo error) {
        Platform.runLater(() -> laterFailure(error));
    }

    /******************************************************************
     *                                                                *
     * Common Callbacks
     *                                                                *
     ******************************************************************/

    public static interface IdUpdater<T> {
        void updateId(Long aLong, T item);
    }

    public static <T> RunLaterCallback<Long> submitCallback(T item, TableView<T> table, IdUpdater<T> updater) {
        return new RunLaterCallback<Long>() {
            @Override
            public void laterSuccess(Long aLong) {
                if (App.properties.getBool("db-alert-on-submit")) {
                    App.notify.showAndWait("Item submitted.");
                }

                if (App.properties.getBool("db-refresh-on-submit")) {
                    App.main.refresh();

                } else {

                    if (!table.getItems().contains(item)) {
                        table.getItems().add(item);
                        table.scrollTo(item);
                    }

                    if (updater != null)
                        updater.updateId(aLong, item);

                }
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                super.laterFailure(error);
                App.main.refresh();
            }
        };
    }

    public static <T> RunLaterCallback<Boolean> deleteCallback(T item, TableView<T> table) {
        return new RunLaterCallback<Boolean>() {
            @Override
            public void laterSuccess(Boolean aBoolean) {
                if (App.properties.getBool("db-refresh-on-delete")) {
                    App.main.refresh();
                } else if (table.getItems().contains(item)) {
                    table.getItems().remove(item);
                }
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                super.laterFailure(error);
                App.main.refresh();
            }
        };
    }

}