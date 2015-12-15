package ow.micropos.client.desktop.utils;

import javafx.application.Platform;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public interface AlertCallback<T> extends Callback<T> {

    void onSuccess(T t, Response response);

    default void onFailure(RetrofitError error) {}
    
    /*=======================================================================*
     =                                                                       =
     = Retrofit Callback method to be decorated
     =                                                                       =
     *=======================================================================*/

    @Override
    public default void success(T t, Response response) {
        Platform.runLater(() -> {
            onSuccess(t, response);
            App.apiIsBusy.set(false);
        });
    }

    @Override
    public default void failure(RetrofitError error) {
        try {
            ErrorInfo info = (ErrorInfo) error.getBodyAs(ErrorInfo.class);
            final String status = info.getStatus();
            final String msg = info.getMessage();

            Platform.runLater(() -> {
                App.error.showAndWait(status, msg);

                onFailure(error);
                App.apiIsBusy.set(false);
            });

        } catch (Exception e) {
            final String msg = e.getMessage();

            Platform.runLater(() -> {
                e.printStackTrace();
                if (msg == null || msg.isEmpty())
                    App.error.showAndWait("Please check that the server is running.");
                else
                    App.error.showAndWait(msg);

                onFailure(error);
                App.apiIsBusy.set(false);
            });
        }
    }
}
