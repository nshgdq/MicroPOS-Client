package ow.micropos.client.desktop.service;

import javafx.application.Platform;
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

    public static RunLaterCallback<Long> submitCallback() {
        return new RunLaterCallback<Long>() {
            @Override
            public void laterSuccess(Long aLong) {
                App.notify.showAndWait("Submit Successful.");
                App.main.refresh();
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                super.laterFailure(error);
                App.main.refresh();
            }
        };
    }

    public static RunLaterCallback<Boolean> deleteCallback() {
        return new RunLaterCallback<Boolean>() {
            @Override
            public void laterSuccess(Boolean aBoolean) {
                App.notify.showAndWait("Delete Successful.");
                App.main.refresh();
            }

            @Override
            public void laterFailure(ErrorInfo error) {
                super.laterFailure(error);
                App.main.refresh();
            }
        };
    }

}
