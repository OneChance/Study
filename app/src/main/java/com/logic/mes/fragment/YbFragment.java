package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.DataUtil;
import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.entity.process.YbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.yb;

public class YbFragment extends BaseTagFragment implements IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public YbFragment() {
        this.tagNameId = R.string.yb_tab_name;
    }

    @InjectView(R.id.yb_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.yb_b_submit)
    Button bSubmit;
    @InjectView(R.id.yb_b_clear)
    Button bClear;
    @InjectView(R.id.yb_v_yzd)
    EditText yzd;
    @InjectView(R.id.yb_v_hbp)
    EditText hbp;
    @InjectView(R.id.yb_v_zb)
    EditText zb;
    @InjectView(R.id.yb_v_dp)
    EditText dp;
    @InjectView(R.id.yb_v_kxs)
    EditText kxs;
    @InjectView(R.id.yb_v_lds)
    EditText lds;
    @InjectView(R.id.yb_v_zqbb)
    EditText zqbb;
    @InjectView(R.id.yb_v_qps)
    EditText qps;

    FragmentActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

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

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "yb", activity);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first, false);
                } else {
                    YbProduct yb = createYb();
                    new ProcessUtil(activity).submit(submitResultReceiver, yb, userInfo.getUser());
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        EditTextUtil.setNoKeyboard(yzd);
        EditTextUtil.setNoKeyboard(hbp);
        EditTextUtil.setNoKeyboard(zb);
        EditTextUtil.setNoKeyboard(dp);
        EditTextUtil.setNoKeyboard(kxs);
        EditTextUtil.setNoKeyboard(lds);
        EditTextUtil.setNoKeyboard(zqbb);
        EditTextUtil.setNoKeyboard(qps);

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbh.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res,"yb"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        cj = data.getVal("qp_cj");
        EditTextUtil.setTextEnd(yzd, data.getVal("yb_yzd"));
        EditTextUtil.setTextEnd(hbp, data.getVal("yb_hbp"));
        EditTextUtil.setTextEnd(zb, data.getVal("yb_zb"));
        EditTextUtil.setTextEnd(dp, data.getVal("yb_dp"));
        EditTextUtil.setTextEnd(kxs, data.getVal("yb_kxs"));
        EditTextUtil.setTextEnd(lds, data.getVal("yb_lds"));
        EditTextUtil.setTextEnd(zqbb, data.getVal("yb_zqbb"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    public void setPbjValue(YbProduct yb) {
        jzbh.setText(yb.getBrickId());
        EditTextUtil.setTextEnd(yzd, yb.getYzd());
        EditTextUtil.setTextEnd(hbp, yb.getHbp());
        EditTextUtil.setTextEnd(zb, yb.getZb());
        EditTextUtil.setTextEnd(dp, yb.getDp());
        EditTextUtil.setTextEnd(kxs, yb.getKxs());
        EditTextUtil.setTextEnd(lds, yb.getLds());
        EditTextUtil.setTextEnd(zqbb, yb.getZqbb());
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
        yb.setYzd(yzd.getText().toString());
        yb.setHbp(hbp.getText().toString());
        yb.setZb(zb.getText().toString());
        yb.setDp(dp.getText().toString());
        yb.setKxs(kxs.getText().toString());
        yb.setLds(lds.getText().toString());
        yb.setZqbb(zqbb.getText().toString());
        yb.setQps(qps.getText().toString());
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
        yzd.setText("");
        hbp.setText("");
        zb.setText("");
        dp.setText("");
        kxs.setText("");
        lds.setText("");
        zqbb.setText("");
        qps.setText("");
    }

    @Override
    public void serverError(Throwable e) {

    }

    /***
     * 计算切片碎
     */
    @OnTextChanged(value = {R.id.yb_v_yzd, R.id.yb_v_hbp, R.id.yb_v_zb, R.id.yb_v_dp, R.id.yb_v_kxs, R.id.yb_v_lds, R.id.yb_v_zqbb}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calQps() {
        int yzdI = DataUtil.getIntValue(yzd.getText().toString());
        int hbpI = DataUtil.getIntValue(hbp.getText().toString());
        int zbI = DataUtil.getIntValue(zb.getText().toString());
        int dpI = DataUtil.getIntValue(dp.getText().toString());
        int kxsI = DataUtil.getIntValue(kxs.getText().toString());
        int ldsI = DataUtil.getIntValue(lds.getText().toString());
        int zqbbI = DataUtil.getIntValue(zqbb.getText().toString());

        int qpsI = 0;

        if (!cj.equals("")) {
            qpsI = new BigDecimal(yzdI).add(new BigDecimal(hbpI)).add(new BigDecimal(zbI)).add(new BigDecimal(dpI)).add(new BigDecimal(kxsI)).add(new BigDecimal(ldsI)).add(new BigDecimal(zqbbI)).divide(new BigDecimal(cj), 0, BigDecimal.ROUND_DOWN).intValue();
        } else {
            qpsI = new BigDecimal(yzdI).add(new BigDecimal(hbpI)).add(new BigDecimal(zbI)).add(new BigDecimal(dpI)).add(new BigDecimal(kxsI)).add(new BigDecimal(ldsI)).add(new BigDecimal(zqbbI)).intValue();
        }

        qps.setText((qpsI + ""));

    }

    @Override
    public void preventSubmit() {
        bSubmit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        bSubmit.setVisibility(View.VISIBLE);
    }
}
