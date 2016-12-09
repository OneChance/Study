package com.logic.mes.presenter.main;

import android.content.Context;

import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.server.ProcessSubmit;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.fragment.FragmentFactory;
import com.logic.mes.net.NetUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;

public class MainPresenter {
    private IMain iMain;
    Thread autoSubmit;
    List<ProcessSubmit> submits = new ArrayList<>();
    List<ProcessSubmit> submited = new ArrayList<>();

    public MainPresenter(IMain iMain) {
        this.iMain = iMain;
    }

    public void getAuthTags(UserInfo userInfo) {
        List<BaseTagFragment> tags = FragmentFactory.getFragmentsByProcesses(userInfo.getProduceDef());
        iMain.setTags(tags);
    }

    public void autoSubmitData(final Context context) {

        submits = DBHelper.getInstance(context).query(ProcessSubmit.class);

        if (submits.size() > 0) {
            //启动线程扫描提交数据表
            autoSubmit = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final ProcessSubmit submit : submits) {
                        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).brickSubmit(submit))
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

                    //清除已提交成功的数据
                    clearSubmited(context);
                }
            });
            autoSubmit.start();
        }

    }

    public void stopAutoSubmit(Context context) {
        if (autoSubmit != null) {
            autoSubmit.interrupt();
        }
        clearSubmited(context);
    }

    public void clearSubmited(Context context) {
        submits.clear();
        if (submited.size() > 0) {
            DBHelper.getInstance(context).delete(submited);
        }
        submited.clear();
    }
}
