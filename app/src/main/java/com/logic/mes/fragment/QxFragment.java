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
import com.logic.mes.entity.base.SysConfig;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.process.QxProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.qx_v_cszbyxcd)
    TextView cszbyxcd;
    @BindView(R.id.qx_v_sjcps)
    EditText sjcps;
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
    EditText qpsV;
    @BindView(R.id.qx_v_qxzps)
    TextView qxzps;


    String xwcd = "0";
    String yxbc = "0";
    String cj = "1";

    SysConfig sysConfig;

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

        View view = inflater.inflate(qx, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
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
        EditTextUtil.setNoKeyboard(jjqxs);
        EditTextUtil.setNoKeyboard(sbqxs);
        EditTextUtil.setNoKeyboard(zqqss);
        EditTextUtil.setNoKeyboard(qpsV);
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
        cszbyxcd.setText(data.getVal("cszbyxcd"));
        EditTextUtil.setTextEnd(sjcps, data.getRelVal("cp", "qx", "sjcps"));//获得插片总片数
        EditTextUtil.setTextEnd(jjqxs, data.getVal("qx_tjqxs"));
        EditTextUtil.setTextEnd(sbqxs, data.getVal("qx_qxsbs"));
        EditTextUtil.setTextEnd(qt, data.getVal("qx_qt"));
        xwcd = data.getVal("pb_xwcd");
        yxbc = data.getVal("ej_yxbc");
        cj = data.getVal("qp_cj");
        EditTextUtil.setTextEnd(qpsV, data.getRelVal("qgs", "qx", "qps"));
        EditTextUtil.setTextEnd(zqqss, data.getVal("qx_zqqss"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
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
        qx.setJjqxs(jjqxs.getText().toString());
        qx.setSbqxs(sbqxs.getText().toString());
        qx.setQt(qt.getText().toString());
        qx.setZj(zj.getText().toString());
        qx.setZqqss(zqqss.getText().toString());
        qx.setQxzps(qxzps.getText().toString());
        qx.setQps(qpsV.getText().toString());
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
        jzdj.setText("");
        cc.setText("");
        db.setText("");
        station.setText("");
        llcps.setText("");
        cszbyxcd.setText("");
        sjcps.setText("");
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
     * 计算清洗碎片合计数
     */
    @OnTextChanged(value = {R.id.qx_v_zqqss, R.id.qx_v_jjqxs, R.id.qx_v_sbqxs, R.id.qx_v_qt}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calZj() {
        //清洗碎
        int jjqxsV = DataUtil.getIntValue(jjqxs.getText().toString());
        //清洗设备碎
        int sbqxsV = DataUtil.getIntValue(sbqxs.getText().toString());
        //整齐缺损碎
        int zqqssV = DataUtil.getIntValue(zqqss.getText().toString());
        //其他
        int qtV = DataUtil.getIntValue(qt.getText().toString());
        //清洗碎片合计
        int qxsphjV = new BigDecimal(jjqxsV).add(new BigDecimal(sbqxsV)).add(new BigDecimal(zqqssV)).add(new BigDecimal(qtV)).intValue();
        zj.setText((qxsphjV + ""));
        //清洗碎片合计数变化,就要重新计算清洗总片数
        calSjps();
    }

    /***
     * 计算清洗总片数
     */
    @OnTextChanged(value = {R.id.qx_v_sjcps}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calSjps() {
        //清洗碎片合计
        int qxsphjV = DataUtil.getIntValue(zj.getText().toString());
        //插片总片数
        int cpzpsV = DataUtil.getIntValue(sjcps.getText().toString());
        //清洗总片数
        int qxzpsV = new BigDecimal(cpzpsV).subtract(new BigDecimal(qxsphjV)).intValue();
        qxzps.setText((qxzpsV + ""));
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
