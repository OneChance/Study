package com.logic.mes.observer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.entity.server.ServerResult;

import java.util.Map;

import rx.Observer;


public class ServerObserver implements Observer<ServerResult> {

    public ServerDataReceiver receiver;
    public String code;
    public Context context;
    private MaterialDialog noticeDialog;
    private TextView textView;
    public String content;
    private String codeNotVali = ",by,wx,";
    public static String SERVER_ERROR = "com.mes.logic.SERVER_ERROR";
    public static String SERVER_OK = "com.mes.logic.SERVER_OK";


    public ServerObserver(final ServerDataReceiver receiver, String code, Context context) {
        this.receiver = receiver;
        this.code = code;
        this.context = context;

        if (context != null && !codeNotVali.contains("," + code + ",") && noticeDialog == null) {
            //为需要验证是否已提交过数据的模块创建对话框
            noticeDialog = new MaterialDialog(this.context);
            View view = View.inflate(context, R.layout.dialog_msg, null);

            textView = (TextView) view.findViewById(R.id.dialog_msg_content);
            textView.setSingleLine(false);
            content = context.getResources().getString(R.string.process_submited);
            noticeDialog.setTitle(R.string.notice);
            noticeDialog.setContentView(view);

            noticeDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receiver.serverData();
                    noticeDialog.dismiss(view);
                }
            });

            noticeDialog.setNegativeButton(R.string.no, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noticeDialog.dismiss(view);
                }
            });
        }
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        try {
            MyApplication.appSendBroadcast(SERVER_ERROR);
            receiver.ableSubmit();
            receiver.serverError(e);
            e.printStackTrace();
        } catch (Exception innerE) {
            innerE.printStackTrace();
        }
    }

    private boolean errorCode(String resCode) {
        if (this.code.equals("by") || this.code.equals("wx")) {
            if (resCode.equals("1")) {
                return true;
            }
        } else {
            if (!resCode.equals("0")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNext(ServerResult res) {

        try {

            Log.d("mes", res.toString());

            MyApplication.appSendBroadcast(SERVER_OK);

            if (errorCode(res.getCode())) {
                if (res.getInfo() != null && !res.getInfo().equals("")) {
                    MyApplication.toast(res.getInfo(), false);
                }
                receiver.preventSubmit();
            } else {
                receiver.ableSubmit();
                receiver.setData(res);

                if (res.getDatas() != null) {
                    if (res.getDatas().getBagDatas() != null && res.getDatas().getBagDatas().size() > 0 && !codeNotVali.contains("," + code + ",")) {

                        Map<String, String> dataMap = res.getDatas().getBagDatas().get(0);
                        String lrsj = dataMap.get(code + "_lrsj");

                        //验证是否已经提交过
                        if (lrsj != null && !lrsj.equals("") && context != null) {
                            textView.setText(String.format(content, lrsj));
                            noticeDialog.show();
                        } else {
                            receiver.serverData();
                        }

                    } else {
                        receiver.serverData();
                    }
                } else {
                    receiver.serverData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ServerDataReceiver {
        /***
         * 把数据传递给监者
         *
         * @param res 从服务器获得的数据
         */
        void setData(ServerResult res);

        /***
         * 通知监听者使用服务器返回的数据
         */
        void serverData();

        /***
         * 通知监听者响应异常
         */
        void serverError(Throwable e);

        /**
         * 通知监听者阻止提交
         */
        void preventSubmit();

        /**
         * 通知监听者允许提交
         */
        void ableSubmit();
    }
}
