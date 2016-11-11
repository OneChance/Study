package com.logic.mes.observer;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.entity.server.ServerResult;

import rx.Observer;


public class ServerObserver implements Observer<ServerResult> {

    public ServerDataReceiver receiver;

    public ServerObserver(ServerDataReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        MyApplication.toast(R.string.submit_error);
        receiver.error();
        e.printStackTrace();
    }


    @Override
    public void onNext(ServerResult res) {
        receiver.getData(res);
    }

    public interface ServerDataReceiver {
        void getData(ServerResult res);
        void error();
    }
}
