package com.logic.mes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.process.DbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.logic.mes.R.layout.db;

public class DbFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public DbFragment() {
        this.tagNameId = R.string.db_tab_name;
    }

    @BindView(R.id.db_b_add)
    Button add;
    @BindView(R.id.db_b_cd)
    Button cd;
    @BindView(R.id.db_brick_id)
    TextView brickId;

    @BindView(R.id.db_v_type)
    EditText type;
    @BindView(R.id.db_v_cate)
    EditText cate;

    IScanReceiver receiver;
    ServerObserver serverObserver;
    Context context;
    MainActivity activity;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    View view;

    DbProduct dbProduct;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(db, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        submitResultReceiver = this;

        serverObserver = new ServerObserver(this, "db", activity);

        if (dbProduct == null) {
            dbProduct = new DbProduct();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit("add");
            }
        });

        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit("cd");
            }
        });

        return view;
    }

    public void submit(String submitType) {
        try {

            boolean submitOk = true;

            if (submitType.equals("add")) {
                if (cate.getText().toString().equals("")) {
                    submitOk = false;
                    MyApplication.toast(R.string.need_cate, false);
                }
            }

            if (brickId.getText().equals(MyApplication.getResString(R.string.wait_scan))) {
                MyApplication.toast(R.string.form_required, false);
                submitOk = false;
            }

            if (submitOk) {

                dbProduct.setBrickId(brickId.getText().toString());
                dbProduct.setType(type.getText().toString());
                dbProduct.setCate(cate.getText().toString());
                dbProduct.setCode("db");

                new ProcessUtil(activity).submit(submitResultReceiver, dbProduct, userInfo.getUser());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void scanReceive(String res, int scanCode) {
        brickId.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "db"))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void serverData() {
        type.setText(data.getVal("db_type"));
        cate.setText(data.getVal("db_cate"));
    }

    @Override
    public void setData(ServerResult res) {
        this.data = res;
    }

    @Override
    public void clear() {
        brickId.setText(R.string.wait_scan);
        type.setText("");
        cate.setText("");
    }

    @Override
    public void serverError(Throwable e) {

    }

    @Override
    public void submitOk() {
        doAfterSumbit(brickId.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(brickId.getText().toString(), false);
    }


    @Override
    public void preventSubmit() {
        add.setVisibility(View.INVISIBLE);
        cd.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        add.setVisibility(View.VISIBLE);
        cd.setVisibility(View.VISIBLE);
    }
}
