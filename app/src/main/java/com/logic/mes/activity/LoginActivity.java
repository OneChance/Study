package com.logic.mes.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logic.mes.IScanReceiver;
import com.logic.mes.R;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.LoginObserver;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements IScanReceiver{

    private Context context;
    @InjectView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
    IScanReceiver receiver;
    LoginObserver loginObserver;
    @InjectView(R.id.loading)
    LinearLayout loading;

    @Override
    protected void onResume() {
        loading.setVisibility(View.INVISIBLE);
        super.onResume();
    }

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
                receive("555",0);
            }
        });
    }

    @Override
    public void receive(String scanResult,int scanCode) {
        loading.setVisibility(View.VISIBLE);
        NetUtil.SetObserverCommonAction(NetUtil.getServices().Login(scanResult))
                .subscribe(loginObserver);
    }
}
