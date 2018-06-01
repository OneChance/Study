package com.logic.mes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScanUtil {
    private IntentFilter mFilter;
    private BroadcastReceiver mReceiver;
    private IScanReceiver receiver;
    private int scanCode;
    private Context context;

    ScanUtil(Context context) {
        this.context = context;


        if (MyApplication.product.equals("msm8610")) {
            mFilter = new IntentFilter("ACTION_BAR_SCAN");
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String scanResult = scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                    String scanStatus = intent.getStringExtra("EXTRA_SCAN_STATE");
                    if ("ok".equals(scanStatus)) {
                        receiver.scanReceive(scanResult, scanCode);
                    } else {
                        receiver.scanError();
                    }
                }
            };
        } else if (MyApplication.product.equals("CT50")) {
            mFilter = new IntentFilter("com.android.service_scanner_appdata");
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ("com.android.service_scanner_appdata".equals(intent.getAction())) {
                        String scanResult = intent.getStringExtra("data");
                        receiver.scanReceive(scanResult, scanCode);
                    }
                }
            };
        }

        context.registerReceiver(mReceiver, mFilter);
    }

    public void setReceiver(final IScanReceiver receiver, int scanCode) {
        this.scanCode = scanCode;
        this.receiver = receiver;
    }

    public IScanReceiver getReceiver() {
        return this.receiver;
    }

    void release() {
        context.unregisterReceiver(mReceiver);
        mFilter = null;
        mReceiver = null;
    }
}
