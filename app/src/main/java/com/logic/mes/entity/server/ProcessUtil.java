package com.logic.mes.entity.server;

import android.content.Context;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
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
    ProcessBase processData;
    SimpleDateFormat sdf;

    public ProcessUtil(Context context) {
        this.context = context;
        this.serverDataReceiver = this;
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    public void submit(SubmitResultReceiver receiver, ProcessBase processData) {
        this.processData = processData;
        serverObserver = new ServerObserver(serverDataReceiver, "", null);
        this.submitResultReceiver = receiver;


        List<User> userList = DBHelper.getInstance(context).query(User.class);
        if (userList != null && userList.size() > 0) {

            ProcessSubmit processSubmit = new ProcessSubmit();
            User user = userList.get(0);

            processSubmit.setUserCode(user.getEmpCode());
            processSubmit.setUserOrg(user.getOrgid_mes().toString());
            processSubmit.setUserName(user.getEmpName());

            String now = sdf.format(new Date());

            if (processData.getLrsj() != null && !processData.getLrsj().equals("")) {
                now = processData.getLrsj();
            } else {
                //记录操作时间 用于提交失败后保存
                this.processData.setLrsj(now);
            }

            processSubmit.setOperationTime(now);
            processSubmit.setProduceCode(processData.getCode());
            List<ProcessItem> items = ItemValueConverter.convert(processData);

            if (items.size() > 0) {
                processSubmit.setItems(items);
                NetUtil.SetObserverCommonAction(NetUtil.getServices(false).brickSubmit(processSubmit))
                        .subscribe(serverObserver);
            } else {
                MyApplication.toast(R.string.submit_data_error);
            }
        } else {
            MyApplication.toast(R.string.emp_info_error);
        }
    }

    @Override
    public void serverData(ServerResult res) {
        if (res != null) {
            if (res.getCode().equals("0")) {
                MyApplication.toast(R.string.submit_ok);
                DBHelper.getInstance(context).delete(processData.getClass());
                submitResultReceiver.submitOk();
            } else {
                if (res.getInfo() != null && !res.getInfo().equals("")) {
                    MyApplication.toast(res.getInfo());
                }
                this.serverError();
            }
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void serverError() {
        //保存这个工序数据
        DBHelper.getInstance(context).delete(processData.getClass());
        DBHelper.getInstance(context).save(processData);
    }


    public interface SubmitResultReceiver {
        void submitOk();
    }

}
