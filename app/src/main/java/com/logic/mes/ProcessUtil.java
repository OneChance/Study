package com.logic.mes;

import android.app.AlertDialog;
import android.content.Context;

import com.logic.mes.db.DBHelper;
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

    private Context context;
    private SubmitResultReceiver submitResultReceiver;
    private ServerObserver.ServerDataReceiver serverDataReceiver;
    private SimpleDateFormat sdf;
    private ServerResult data;
    private ProcessSubmit processToSubmit;
    private AlertDialog.Builder tipDialog;

    public ProcessUtil(Context context) {
        this.context = context;
        this.serverDataReceiver = this;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tipDialog = new AlertDialog.Builder(context)
                .setTitle(MyApplication.getResString(R.string.dialog_title))
                .setPositiveButton(MyApplication.getResString(R.string.dialog_confirm), null);
    }

    /***
     * @param receiver    提交事件的监听者（各Fragment）
     * @param processData 提交的数据
     */
    public void submit(SubmitResultReceiver receiver, ProcessBase processData, User user) {
        ServerObserver serverObserver = new ServerObserver(serverDataReceiver, "", null);
        this.submitResultReceiver = receiver;

        if (user != null) {

            ProcessSubmit processSubmit = new ProcessSubmit();

            processSubmit.setUserCode(user.getEmpCode());
            processSubmit.setUserOrg(user.getOrgid_mes().toString());
            processSubmit.setUserName(user.getEmpName());
            String now = sdf.format(new Date());
            processSubmit.setOperationTime(now);
            processSubmit.setProduceCode(processData.getCode());
            processSubmit.setBagCode(processData.getBagCode());
            processSubmit.setOrgPath(user.getOrgPath());
            processSubmit.setChangQu("");
            processSubmit.setVersion(MyApplication.VERSION);
            processSubmit.setClientType(MyApplication.CLIENT_TYPE);

            if (processData.getCode().equals("qx")) {
                processSubmit.setMachineCode(LocalConfig.MACHINE_CODE);
            }

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

        if (MyApplication.netAble) {
            if (processSubmit.getProduceCode().equals("jb")) {
                NetUtil.SetObserverCommonAction(NetUtil.getServices(false).cancelBrickGroup(processSubmit))
                        .subscribe(serverObserver);
            } else {
                NetUtil.SetObserverCommonAction(NetUtil.getServices(false).brickSubmit(processSubmit))
                        .subscribe(serverObserver);
            }
        } else {
            DBHelper.getInstance(context).save(processSubmit);
            serverError(null);
        }
    }

    @Override
    public void serverData() {
        if (data.getInfo() != null && !data.getInfo().equals("")) {
            tipDialog.setMessage(data.getInfo()).show();
        }
        MyApplication.toast(MyApplication.getResString(R.string.submit_ok), true);
        submitResultReceiver.submitOk();
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void serverError(Throwable e) {
        if (MyApplication.offlineAble) {
            if (processToSubmit != null) {
                //保存这个工序数据
                MyApplication.toast(R.string.data_save, true);
                DBHelper.getInstance(context).save(processToSubmit);
                submitResultReceiver.submitError();
            }
        } else {
            MyApplication.toast(R.string.submit_error, false);
            submitResultReceiver.submitError();
        }
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }


    public interface SubmitResultReceiver {
        void submitOk();

        void submitError();
    }

}
