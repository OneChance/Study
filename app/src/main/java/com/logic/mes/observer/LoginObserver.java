package com.logic.mes.observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.base.UserInfoResult;
import com.logic.mes.update.UpdateAppUtils;

import java.util.List;

import rx.Observer;


public class LoginObserver implements Observer<UserInfoResult> {

    public Context context;
    public IUpdate iUpdate;

    public LoginObserver(Context context,IUpdate iUpdate) {
        this.context = context;
        this.iUpdate = iUpdate;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        dbLogin();
        e.printStackTrace();
    }


    @Override
    public void onNext(UserInfoResult res) {
        if(res.getCode()==0){
            DBHelper.getInstance(context).deleteAll(UserInfo.class);
            UserInfo userInfo = res.getDatas();
            DBHelper.getInstance(context).save(userInfo);
            toMain(userInfo);
        }else{
            dbLogin();
        }
    }

    public void toMain(UserInfo userInfo){

        if(false){
            iUpdate.updateStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateAppUtils.downloadApk(context,"url");
                }
            }).start();
        }else{
            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userInfo", userInfo);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    public void dbLogin(){
        List<UserInfo> list = DBHelper.getInstance(context).query(UserInfo.class);
        if(list!=null&&list.size()>0){
            toMain(list.get(0));
        }else{
            iUpdate.loginButtonRecover();
            Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    public interface IUpdate{
        void updateStart();
        void loginButtonRecover();
    }
}
