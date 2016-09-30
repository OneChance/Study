package com.logic.mes.net;


import com.logic.mes.entity.base.UserInfoResult;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface IServices {
    @FormUrlEncoded
    @POST("/mes/intface/userlogon/")
    public Observable<UserInfoResult> Login(@Field("code") String userCode);

    @GET("/nba/game/myteam/?device=app&ask=players")
    public Observable<Object> getTeamPlayers();

    @GET("/nba/game/myteam/?device=app&ask=teamInfo")
    public Observable<Object> getTeamInfo();
}
