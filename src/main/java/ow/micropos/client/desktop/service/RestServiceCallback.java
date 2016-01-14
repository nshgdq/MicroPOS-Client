package ow.micropos.client.desktop.service;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public interface RestServiceCallback<T> extends Callback<T> {

    void reject(RestServiceProxy.OnCondition condition);

    void success(T t, Response response);

    void failure(RetrofitError error);
}
