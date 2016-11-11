package com.logic.mes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public class ScanUtil {
    IntentFilter mFilter;
    Intent intent;
    BroadcastReceiver mReceiver;
    IScanReceiver receiver;
    int scanCode;
    Context context;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == -1) {
                receiver.error();
            }
        }
    };


    public ScanUtil(Context context) {
        this.context = context;
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
        intent = new Intent("ACTION_BAR_TRIGSCAN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                final String scanStatus = intent.getStringExtra("EXTRA_SCAN_STATE");

                if ("ok".equals(scanStatus)) {
                    receiver.receive(scanResult, scanCode);
                } else {
                    receiver.error();
                }
            }
        };
        context.registerReceiver(mReceiver, mFilter);
    }

    public void send(final IScanReceiver receiver, int scanCode) {
        this.scanCode = scanCode;
        this.receiver = receiver;
        context.sendBroadcast(intent);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = -1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void release() {
        context.unregisterReceiver(mReceiver);
        mFilter = null;
        mReceiver = null;
    }
}
