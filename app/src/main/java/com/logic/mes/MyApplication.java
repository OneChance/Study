package com.logic.mes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    public static ScanUtil scanUtil;
    public static Integer VERSION = 1;
    private static List<Activity> mList = new LinkedList();

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }

    public static ScanUtil getScanUtil(){
        if(scanUtil==null){
            scanUtil = new ScanUtil(context);
        }
        return scanUtil;
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

    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}