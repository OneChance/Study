package com.example.zhouhui.study;

import android.app.Application;
import android.content.Context;

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
}