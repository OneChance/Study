package com.logic.mes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.DataUtil;
import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.entity.process.YbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.yb;

public class YbFragment extends BaseTagFragment implements IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public YbFragment() {
        this.tagNameId = R.string.yb_tab_name;
    }

    @BindView(R.id.yb_v_jzbh)
    TextView jzbh;
    @BindView(R.id.yb_b_submit)
    Button bSubmit;
    @BindView(R.id.yb_b_clear)
    Button bClear;
    @BindView(R.id.yb_v_zb)
    EditText zb;
    @BindView(R.id.yb_v_dp)
    EditText dp;
    @BindView(R.id.yb_v_qt)
    EditText qt;
    @BindView(R.id.yb_v_kxs)
    EditText kxs;
    @BindView(R.id.yb_v_dxfq)
    EditText dxfq;
    @BindView(R.id.yb_v_qps)
    TextView qps;
    @BindView(R.id.yb_v_zgbf)
    CheckBox zgbf;

    MainActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

    MaterialDialog noticeDialog;
    View noteView;
    TextView noteMsgView;
    boolean sfbf = false;

    String cj = "";

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(yb, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "yb", activity);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first, false);
                } else {
                    if (zgbf.isChecked()) {
                        //如果整根报废勾选
                        noticeDialog.show();
                    } else {
                        YbProduct yb = createYb();
                        yb.setSfbf("否");
                        new ProcessUtil(activity).submit(submitResultReceiver, yb, userInfo.getUser());
                    }
                }
            }
        });

        zgbf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    setDisable(zb);
                    setDisable(dp);
                    setDisable(kxs);
                    setDisable(dxfq);
                    setDisable(qt);
                    bfClear();
                } else {
                    setAble(zb);
                    setAble(dp);
                    setAble(kxs);
                    setAble(dxfq);
                    setAble(qt);
                }
            }
        });


        noticeDialog = new MaterialDialog(activity);

        noteView = View.inflate(activity, R.layout.dialog_msg, null);
        noteMsgView = (TextView) noteView.findViewById(R.id.dialog_msg_content);
        noteMsgView.setSingleLine(false);
        noteMsgView.setText(activity.getResources().getString(R.string.scrap_confirm));
        noticeDialog.setTitle(R.string.notice);
        noticeDialog.setContentView(noteView);

        noticeDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YbProduct yb = createYb();
                yb.setSfbf("是");
                new ProcessUtil(activity).submit(submitResultReceiver, yb, userInfo.getUser());
                noticeDialog.dismiss(view);
            }
        });

        noticeDialog.setNegativeButton(R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeDialog.dismiss(view);
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        EditTextUtil.setNoKeyboard(zb);
        EditTextUtil.setNoKeyboard(dp);
        EditTextUtil.setNoKeyboard(kxs);
        EditTextUtil.setNoKeyboard(dxfq);
        EditTextUtil.setNoKeyboard(qt);

        return view;
    }

    private void setDisable(EditText et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
    }

    private void setAble(EditText et) {
        et.setFocusableInTouchMode(true);
        et.setFocusable(true);
        et.requestFocus();
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbh.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "yb"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        cj = data.getVal("qp_cj");

        if (cjError(cj)) {
            MyApplication.toast(R.string.no_cj, false);
        }

        EditTextUtil.setTextEnd(zb, data.getVal("yb_zb"));
        EditTextUtil.setTextEnd(dp, data.getVal("yb_dp"));
        EditTextUtil.setTextEnd(kxs, data.getVal("yb_kxs"));
        EditTextUtil.setTextEnd(dxfq, data.getVal("yb_dxfq"));
        EditTextUtil.setTextEnd(qt, data.getVal("yb_lds"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    public void setPbjValue(YbProduct yb) {
        jzbh.setText(yb.getBrickId());
        EditTextUtil.setTextEnd(zb, yb.getZb());
        EditTextUtil.setTextEnd(dp, yb.getDp());
        EditTextUtil.setTextEnd(kxs, yb.getKxs());
        EditTextUtil.setTextEnd(dxfq, yb.getDxfq());
        EditTextUtil.setTextEnd(qt, yb.getQt());
    }

    @Override
    public void submitOk() {
        doAfterSumbit(jzbh.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(jzbh.getText().toString(), false);
    }

    public YbProduct createYb() {
        YbProduct yb = new YbProduct();
        yb.setBrickId(jzbh.getText().toString());
        yb.setZb(zb.getText().toString());
        yb.setDp(dp.getText().toString());
        yb.setKxs(kxs.getText().toString());
        yb.setDxfq(dxfq.getText().toString());

        //根据断线放弃计算断线片数
        double dxfqV = DataUtil.getDoubleValue(dxfq.getText().toString());
        int dxps = new BigDecimal(dxfqV).divide(new BigDecimal(cj), 0, BigDecimal.ROUND_HALF_UP).intValue();
        yb.setDxps(dxps + "");

        yb.setQps(qps.getText().toString());
        yb.setQt(qt.getText().toString());
        yb.setCode("yb");
        return yb;
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        jzbh.setText(R.string.wait_scan);
        zb.setText("");
        dp.setText("");
        kxs.setText("");
        qps.setText("");
        dxfq.setText("");
        qt.setText("");
        zgbf.setChecked(false);
    }

    public void bfClear() {
        zb.setText("");
        dp.setText("");
        kxs.setText("");
        qps.setText("");
        dxfq.setText("");
        qt.setText("");
    }


    @Override
    public void serverError(Throwable e) {

    }

    /***
     * 计算切片碎
     */
    @OnTextChanged(value = {R.id.yb_v_qt, R.id.yb_v_zb, R.id.yb_v_dp, R.id.yb_v_kxs}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calQps() {
        double zbI = DataUtil.getDoubleValue(zb.getText().toString());
        double dpI = DataUtil.getDoubleValue(dp.getText().toString());
        double kxsI = DataUtil.getDoubleValue(kxs.getText().toString());
        double qtI = DataUtil.getDoubleValue(qt.getText().toString());

        int qpsI = 0;

        if (!cjError(cj)) {
            qpsI = new BigDecimal(zbI).add(new BigDecimal(qtI)).add(new BigDecimal(dpI)).add(new BigDecimal(kxsI)).divide(new BigDecimal(cj), 0, BigDecimal.ROUND_HALF_UP).intValue();
            qps.setText((qpsI + ""));
        } else {
            qps.setText("");
        }
    }

    @Override
    public void preventSubmit() {
        bSubmit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        bSubmit.setVisibility(View.VISIBLE);
    }

    public boolean cjError(String cj) {

        if (cj.equals("")) {
            return true;
        }

        try {
            double cjD = Double.parseDouble(cj);
            if (cjD <= 0) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }
}
