package com.logic.mes.net;


import com.logic.mes.entity.base.UserData;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface IServices {
    @FormUrlEncoded
    @POST("nba/account/login/?device=app")
    public Observable<UserData> Login(@Field("userCode") String userCode);

    @GET("/nba/game/myteam/?device=app&ask=players")
    public Observable<Object> getTeamPlayers();

    @GET("/nba/game/myteam/?device=app&ask=teamInfo")
    public Observable<Object> getTeamInfo();
}
