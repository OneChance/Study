package com.logic.mes.observer;

import android.content.Context;
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
    public MaterialDialog noticeDialog;
    public TextView textView;
    public String content;
    public String codeNotVali = ",rk,ck,";


    public ServerObserver(ServerDataReceiver receiver, String code, Context context) {
        this.receiver = receiver;
        this.code = code;
        this.context = context;

        if (context != null && noticeDialog == null) {
            noticeDialog = new MaterialDialog(this.context);
            View view = View.inflate(context, R.layout.dialog_msg, null);

            textView = (TextView) view.findViewById(R.id.dialog_msg_content);
            textView.setSingleLine(false);
            content = context.getResources().getString(R.string.process_submited);
            noticeDialog.setTitle(R.string.notice);
            noticeDialog.setContentView(view);
        }
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        MyApplication.toast(R.string.server_error);
        receiver.serverError();
        e.printStackTrace();
    }

    @Override
    public void onNext(final ServerResult res) {

        if (res.getDatas() != null) {
            if (res.getDatas().getBagDatas() != null && res.getDatas().getBagDatas().size() > 0) {
                Map<String, String> dataMap = res.getDatas().getBagDatas().get(0);
                String lrsj = dataMap.get(code + "_lrsj");

                //验证是否已经提交过
                if (lrsj != null && !lrsj.equals("") && context != null && !codeNotVali.contains("," + code + ",")) {

                    textView.setText(String.format(content, lrsj));
                    noticeDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            receiver.serverData(res);
                            noticeDialog.dismiss(view);
                        }
                    });

                    noticeDialog.setNegativeButton(R.string.no, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            noticeDialog.dismiss(view);
                        }
                    });

                    noticeDialog.show();
                } else {
                    receiver.serverData(res);
                }
            } else {
                MyApplication.toast(R.string.no_data);
            }
        } else {
            receiver.serverData(res);
        }
    }

    public interface ServerDataReceiver {
        void serverData(ServerResult res);

        void clear();

        void serverError();
    }
}
