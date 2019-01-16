package com.logic.mes.presenter.main;

import android.content.Context;

import com.logic.mes.MyApplication;
import com.logic.mes.autosubmit.DataAutoSubmit;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.fragment.FragmentFactory;

import java.util.List;

public class MainPresenter {
    private IMain iMain;
    private Thread timeingSubmit;
    private Context context;

    public MainPresenter(IMain iMain, final Context context) {
        this.iMain = iMain;
        this.context = context;

        if (MyApplication.offlineAble) {
            timeingSubmit = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (MyApplication.netAble) {
                        DataAutoSubmit.getInstance().autoSubmit(context);
                        try {
                            Thread.sleep(30 * 60 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            timeingSubmit.start();
        }
    }

    public void getAuthTags(UserInfo userInfo) {
        List<BaseTagFragment> tags = FragmentFactory.getInstance().getFragmentsByProcesses(userInfo.getProduceDef());
        iMain.setTags(tags);
    }

    public void autoSubmitData() {
        if (MyApplication.offlineAble) {
            DataAutoSubmit.getInstance().autoSubmit(context);
        }
    }

    public void stopSubmitData() {
        if (MyApplication.offlineAble) {
            DataAutoSubmit.getInstance().shutdownPool();
            timeingSubmit.interrupt();
        }
    }
}
