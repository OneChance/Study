package com.logic.mes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.server.ProcessSubmit;

import java.util.List;

public class NetStateReceiver extends BroadcastReceiver {

    Thread autoSubmit;

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO Auto-generated method stub
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();

        if (activeInfo == null) {
            //网络断开
            MyApplication.netAble = false;
            //autoSubmit.interrupt();
        } else {
            //网络连接
            MyApplication.netAble = true;

            //启动线程扫描提交数据表
            /*autoSubmit = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<ProcessSubmit> submits = DBHelper.getInstance(context).query(ProcessSubmit.class);
                                for (ProcessSubmit submit : submits) {

                                }
                }
            });
            autoSubmit.start();*/


        }
    }

}
