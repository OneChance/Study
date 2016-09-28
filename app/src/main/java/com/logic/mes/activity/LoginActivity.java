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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements IScanReceiver{

    private Context context;
    @InjectView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
    IScanReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        receiver = this;

        ButterKnife.inject(this);

        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启扫描
                MyApplication.scanUtil.send(receiver, 0);
            }
        });
    }

    @Override
    public void receive(String scanResult,int scanCode) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra("identity", scanResult);
        context.startActivity(intent);
    }
}
