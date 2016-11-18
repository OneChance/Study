package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.QxProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

import static com.logic.mes.R.id.qx_v_zj;
import static com.logic.mes.R.layout.qx;

public class QxFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public QxFragment() {
        this.tagNameId = R.string.qx_tab_name;
    }

    @InjectView(R.id.yb_save)
    Button save;
    @InjectView(R.id.qx_v_jzbh_head)
    TextView jzbhHead;
    @InjectView(R.id.qx_b_submit)
    Button bSubmit;
    @InjectView(R.id.qx_b_clear)
    Button bClear;
    @InjectView(R.id.qx_v_station)
    TextView station;
    @InjectView(R.id.qx_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.qx_v_llcps)
    TextView llcps;
    @InjectView(R.id.qx_v_sjcps)
    EditText sjcps;
    @InjectView(R.id.qx_v_hs)
    EditText hs;
    @InjectView(R.id.qx_v_ps)
    EditText ps;
    @InjectView(R.id.qx_v_bb)
    EditText bb;
    @InjectView(R.id.qx_v_yp)
    EditText yp;
    @InjectView(R.id.qx_v_jjqxs)
    EditText jjqxs;
    @InjectView(R.id.qx_v_sbqxs)
    EditText sbqxs;
    @InjectView(R.id.qx_v_qt)
    EditText qt;
    @InjectView(qx_v_zj)
    TextView zj;

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

        View view = inflater.inflate(qx, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "qx", activity);
        submitResultReceiver = this;

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jzbhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    //先清空表
                    DBHelper.getInstance(activity).delete(QxProduct.class);
                    QxProduct qx = createQx();
                    DBHelper.getInstance(activity).save(qx);
                    MyApplication.toast(R.string.product_save_success);
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    QxProduct qx = createQx();
                    qx.setCode("qx");
                    new ProcessUtil(activity).submit(submitResultReceiver, qx);
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        List<QxProduct> plist = DBHelper.getInstance(activity).query(QxProduct.class);
        if (plist.size() > 0) {
            QxProduct qx = plist.get(0);
            setPbjValue(qx);
        }

        EditTextUtil.setNoKeyboard(sjcps);
        EditTextUtil.setNoKeyboard(hs);
        EditTextUtil.setNoKeyboard(ps);
        EditTextUtil.setNoKeyboard(bb);
        EditTextUtil.setNoKeyboard(yp);
        EditTextUtil.setNoKeyboard(jjqxs);
        EditTextUtil.setNoKeyboard(sbqxs);
        EditTextUtil.setNoKeyboard(qt);

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbhHead.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData(ServerResult res) {
        jzbh.setText(res.getVal("ej_BrickID"));
        station.setText(res.getVal("pb_gw"));
        llcps.setText(res.getVal("cp_llcps"));
        EditTextUtil.setTextEnd(sjcps, res.getVal("qx_sjps"));
        EditTextUtil.setTextEnd(hs, res.getVal("qx_hs"));
        EditTextUtil.setTextEnd(ps, res.getVal("qx_ps"));
        EditTextUtil.setTextEnd(bb, res.getVal("qx_bb"));
        EditTextUtil.setTextEnd(yp, res.getVal("qx_yp"));
        EditTextUtil.setTextEnd(jjqxs, res.getVal("qx_tjqxs"));
        EditTextUtil.setTextEnd(sbqxs, res.getVal("qx_qxsbs"));
        EditTextUtil.setTextEnd(qt, res.getVal("qx_qt"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public void setPbjValue(QxProduct qx) {
        jzbhHead.setText(qx.getBrickId());
        jzbh.setText(qx.getBrickId());
        station.setText(qx.getStation());
        llcps.setText(qx.getLlcps());
        sjcps.setText(qx.getSjcps());
        hs.setText(qx.getHs());
        ps.setText(qx.getPs());
        bb.setText(qx.getBb());
        yp.setText(qx.getYp());
        jjqxs.setText(qx.getJjqxs());
        sbqxs.setText(qx.getSbqxs());
        zj.setText(qx.getZj());
        qt.setText(qx.getQt());
    }

    @Override
    public void submitOk() {
        clear();
    }

    public QxProduct createQx() {
        QxProduct qx = new QxProduct();
        qx.setBrickId(jzbh.getText().toString());
        qx.setStation(station.getText().toString());
        qx.setLlcps(llcps.getText().toString());
        qx.setSjcps(sjcps.getText().toString());
        qx.setHs(hs.getText().toString());
        qx.setPs(ps.getText().toString());
        qx.setBb(bb.getText().toString());
        qx.setYp(yp.getText().toString());
        qx.setJjqxs(jjqxs.getText().toString());
        qx.setSbqxs(sbqxs.getText().toString());
        qx.setQt(qt.getText().toString());
        qx.setZj(zj.getText().toString());
        return qx;
    }

    public int getIntValue(String v) {
        return v == null || v.equals("") ? 0 : Integer.parseInt(v);
    }

    @Override
    public void clear() {
        jzbhHead.setText(R.string.wait_scan);
        jzbh.setText("");
        station.setText("");
        llcps.setText("");
        sjcps.setText("");
        hs.setText("");
        ps.setText("");
        bb.setText("");
        yp.setText("");
        jjqxs.setText("");
        sbqxs.setText("");
        zj.setText("0");
        qt.setText("");
        DBHelper.getInstance(activity).delete(QxProduct.class);
    }

    @Override
    public void serverError() {

    }


    @OnTextChanged(value = {R.id.qx_v_ps, R.id.qx_v_bb, R.id.qx_v_yp, R.id.qx_v_jjqxs, R.id.qx_v_sbqxs, R.id.qx_v_qt}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calZj() {
        int psI = getIntValue(ps.getText().toString());
        int bbI = getIntValue(bb.getText().toString());
        int ypV = getIntValue(yp.getText().toString());
        int jjqxsV = getIntValue(jjqxs.getText().toString());
        int sbqxsV = getIntValue(sbqxs.getText().toString());
        int qtV = getIntValue(qt.getText().toString());
        int zjV = new BigDecimal(psI).add(new BigDecimal(bbI)).add(new BigDecimal(ypV)).add(new BigDecimal(jjqxsV)).add(new BigDecimal(sbqxsV)).add(new BigDecimal(qtV)).intValue();
        zj.setText(zjV + "");
    }
}
