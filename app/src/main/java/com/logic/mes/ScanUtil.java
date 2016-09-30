package com.logic.mes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScanUtil {
    IntentFilter mFilter;
    Intent intent;
    BroadcastReceiver mReceiver;
    IScanReceiver receiver;
    int scanCode;
    Context context;

    public ScanUtil(Context context){
        this.context = context;
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
        intent = new Intent("ACTION_BAR_TRIGSCAN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                receiver.receive(scanResult,scanCode);
            }
        };
        context.registerReceiver(mReceiver, mFilter);
    }

    public void send(IScanReceiver receiver,int scanCode){
        this.scanCode = scanCode;
        this.receiver = receiver;
        context.sendBroadcast(intent);
    }

    public void release(){
        context.unregisterReceiver(mReceiver);
        mFilter=null;
        mReceiver=null;
    }
}