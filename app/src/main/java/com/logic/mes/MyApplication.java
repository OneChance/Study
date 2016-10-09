package com.logic.mes;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MyApplication extends Application {
    private static Context context;
    public static ScanUtil scanUtil;
    public static String VERSION = "1.0.0";

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

    public static void toast(int resId){
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static String getResString(int resId){
        return context.getResources().getString(resId);
    }
}