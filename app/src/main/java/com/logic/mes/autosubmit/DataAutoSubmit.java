package com.logic.mes.autosubmit;


import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.server.ProcessSubmit;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observer;

public class DataAutoSubmit {
    private static DataAutoSubmit dataAutoSubmit;
    private ExecutorService cachedThreadPool;

    public static DataAutoSubmit getInstance() {
        if (dataAutoSubmit == null) {
            dataAutoSubmit = new DataAutoSubmit();
        }
        return dataAutoSubmit;
    }

    private DataAutoSubmit() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void shutdownPool() {
        cachedThreadPool.shutdown();
    }

    public void autoSubmit(Context context) {
        final LiteOrm dbInstance = DBHelper.getInstance(context);
        List<ProcessSubmit> submits = DBHelper.getInstance(context).query(ProcessSubmit.class);
        if (submits.size() > 0) {
            for (final ProcessSubmit submit : submits) {
                cachedThreadPool.execute(new Runnable() {
                    public void run() {
                        doSubmit(dbInstance, submit);
                    }
                });
            }
        }
    }

    private void doSubmit(final LiteOrm dbInstance, final ProcessSubmit submit) {
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).submitCatch(submit))
                .subscribe(new Observer<ServerResult>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(ServerResult serverResult) {
                        dbInstance.delete(submit);
                    }
                });
    }
}
