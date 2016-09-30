package com.logic.mes;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MyApplication extends Application {
    private static Context context;
    public static ScanUtil scanUtil;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        scanUtil = new ScanUtil(context);
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        scanUtil.release();
        super.onTerminate();
    }

    public static void toast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}