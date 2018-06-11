package com.logic.mes.fragment;

import android.os.Bundle;
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
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.process.QgsfhProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.qgsfh;

public class QgsfhFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public QgsfhFragment() {
        this.tagNameId = R.string.qgsfh_tab_name;
    }

    @BindView(R.id.qgsfh_v_jzbh)
    TextView jzbh;
    @BindView(R.id.qgsfh_b_submit)
    Button bSubmit;
    @BindView(R.id.qgsfh_b_clear)
    Button bClear;
    @BindView(R.id.qgsfh_v_zb)
    EditText zb;
    @BindView(R.id.qgsfh_v_dp)
    EditText dp;
    @BindView(R.id.qgsfh_v_kxs)
    EditText kxs;
    @BindView(R.id.qgsfh_v_qt)
    EditText qt;
    @BindView(R.id.qgsfh_v_dxfq)
    EditText dxfq;
    @BindView(R.id.qgsfh_v_qps)
    TextView qps;
    String cj = "";

    MainActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(qgsfh, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "qgs", activity);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first, false);
                } else {
                    QgsfhProduct qgsfh = createQgsfh();
                    qgsfh.setCode("qgs");
                    new ProcessUtil(activity).submit(submitResultReceiver, qgsfh, userInfo.getUser());
                }
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

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbh.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "qgs"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {

        cj = data.getVal("qp_cj");

        if (cjError(cj)) {
            MyApplication.toast(R.string.no_cj, false);
        } else {
            EditTextUtil.setTextEnd(zb, calWithCj(data.getRelValWithRes("yb", "qgs", "zb")));
            EditTextUtil.setTextEnd(dp, calWithCj(data.getRelValWithRes("yb", "qgs", "dp")));
            EditTextUtil.setTextEnd(kxs, calWithCj(data.getRelValWithRes("yb", "qgs", "kxs")));
            EditTextUtil.setTextEnd(dxfq, calWithCj(data.getRelValWithRes("yb", "qgs", "dxfq")));
            EditTextUtil.setTextEnd(qt, calWithCj(data.getRelValWithRes("yb", "qgs", "lds")));
        }
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    public void setPbjValue(QgsfhProduct qgsfh) {
        jzbh.setText(qgsfh.getBrickId());
        EditTextUtil.setTextEnd(zb, qgsfh.getZb());
        EditTextUtil.setTextEnd(dp, qgsfh.getDp());
        EditTextUtil.setTextEnd(kxs, qgsfh.getKxs());
        EditTextUtil.setTextEnd(dxfq, qgsfh.getDxfq());
        EditTextUtil.setTextEnd(qt, qgsfh.getQt());
    }

    @Override
    public void submitOk() {
        doAfterSumbit(jzbh.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(jzbh.getText().toString(), false);
    }

    public QgsfhProduct createQgsfh() {
        QgsfhProduct qgsfh = new QgsfhProduct();
        qgsfh.setBrickId(jzbh.getText().toString());
        qgsfh.setZb(zb.getText().toString());
        qgsfh.setDp(dp.getText().toString());
        qgsfh.setKxs(kxs.getText().toString());
        qgsfh.setQps(qps.getText().toString());
        qgsfh.setDxfq(dxfq.getText().toString());
        qgsfh.setQt(qt.getText().toString());
        return qgsfh;
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
    }

    @Override
    public void serverError(Throwable e) {

    }

    /***
     * 计算切片碎
     */
    @OnTextChanged(value = {R.id.qgsfh_v_qt, R.id.qgsfh_v_zb, R.id.qgsfh_v_dp, R.id.qgsfh_v_kxs, R.id.qgsfh_v_dxfq}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calQps() {
        int zbI = DataUtil.getIntValue(zb.getText().toString());
        int dpI = DataUtil.getIntValue(dp.getText().toString());
        int kxsI = DataUtil.getIntValue(kxs.getText().toString());
        int dxfqI = DataUtil.getIntValue(dxfq.getText().toString());
        int qtI = DataUtil.getIntValue(qt.getText().toString());
        int qpsI = new BigDecimal(zbI).add(new BigDecimal(qtI)).add(new BigDecimal(dpI)).add(new BigDecimal(kxsI)).add(new BigDecimal(dxfqI)).intValue();

        qps.setText((qpsI + ""));
    }

    /**
     * @return 返回除以槽距后的值
     */
    public String calWithCj(String[] res) {
        if (res[1].equals("after")) {
            return res[0];
        } else if (res[1].equals("pre")) {
            if (cjError(cj)) {
                return "";
            } else {
                if (res[0].equals("")) {
                    res[0] = "0";
                }
                return new BigDecimal(res[0]).divide(new BigDecimal(cj), 0, BigDecimal.ROUND_HALF_UP).toString();
            }
        }
        return "";
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
