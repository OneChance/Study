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
import com.logic.mes.R;
import com.logic.mes.entity.process.QgsfhProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.qgsfh;

public class QgsfhFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public QgsfhFragment() {
        this.tagNameId = R.string.qgsfh_tab_name;
    }

    @InjectView(R.id.qgsfh_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.qgsfh_b_submit)
    Button bSubmit;
    @InjectView(R.id.qgsfh_b_clear)
    Button bClear;
    @InjectView(R.id.qgsfh_v_yzd)
    EditText yzd;
    @InjectView(R.id.qgsfh_v_hbp)
    EditText hbp;
    @InjectView(R.id.qgsfh_v_zb)
    EditText zb;
    @InjectView(R.id.qgsfh_v_dp)
    EditText dp;
    @InjectView(R.id.qgsfh_v_kxs)
    EditText kxs;
    @InjectView(R.id.qgsfh_v_lds)
    EditText lds;
    @InjectView(R.id.qgsfh_v_zqbb)
    EditText zqbb;
    @InjectView(R.id.qgsfh_v_qps)
    TextView qps;

    FragmentActivity activity;
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

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "qgs", activity);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
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

        EditTextUtil.setNoKeyboard(yzd);
        EditTextUtil.setNoKeyboard(hbp);
        EditTextUtil.setNoKeyboard(zb);
        EditTextUtil.setNoKeyboard(dp);
        EditTextUtil.setNoKeyboard(kxs);
        EditTextUtil.setNoKeyboard(lds);
        EditTextUtil.setNoKeyboard(zqbb);

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbh.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        EditTextUtil.setTextEnd(yzd, data.getRelVal("yb", "qgs", "yzd"));
        EditTextUtil.setTextEnd(hbp, data.getRelVal("yb", "qgs", "hbp"));
        EditTextUtil.setTextEnd(zb, data.getRelVal("yb", "qgs", "zb"));
        EditTextUtil.setTextEnd(dp, data.getRelVal("yb", "qgs", "dp"));
        EditTextUtil.setTextEnd(kxs, data.getRelVal("yb", "qgs", "kxs"));
        EditTextUtil.setTextEnd(lds, data.getRelVal("yb", "qgs", "lds"));
        EditTextUtil.setTextEnd(zqbb, data.getRelVal("yb", "qgs", "zqbb"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public void setPbjValue(QgsfhProduct qgsfh) {
        jzbh.setText(qgsfh.getBrickId());
        EditTextUtil.setTextEnd(yzd, qgsfh.getYzd());
        EditTextUtil.setTextEnd(hbp, qgsfh.getHbp());
        EditTextUtil.setTextEnd(zb, qgsfh.getZb());
        EditTextUtil.setTextEnd(dp, qgsfh.getDp());
        EditTextUtil.setTextEnd(kxs, qgsfh.getKxs());
        EditTextUtil.setTextEnd(lds, qgsfh.getLds());
        EditTextUtil.setTextEnd(zqbb, qgsfh.getZqbb());
    }

    @Override
    public void submitOk() {
        clear();
    }

    public QgsfhProduct createQgsfh() {
        QgsfhProduct qgsfh = new QgsfhProduct();
        qgsfh.setBrickId(jzbh.getText().toString());
        qgsfh.setYzd(yzd.getText().toString());
        qgsfh.setHbp(hbp.getText().toString());
        qgsfh.setZb(zb.getText().toString());
        qgsfh.setDp(dp.getText().toString());
        qgsfh.setKxs(kxs.getText().toString());
        qgsfh.setLds(lds.getText().toString());
        qgsfh.setZqbb(zqbb.getText().toString());
        qgsfh.setQps(qps.getText().toString());

        return qgsfh;
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
    }

    @Override
    public void serverError() {

    }

    /***
     * @Description 计算切片碎
     */
    @OnTextChanged(value = {R.id.qgsfh_v_yzd, R.id.qgsfh_v_hbp, R.id.qgsfh_v_zb, R.id.qgsfh_v_dp, R.id.qgsfh_v_kxs, R.id.qgsfh_v_lds, R.id.qgsfh_v_zqbb}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calQps() {
        int yzdI = DataUtil.getIntValue(yzd.getText().toString());
        int hbpI = DataUtil.getIntValue(hbp.getText().toString());
        int zbI = DataUtil.getIntValue(zb.getText().toString());
        int dpI = DataUtil.getIntValue(dp.getText().toString());
        int kxsI = DataUtil.getIntValue(kxs.getText().toString());
        int ldsI = DataUtil.getIntValue(lds.getText().toString());
        int zqbbI = DataUtil.getIntValue(zqbb.getText().toString());

        int qpsI = new BigDecimal(yzdI).add(new BigDecimal(hbpI)).add(new BigDecimal(zbI)).add(new BigDecimal(dpI)).add(new BigDecimal(kxsI)).add(new BigDecimal(ldsI)).add(new BigDecimal(zqbbI)).intValue();
        qps.setText(qpsI + "");
    }
}
