package com.logic.mes.net;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class NetUtil {
    private static IServices services;
    private static OkHttpClient client;
    private static Retrofit restAdapter;

    public static IServices getServices(boolean rebuild) {

        if (services == null || rebuild) {

            if (services == null) {
                client = new OkHttpClient();
                client.setConnectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS);
                client.interceptors().add(new Interceptor() {

                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request.Builder builder = chain.request()
                                .newBuilder();

                        if (Session.SessionId != null && !Session.SessionId.equals("")) {
                            builder = builder.addHeader("Cookie", Session.SessionId);
                        }

                        Request request = builder.build();

                        Log.d("------------", request.urlString());

                        Response response = chain.proceed(request);

                        if (Session.SessionId != null && Session.SessionId.equals("")) {
                            Session.SessionId = response.header("Set-Cookie");
                        }

                        return response;
                    }
                });
            }

            if (rebuild) {

                if (NetConfig.IP.equals("")) {
                    NetConfig.IP = "127.0.0.1";
                }

                if (NetConfig.PORT.equals("")) {
                    NetConfig.PORT = "8080";
                }

                restAdapter = new Retrofit.Builder()
                        .baseUrl("http://" + NetConfig.IP + ":" + NetConfig.PORT + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
            }

            services = restAdapter.create(IServices.class);
        }

        return services;
    }

    public static <T> Observable<T> SetObserverCommonAction(Observable<T> observable) {

        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
