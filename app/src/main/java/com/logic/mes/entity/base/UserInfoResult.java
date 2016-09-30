package com.logic.mes.entity.base;


public class UserInfoResult  extends JsonResult{
    UserInfo datas;

    public UserInfo getDatas() {
        return datas;
    }

    public void setDatas(UserInfo datas) {
        this.datas = datas;
    }
}
