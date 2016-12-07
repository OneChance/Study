package com.logic.mes.net;


import com.logic.mes.entity.base.UserInfoResult;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.entity.server.ProcessSubmit;
import com.logic.mes.entity.server.ServerResult;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface IServices {
    @FormUrlEncoded
    @POST("intface/userlogon/")
    public Observable<UserInfoResult> Login(@Field("code") String userCode);

    @FormUrlEncoded
    @POST("intface/getbrick/")
    public Observable<ServerResult> getBrickInfo(@Field("brickId") String brickId);

    @POST("intface/bricksubmit/")
    public Observable<ServerResult> brickSubmit(@Body ProcessSubmit data);

    @FormUrlEncoded
    @POST("intface/getbagdata/")
    public Observable<ServerResult> getBagData(@Field("objCode") String objCode);

    @POST("intface/datacheck/")
    public Observable<ServerResult> checkData(@Body ProcessItem data);
}
