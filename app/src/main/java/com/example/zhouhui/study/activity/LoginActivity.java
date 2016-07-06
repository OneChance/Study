package com.example.zhouhui.study.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zhouhui.study.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity {

    private Context context;
    @InjectView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;

        ButterKnife.inject(this);

        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启扫描
                Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
                intent.putExtra("timeout", 3);
                sendBroadcast(intent);
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                intent.setClass(context, MainActivity.class);
                Bundle identity = new Bundle();
                identity.putSerializable("identity", scanResult);
                intent.putExtras(identity);
                context.startActivity(intent);
            }
        };

        mFilter = new IntentFilter("ACTION_BAR_SCAN");
        //在用户自行获取数据时，将广播的优先级调到最高 1000，***此处必须***
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播来获取扫描结果
        this.registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        //注销获取扫描结果的广播
        this.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mReceiver = null;
        mFilter = null;
        super.onDestroy();
    }
}
