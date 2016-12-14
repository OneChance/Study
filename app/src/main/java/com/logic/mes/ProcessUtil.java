package com.logic.mes;

import android.content.Context;

import com.logic.mes.entity.base.User;
import com.logic.mes.entity.process.ProcessBase;
import com.logic.mes.entity.server.ItemValueConverter;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.entity.server.ProcessSubmit;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProcessUtil implements ServerObserver.ServerDataReceiver {

    private ServerObserver serverObserver;
    private Context context;
    private SubmitResultReceiver submitResultReceiver;
    private ServerObserver.ServerDataReceiver serverDataReceiver;
    private SimpleDateFormat sdf;
    private ServerResult data;
    private ProcessSubmit processToSubmit;

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

    private void submitData(ServerObserver serverObserver, ProcessSubmit processSubmit) {
        processToSubmit = processSubmit;
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).brickSubmit(processSubmit))
                .subscribe(serverObserver);
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
    public void serverError(Throwable e) {
        if (processToSubmit != null) {
            //保存这个工序数据
            MyApplication.toast(R.string.data_save, true);
            //DBHelper.getInstance(context).save(processToSubmit);
            submitResultReceiver.submitError();
        }
    }


    public interface SubmitResultReceiver {
        void submitOk();

        void submitError();
    }

}
