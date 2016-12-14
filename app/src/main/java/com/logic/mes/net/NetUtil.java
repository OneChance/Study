package com.logic.mes.net;

import com.squareup.okhttp.OkHttpClient;

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
                /*client.interceptors().add(new Interceptor() {

                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request request = chain.request();
                        request = request.newBuilder()
                                .cacheControl(com.squareup.okhttp.CacheControl.FORCE_NETWORK)
                                .build();

                        return chain.proceed(request);
                    }
                });*/
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
