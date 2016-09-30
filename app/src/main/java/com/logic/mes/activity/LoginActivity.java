package com.logic.mes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.entity.base.UserData;
import com.logic.mes.entity.process.Process;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.LoginObserver;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements IScanReceiver{

    private Context context;
    @InjectView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
    IScanReceiver receiver;
    LoginObserver loginObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        receiver = this;
        loginObserver = new LoginObserver(context);

        ButterKnife.inject(this);

        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启扫描
                //MyApplication.scanUtil.send(receiver, 0);

                UserData userData = new UserData();

                //测试代码
                MyApplication.toast("登录成功");
                userData.setUserName("张三");
                List<Process> pList = new ArrayList<Process>();
                Process p = new Process();
                p.setId(0);
                pList.add(p);
                Process p1 = new Process();
                p1.setId(1);
                pList.add(p1);
                userData.setProcesses(pList);


                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userData", userData);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void receive(String scanResult,int scanCode) {
        NetUtil.SetObserverCommonAction(NetUtil.getServices().Login(scanResult))
                .subscribe(loginObserver);
    }
}
