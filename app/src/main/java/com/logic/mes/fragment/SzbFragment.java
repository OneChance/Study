package com.logic.mes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.process.SzbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SzbFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public SzbFragment() {
        this.tagNameId = R.string.szb_tab_name;
    }

    @BindView(R.id.szb_v_brickid)
    TextView brickId;
    @BindView(R.id.szb_v_qgbh)
    TextView qgbh;
    @BindView(R.id.szb_v_gw)
    TextView gw;
    @BindView(R.id.szb_v_kjg)
    TextView kjg;
    @BindView(R.id.szb_v_cszbcd)
    EditText cszbcd;
    @BindView(R.id.szb_v_dywzbb)
    EditText dywzbb;
    @BindView(R.id.szb_ccwz_group)
    RadioGroup ccwz;

    @BindView(R.id.szb_b_submit)
    Button submit;
    @BindView(R.id.szb_b_clear)
    Button clear;

    SzbProduct szb;
    MainActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    View view;

    boolean visible = false;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.szb, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "cszb", activity);
        submitResultReceiver = this;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brickId.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first, false);
                } else {
                    SzbProduct szb = createSzb();
                    szb.setCode("cszb");
                    new ProcessUtil(activity).submit(submitResultReceiver, szb, userInfo.getUser());
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        brickId.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "cszb"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        qgbh.setText(data.getVal("lb_qgbh"));
        gw.setText(data.getVal("pb_gw"));
        kjg.setText(data.getVal("qp_lrxm"));
        EditTextUtil.setTextEnd(cszbcd, data.getVal("cszb_cszbcd"));
        EditTextUtil.setTextEnd(dywzbb, data.getVal("cszb_dywzbb"));
        setRatioGroup(data.getVal("cszb_ccwz"));
    }

    public SzbProduct createSzb() {
        SzbProduct szb = new SzbProduct();
        szb.setBrickId(brickId.getText().toString());
        szb.setCszbcd(cszbcd.getText().toString());
        szb.setDywzbb(dywzbb.getText().toString());

        RadioButton rb = (RadioButton) (view.findViewById(ccwz.getCheckedRadioButtonId()));
        String ccwz = "";

        if (rb != null) {
            ccwz = rb.getText().toString();
        } else {
            return null;
        }

        szb.setCcwz(ccwz);

        return szb;
    }

    public void setRatioGroup(String ccwzV) {
        if (ccwzV.equals(MyApplication.getResString(R.string.szb_tail))) {
            ccwz.check(R.id.szb_tail);
        } else if (ccwzV.equals(MyApplication.getResString(R.string.szb_middle))) {
            ccwz.check(R.id.szb_middle);
        } else {
            ccwz.check(R.id.szb_head);
        }
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        brickId.setText(R.string.wait_scan);
        qgbh.setText("");
        gw.setText("");
        kjg.setText("");
        cszbcd.setText("");
        dywzbb.setText("");
        ccwz.check(R.id.szb_head);
    }

    @Override
    public void serverError(Throwable e) {
        //addRow(brick.getText().toString(), "", "", 0.0, "", "", "");
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }


    @Override
    public void submitOk() {
        doAfterSumbit(brickId.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(brickId.getText().toString(), false);
    }
}
