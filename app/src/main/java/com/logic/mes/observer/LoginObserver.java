package com.logic.mes.observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.base.UserData;

import rx.Observer;


public class LoginObserver implements Observer<UserData> {

    public Context context;


    public LoginObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNext(UserData userData) {

        String msg = userData.getMsg();

        if (msg.equals("")) {

            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userData", userData);
            intent.putExtras(bundle);

            context.startActivity(intent);
        } else {
            MyApplication.toast(msg);
        }
    }

}
