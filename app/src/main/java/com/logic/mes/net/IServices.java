package com.logic.mes.net;


import com.logic.mes.entity.base.UserInfoResult;
import com.logic.mes.entity.process.By;
import com.logic.mes.entity.process.Wx;
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
    public Observable<UserInfoResult> Login(@Field("code") String userCode,@Field("clientType") String clientType,@Field("version") Integer version);

    @FormUrlEncoded
    @POST("intface/getbrick/")
    public Observable<ServerResult> getBrickInfo(@Field("brickId") String brickId, @Field("produceCode") String produceCode);

    @FormUrlEncoded
    @POST("intface/getbrickgroup/")
    public Observable<ServerResult> getBrickGroup(@Field("brickId") String brickId, @Field("produceCode") String produceCode);

    @POST("intface/cancelbrickgroup/")
    public Observable<ServerResult> cancelBrickGroup(@Body ProcessSubmit data);

    @POST("intface/bricksubmit/")
    public Observable<ServerResult> brickSubmit(@Body ProcessSubmit data);

    @POST("intface/submitcatch/")
    public Observable<ServerResult> submitCatch(@Body ProcessSubmit data);

    @FormUrlEncoded
    @POST("intface/getbagdata/")
    public Observable<ServerResult> getBagData(@Field("objCode") String objCode);

    @POST("intface/datacheck/")
    public Observable<ServerResult> checkData(@Body ProcessItem data);

    @FormUrlEncoded
    @POST("intface/getuser/")
    public Observable<ServerResult> getUser(@Field("userCode") String userCode);

    @POST("intface/baoyang/")
    public Observable<ServerResult> baoYangStart(@Body By data);

    @POST("intface/weixiu/")
    public Observable<ServerResult> weiXiuStart(@Body Wx data);
}
