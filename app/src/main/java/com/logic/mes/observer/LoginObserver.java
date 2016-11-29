package com.logic.mes.observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.TimeUtil;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.db.DBHelper;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.base.UserInfoResult;
import com.logic.mes.update.UpdateAppUtils;

import java.util.List;

import rx.Observer;


public class LoginObserver implements Observer<UserInfoResult> {

    public Context context;
    public IUpdate iUpdate;
    public static String currentInputCode = "";
    public MaterialDialog noticeDialog;
    UserInfo userInfo;
    Spinner spinner;
    String chooseOrg;
    private ArrayAdapter<String> adapter;

    public LoginObserver(Context context, IUpdate iUpdate) {
        this.context = context;
        this.iUpdate = iUpdate;


        noticeDialog = new MaterialDialog(this.context);
        View view = View.inflate(context, R.layout.org_choose, null);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chooseOrg = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        noticeDialog.setTitle(R.string.choose_org);
        noticeDialog.setContentView(view);
        noticeDialog.setPositiveButton(R.string.choose, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orgChooseCallback();
                noticeDialog.dismiss(view);
            }
        });

        adapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
                if (true) {
                    this.userInfo = userInfo;

                    adapter.clear();
                    adapter.add("a");
                    adapter.add("b");
                    adapter.notifyDataSetChanged();

                    noticeDialog.show();
                } else {
                    activityTo(userInfo);
                }
            } else {
                Toast.makeText(context, R.string.time_error, Toast.LENGTH_LONG).show();
                iUpdate.loginButtonRecover();
            }
        }
    }

    public void orgChooseCallback() {
        Log.d("mes", "chooseOrg:" + chooseOrg);

        //根据值取id


        //activityTo(this.userInfo);
    }

    public void activityTo(UserInfo userInfo) {
        DBHelper.getInstance(context).deleteAll(UserInfo.class);
        userInfo.getAppInfo().setCurrentTime("");
        DBHelper.getInstance(context).save(userInfo);
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        intent.putExtras(bundle);
        context.startActivity(intent);
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
