package com.logic.mes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScanUtil {
    private IntentFilter mFilter;
    private Intent intent;
    private BroadcastReceiver mReceiver;
    private IScanReceiver receiver;
    private int scanCode;
    private Context context;

    ScanUtil(Context context) {
        this.context = context;
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
        intent = new Intent("ACTION_BAR_TRIGSCAN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                final String scanStatus = intent.getStringExtra("EXTRA_SCAN_STATE");

                if ("ok".equals(scanStatus)) {
                    receiver.scanReceive(scanResult, scanCode);
                } else {
                    receiver.scanError();
                }
            }
        };
        context.registerReceiver(mReceiver, mFilter);
    }

    public void setReceiver(final IScanReceiver receiver, int scanCode) {
        this.scanCode = scanCode;
        this.receiver = receiver;
    }

    void release() {
        context.unregisterReceiver(mReceiver);
        mFilter = null;
        mReceiver = null;
    }
}
