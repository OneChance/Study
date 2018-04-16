package com.logic.mes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    public static ScanUtil scanUtil;
    public static Integer VERSION = 0;
    public static String CLIENT_TYPE = "PDA";
    private static List<Activity> mList = new LinkedList<>();
    public static boolean netAble = true;
    public static boolean offlineAble = false;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        try {
            VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            VERSION = 9999;
        }
        CrashHandler.getInstance().init(this);
        super.onCreate();
    }

    public static ScanUtil getScanUtil() {
        if (scanUtil == null) {
            scanUtil = new ScanUtil(context);
        }
        return scanUtil;
    }

    @Override
    public void onTerminate() {
        scanUtil.release();
        super.onTerminate();
    }

    /**
     * 字符串为消息内容的提示
     *
     * @param msg     消息内容
     * @param success 是否成功
     */
    public static void toast(String msg, boolean success) {
        customToast(msg, success);
    }

    /**
     * 带持续时间的字符串为消息内容的提示
     *
     * @param msg     消息内容
     * @param success 是否成功
     * @param dur     持续时间
     */
    public static void toast(String msg, boolean success, int dur) {
        customToast(msg, success, dur);
    }

    /**
     * 资源ID为消息内容的提示
     *
     * @param resId   资源ID
     * @param success 是否成功
     */
    public static void toast(int resId, boolean success) {
        String msg = getResString(resId);
        customToast(msg, success);
    }

    /**
     * 带持续时间的资源ID为消息内容的提示
     *
     * @param resId   资源ID
     * @param success 是否成功
     * @param dur     持续时间
     */
    public static void toast(int resId, boolean success, int dur) {
        String msg = getResString(resId);
        customToast(msg, success, dur);
    }

    /**
     * 自定义提示view
     *
     * @param msg     消息内容
     * @param success 是否成功
     */
    public static void customToast(String msg, boolean success) {
        customToast(msg, success, 0);
    }

    /**
     * 带自定义时间的提示view
     *
     * @param msg     消息内容
     * @param success 是否成功
     * @param dur     持续时间
     */
    public static void customToast(String msg, boolean success, int dur) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast, null);
        final Toast toast = new Toast(context);
        TextView textView = (TextView) toastView.findViewById(R.id.toast_notice);
        if (success) {
            textView.setBackgroundColor(context.getResources().getColor(R.color.success));
        } else {
            textView.setBackgroundColor(context.getResources().getColor(R.color.error));
        }
        textView.setText(msg);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);

        if (dur == 0) {
            if (success) {
                dur = 3500;
            } else {
                dur = 10000;
            }
        }

        new CountDownTimer(dur, 1000) {

            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.show();
            }

        }.start();
    }

    public static String getResString(int resId) {
        return context.getResources().getString(resId);
    }

    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void appSendBroadcast(String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    public static void exit() {
        try {

            scanUtil.release();

            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}