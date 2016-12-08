package com.logic.mes.entity.server;

import android.content.Context;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.entity.base.User;
import com.logic.mes.entity.process.ProcessBase;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProcessUtil implements ServerObserver.ServerDataReceiver {

    ServerObserver serverObserver;
    Context context;
    SubmitResultReceiver submitResultReceiver;
    ServerObserver.ServerDataReceiver serverDataReceiver;
    SimpleDateFormat sdf;
    ServerResult data;

    public ProcessUtil(Context context) {
        this.context = context;
        this.serverDataReceiver = this;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /***
     * @param receiver    提交事件的监听者（各Fragment）
     * @param processData 提交的数据
     */
    public void submit(SubmitResultReceiver receiver, ProcessBase processData, User user) {
        serverObserver = new ServerObserver(serverDataReceiver, "", null);
        this.submitResultReceiver = receiver;

        if (user != null) {

            ProcessSubmit processSubmit = new ProcessSubmit();

            processSubmit.setUserCode(user.getEmpCode());
            processSubmit.setUserOrg(user.getOrgid_mes().toString());
            processSubmit.setUserName(user.getEmpName());
            String now = sdf.format(new Date());
            processSubmit.setOperationTime(now);
            processSubmit.setProduceCode(processData.getCode());

            List<ProcessItem> items = ItemValueConverter.convert(processData);

            if (items.size() > 0) {
                processSubmit.setItems(items);
                submitData(serverObserver, processSubmit);
            } else {
                MyApplication.toast(R.string.submit_data_error, false);
            }
        } else {
            MyApplication.toast(R.string.emp_info_error, false);
        }
    }

    public void submitData(ServerObserver serverObserver, ProcessSubmit processSubmit) {
        if (MyApplication.netAble) {
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).brickSubmit(processSubmit))
                    .subscribe(serverObserver);
        } else {
            //添加到提交数据表
        }
    }

    @Override
    public void serverData() {
        try {
            if (data != null) {
                if (data.getCode().equals("0")) {
                    MyApplication.toast(R.string.submit_ok, true);
                    submitResultReceiver.submitOk();
                } else {
                    if (data.getInfo() != null && !data.getInfo().equals("")) {
                        MyApplication.toast(data.getInfo(), false);
                    }
                    this.serverError();
                }
            }
        } catch (Exception e) {
            MyApplication.toast(e.getMessage(), false);
        }
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {

    }

    @Override
    public void serverError() {
        //保存这个工序数据

    }


    public interface SubmitResultReceiver {
        void submitOk();
    }

}
