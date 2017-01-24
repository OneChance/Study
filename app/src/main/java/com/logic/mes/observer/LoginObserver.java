package com.logic.mes.observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.logic.mes.entity.base.Org;
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
        MyApplication.netAble = false;
        e.printStackTrace();
    }


    @Override
    public void onNext(UserInfoResult res) {

        MyApplication.netAble = true;

        if (res.getCode() == 0) {
            UserInfo userInfo = res.getDatas();
            toMain(userInfo);
        } else {
            MyApplication.toast(res.getInfo(), false);
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
                if (userInfo.getOrgs() != null && userInfo.getOrgs().size() > 1) {
                    this.userInfo = userInfo;
                    adapter.clear();

                    for (Org org : userInfo.getOrgs()) {
                        String orgName = org.getOrgName();

                        if (orgName.length() > 15) {
                            orgName = "..." + orgName.substring(orgName.length() - 15);
                        }

                        adapter.add(orgName);
                    }

                    adapter.notifyDataSetChanged();

                    noticeDialog.show();
                } else {

                    if(userInfo.getUser()!=null && userInfo.getOrgs()!=null && userInfo.getOrgs().size()>0){
                        userInfo.getUser().setOrgPath(userInfo.getOrgs().get(0).getOrgName());
                        userInfo.getUser().setOrgid_mes(userInfo.getOrgs().get(0).getId());
                    }

                    activityTo(userInfo);
                }
            } else {
                Toast.makeText(context, R.string.time_error, Toast.LENGTH_LONG).show();
                iUpdate.loginButtonRecover();
            }
        }
    }

    private void orgChooseCallback() {
        for (Org org : this.userInfo.getOrgs()) {
            if (org.getOrgName().equals(chooseOrg)) {
                this.userInfo.getUser().setOrgPath(org.getOrgName());
                this.userInfo.getUser().setOrgid_mes(org.getId());
            }
        }

        activityTo(this.userInfo);

    }

    private void activityTo(UserInfo userInfo) {

        String orgPath = "";

        if(userInfo.getUser()!=null){
            orgPath = userInfo.getUser().getOrgPath();
        }

        if(orgPath.equals("")||orgPath.length()<2){
            Toast.makeText(context, R.string.choose_org_error, Toast.LENGTH_LONG).show();
        }else{
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
    }

    private void dbLogin() {
        List<UserInfo> list = DBHelper.getInstance(context).query(UserInfo.class);

        if (list != null && list.size() > 0 && list.get(0).getUser().getEmpCode().equals(currentInputCode)) {
            toMain(list.get(0));
        } else {
            iUpdate.loginButtonRecover();
            MyApplication.toast(R.string.login_local_error, false);
        }
    }

    public interface IUpdate {
        void updateStart();

        void loginButtonRecover();
    }
}
