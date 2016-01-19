package ow.micropos.client.desktop.service;

import ow.micropos.client.desktop.model.error.ErrorInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RestServiceCallback<T> implements Callback<T> {

    /******************************************************************
     *                                                                *
     * Callback gets bound to method, so it's safest to just inline   *
     * the callback definition.                                       *
     *                                                                *
     ******************************************************************/

    public abstract void reject();

    public abstract void success(T t);

    public abstract void failure(ErrorInfo error);

    @Override
    public final void success(T t, Response response) {
        success(t);

        if (method >= 0 && methodInUse != null)
            methodInUse[method].set(false);
    }

    @Override
    public final void failure(RetrofitError error) {

        ErrorInfo info;
        try {
            info = (ErrorInfo) error.getBodyAs(ErrorInfo.class);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty())
                msg = "Please contact server administrator.";
            info = new ErrorInfo("Unexpected Error", msg);
        }

        failure(info);

        if (method >= 0 && methodInUse != null)
            methodInUse[method].set(false);
    }

    /******************************************************************
     *                                                                *
     * Self-releasing callback
     *                                                                *
     ******************************************************************/

    private int method = -1;

    private AtomicBoolean[] methodInUse = null;

    public void injectMethodInfo(int method, AtomicBoolean[] methodInUse) {
        if (this.method == -1)
            this.method = method;
        if (this.methodInUse == null)
            this.methodInUse = methodInUse;
    }

}
