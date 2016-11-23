package com.logic.mes.observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.TimeUtil;
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
    public static String currentInputCode = "";

    public LoginObserver(Context context, IUpdate iUpdate) {
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
        if (res.getCode() == 0) {
            UserInfo userInfo = res.getDatas();
            toMain(userInfo);
        } else {
            Log.w("mes", "login warn:" + res.getInfo());
            dbLogin();
        }
    }

    public void toMain(final UserInfo userInfo) {

        if (userInfo.getAppInfo().getAndroidVersion() > MyApplication.VERSION) {
            iUpdate.updateStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateAppUtils.downloadApk(context, userInfo.getAppInfo().getAndroidUrl());
                }
            }).start();
        } else {
            if (TimeUtil.isValidDateTime(userInfo.getAppInfo().getCurrentTime())) {
                DBHelper.getInstance(context).deleteAll(UserInfo.class);
                userInfo.getAppInfo().setCurrentTime("");
                DBHelper.getInstance(context).save(userInfo);
                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, R.string.time_error, Toast.LENGTH_LONG).show();
                iUpdate.loginButtonRecover();
            }
        }
    }

    public void dbLogin() {
        List<UserInfo> list = DBHelper.getInstance(context).query(UserInfo.class);

        if (list != null && list.size() > 0 && list.get(0).getUser().getEmpCode().equals(currentInputCode)) {
            toMain(list.get(0));
        } else {
            iUpdate.loginButtonRecover();
            Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    public interface IUpdate {
        void updateStart();

        void loginButtonRecover();
    }
}
