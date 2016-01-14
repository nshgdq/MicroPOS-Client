package ow.micropos.client.desktop.service;

import javafx.application.Platform;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestAlertCallback<T> implements Callback<T> {


    @Override
    public void success(Object o, Response response) {
        Platform.runLater(() -> {
            onSuccess(t, response);
            App.apiIsBusy.set(false);
        });
    }

    @Override
    public void failure(RetrofitError error) {
        try {
            ErrorInfo info = (ErrorInfo) error.getBodyAs(ErrorInfo.class);
            final String status = info.getStatus();
            final String msg = info.getMessage();

            Platform.runLater(() -> {
                App.notify.showAndWait(status, msg);

                onFailure(error);
                App.apiIsBusy.set(false);
            });

        } catch (Exception e) {
            final String msg = e.getMessage();

            Platform.runLater(() -> {
                e.printStackTrace();
                if (msg == null || msg.isEmpty())
                    App.notify.showAndWait("Please check that the server is running.");
                else
                    App.notify.showAndWait(msg);

                onFailure(error);
                App.apiIsBusy.set(false);
            });
        }
    }
}
