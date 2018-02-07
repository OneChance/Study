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
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.base.SysConfig;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.process.QxProduct;
import com.logic.mes.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnTextChanged;

import static com.logic.mes.R.id.qx_v_zj;
import static com.logic.mes.R.layout.qx;

public class QxFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public QxFragment() {
        this.tagNameId = R.string.qx_tab_name;
    }

    @BindView(R.id.qx_v_jzbh_head)
    TextView jzbhHead;
    @BindView(R.id.qx_b_submit)
    Button bSubmit;
    @BindView(R.id.qx_b_clear)
    Button bClear;
    @BindView(R.id.qx_v_cc)
    TextView cc;
    @BindView(R.id.qx_v_jzdj)
    TextView jzdj;
    @BindView(R.id.qx_v_db)
    TextView db;
    @BindView(R.id.qx_v_station)
    TextView station;
    @BindView(R.id.qx_v_jzbh)
    TextView jzbh;
    @BindView(R.id.qx_v_llcps)
    TextView llcps;
    @BindView(R.id.qx_v_sjcps)
    EditText sjcps;
    @BindView(R.id.qx_v_hs)
    EditText hs;
    @BindView(R.id.qx_v_mhps)
    EditText mhps;
    @BindView(R.id.qx_v_ps)
    EditText ps;
    @BindView(R.id.qx_v_bb)
    EditText bb;
    @BindView(R.id.qx_v_yp)
    EditText yp;
    @BindView(R.id.qx_v_jjqxs)
    EditText jjqxs;
    @BindView(R.id.qx_v_sbqxs)
    EditText sbqxs;
    @BindView(R.id.qx_v_qt)
    EditText qt;
    @BindView(qx_v_zj)
    TextView zj;
    @BindView(R.id.qx_v_zqqss)
    EditText zqqss;
    @BindView(R.id.qx_v_qps)
    TextView qpsV;


    String xwcd = "0";
    String yxbc = "0";
    String cj = "1";
    String qps = "0";

    SysConfig sysConfig;

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

        View view = inflater.inflate(qx, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "qx", activity);
        submitResultReceiver = this;

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first, false);
                } else {
                    QxProduct qx = createQx();
                    qx.setCode("qx");
                    new ProcessUtil(activity).submit(submitResultReceiver, qx, userInfo.getUser());
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        EditTextUtil.setNoKeyboard(sjcps);
        EditTextUtil.setNoKeyboard(hs);
        EditTextUtil.setNoKeyboard(mhps);
        EditTextUtil.setNoKeyboard(ps);
        EditTextUtil.setNoKeyboard(bb);
        EditTextUtil.setNoKeyboard(yp);
        EditTextUtil.setNoKeyboard(jjqxs);
        EditTextUtil.setNoKeyboard(sbqxs);
        EditTextUtil.setNoKeyboard(zqqss);
        EditTextUtil.setNoKeyboard(qt);


        UserInfo userInfo = ((MainActivity) getActivity()).getUserInfo();
        sysConfig = userInfo.getSysConfig();

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbhHead.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "qx"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        jzdj.setText(data.getVal("ej_jzdj"));
        cc.setText(data.getVal("pbj_cc"));
        db.setText(data.getVal("ej_db"));
        jzbh.setText(data.getVal("ej_BrickID"));
        station.setText(data.getVal("pb_gw"));
        llcps.setText(data.getVal("cp_llcps"));
        EditTextUtil.setTextEnd(sjcps, data.getRelVal("cp", "qx", "sjcps"));
        EditTextUtil.setTextEnd(hs, data.getRelVal("cp", "qx", "hs"));

        //String pieces = sysConfig.getPieces();
        //EditTextUtil.setTextEnd(mhps, pieces);
        EditTextUtil.setTextEnd(mhps, data.getRelVal("cp", "qx", "mhps"));

        EditTextUtil.setTextEnd(ps, data.getRelVal("cp", "qx", "ps"));
        EditTextUtil.setTextEnd(bb, data.getRelVal("cp", "qx", "bb"));
        EditTextUtil.setTextEnd(yp, data.getRelVal("cp", "qx", "yp"));
        EditTextUtil.setTextEnd(jjqxs, data.getRelVal("cp", "qx", "tjqxs"));
        EditTextUtil.setTextEnd(sbqxs, data.getVal("qx_qxsbs"));
        EditTextUtil.setTextEnd(qt, data.getVal("qx_qt"));

        xwcd = data.getVal("pb_xwcd");
        yxbc = data.getVal("ej_yxbc");
        cj = data.getVal("qp_cj");
        qps = data.getVal("qgs_qps");

        qpsV.setText(qps);

        calSjps();
        //calLlcps();
        //calTJqxs();
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    public void setPbjValue(QxProduct qx) {
        jzbhHead.setText(qx.getBrickId());
        jzbh.setText(qx.getBrickId());
        station.setText(qx.getStation());
        sjcps.setText(qx.getSjcps());
        hs.setText(qx.getHs());
        ps.setText(qx.getPs());
        bb.setText(qx.getBb());
        yp.setText(qx.getYp());
        jjqxs.setText(qx.getJjqxs());
        sbqxs.setText(qx.getSbqxs());
        zqqss.setText(qx.getZqqss());
        zj.setText(qx.getZj());
        qt.setText(qx.getQt());
    }

    @Override
    public void submitOk() {
        doAfterSumbit(jzbhHead.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(jzbhHead.getText().toString(), false);
    }

    public QxProduct createQx() {
        QxProduct qx = new QxProduct();
        qx.setBrickId(jzbh.getText().toString());
        qx.setStation(station.getText().toString());
        qx.setSjcps(sjcps.getText().toString());
        qx.setHs(hs.getText().toString());
        qx.setPs(ps.getText().toString());
        qx.setBb(bb.getText().toString());
        qx.setYp(yp.getText().toString());
        qx.setJjqxs(jjqxs.getText().toString());
        qx.setSbqxs(sbqxs.getText().toString());
        qx.setQt(qt.getText().toString());
        qx.setZj(zj.getText().toString());
        qx.setZqqss(zqqss.getText().toString());
        return qx;
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        jzbhHead.setText(R.string.wait_scan);
        jzbh.setText("");
        cc.setText("");
        db.setText("");
        station.setText("");
        llcps.setText("");
        sjcps.setText("");
        hs.setText("");
        mhps.setText("");
        ps.setText("");
        bb.setText("");
        yp.setText("");
        jjqxs.setText("");
        sbqxs.setText("");
        zj.setText("");
        qt.setText("");
        zqqss.setText("");
    }

    @Override
    public void serverError(Throwable e) {

    }

    /***
     * 计算总计数
     */
    @OnTextChanged(value = {R.id.qx_v_ps, R.id.qx_v_bb, R.id.qx_v_yp, R.id.qx_v_jjqxs, R.id.qx_v_sbqxs, R.id.qx_v_qt}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calZj() {
        //int psI = DataUtil.getIntValue(ps.getText().toString());
        int bbI = DataUtil.getIntValue(bb.getText().toString());
        int ypV = DataUtil.getIntValue(yp.getText().toString());
        int jjqxsV = DataUtil.getIntValue(jjqxs.getText().toString());
        int sbqxsV = DataUtil.getIntValue(sbqxs.getText().toString());
        int qtV = DataUtil.getIntValue(qt.getText().toString());
        int zjV = new BigDecimal(bbI).add(new BigDecimal(ypV)).add(new BigDecimal(jjqxsV)).add(new BigDecimal(sbqxsV)).add(new BigDecimal(qtV)).intValue();
        zj.setText((zjV + ""));
    }

    /***
     * 计算实际出片数
     */
    @OnTextChanged(value = {R.id.qx_v_hs, R.id.qx_v_mhps, R.id.qx_v_ps, R.id.qx_v_bb, R.id.qx_v_yp}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calSjps() {
        int hsI = DataUtil.getIntValue(hs.getText().toString());
        int mhpsI = DataUtil.getIntValue(mhps.getText().toString());
        int psI = DataUtil.getIntValue(ps.getText().toString());
        int bbI = DataUtil.getIntValue(bb.getText().toString());
        int ypI = DataUtil.getIntValue(yp.getText().toString());
        int sjpsI = new BigDecimal(hsI).multiply(new BigDecimal(mhpsI)).add(new BigDecimal(psI)).add(new BigDecimal(bbI)).add(new BigDecimal(ypI)).intValue();
        sjcps.setText((sjpsI + ""));

        //calTJqxs();
    }

    /***
     * 计算理论出片数
     */
    /*
    @OnTextChanged(value = {R.id.qx_v_yp}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calLlcps() {

        double yxbcI = DataUtil.getDoubleValue(yxbc);
        double xwcdI = DataUtil.getDoubleValue(xwcd);
        double cjI = DataUtil.getDoubleValueNotZero(cj);
        int ypI = DataUtil.getIntValue(yp.getText().toString());
        int llcpsI = new BigDecimal(yxbcI).subtract(new BigDecimal(xwcdI)).divide(new BigDecimal(cjI), 0, BigDecimal.ROUND_DOWN).subtract(new BigDecimal(ypI)).intValue();
        llcps.setText((llcpsI + ""));

        calTJqxs();
    }*/

    /***
     * 计算脱胶清洗碎
     */
    /*
    public void calTJqxs() {
        int llcpsI = DataUtil.getIntValue(llcps.getText().toString());
        int sjcpsI = DataUtil.getIntValue(sjcps.getText().toString());
        int offset = DataUtil.getIntValue(sysConfig.getOffset());
        int qpsI = DataUtil.getIntValue(qps);

        int tjqxsI = new BigDecimal(llcpsI).add(new BigDecimal(offset)).subtract(new BigDecimal(sjcpsI)).subtract(new BigDecimal(qpsI)).intValue();
        jjqxs.setText((tjqxsI + ""));
    }*/
    @Override
    public void preventSubmit() {
        bSubmit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        bSubmit.setVisibility(View.VISIBLE);
    }
}
