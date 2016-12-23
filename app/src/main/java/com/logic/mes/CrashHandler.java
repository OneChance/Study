package com.logic.mes;


import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

public class CrashHandler implements
        Thread.UncaughtExceptionHandler {
    private static final Object lock = new Object();

    private CrashHandler() {
        // Empty Constractor
    }

    private static CrashHandler mCrashHandler;
    private Context mContext;

    public static CrashHandler getInstance() {
        synchronized (lock) {
            if (mCrashHandler == null) {
                synchronized (lock) {
                    if (mCrashHandler == null) {
                        mCrashHandler = new CrashHandler();
                    }
                }
            }

            return mCrashHandler;
        }
    }

    /* 初始化 */
    void init(Context context) {
        this.mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();

                Toast.makeText(mContext, "程序发生了点小意外，即将重启，请稍等... " +
                        ex.getMessage(), Toast.LENGTH_LONG).show();

                Looper.loop();

                // 将Activity的栈清空
                MyApplication.exit();

                // 关闭虚拟机，彻底释放内存空间
                android.os.Process.killProcess(android.os.Process.myPid());

                System.exit(1);

            }
        }).start();
    }
}
