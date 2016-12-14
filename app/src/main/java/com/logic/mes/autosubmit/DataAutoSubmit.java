package com.logic.mes.autosubmit;


import android.content.Context;
import android.util.Log;

import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.server.ProcessSubmit;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;

public class DataAutoSubmit {
    private static DataAutoSubmit dataAutoSubmit;

    public static DataAutoSubmit getInstance() {
        if (dataAutoSubmit == null) {
            dataAutoSubmit = new DataAutoSubmit();
        }
        return dataAutoSubmit;
    }

    public synchronized void autoSubmit(Context context) {

        List<ProcessSubmit> submits = DBHelper.getInstance(context).query(ProcessSubmit.class);
        final List<ProcessSubmit> submited = new ArrayList<>();

        if(submits.size()>0){
            for (final ProcessSubmit submit : submits) {

                Log.d("mes", "submit data from database:" + submit.getProduceCode());

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
                                submited.add(submit);
                            }
                        });
            }
        }

        //清除已提交成功的数据
        if (submited.size() > 0) {
            DBHelper.getInstance(context).delete(submited);
        }
    }
}
