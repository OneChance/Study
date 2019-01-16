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
import com.logic.mes.entity.process.FgqxProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.fgqx;

public class FgqxFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public FgqxFragment() {
        this.tagNameId = R.string.fgqx_tab_name;
    }

    @BindView(R.id.fgqx_v_ph)
    TextView ph;
    @BindView(R.id.fgqx_b_submit)
    Button bSubmit;
    @BindView(R.id.fgqx_b_clear)
    Button bClear;

    @BindView(R.id.fgqx_v_cpzps)
    TextView cpzps;
    @BindView(R.id.fgqx_v_qxzps)
    TextView qxzps;
    @BindView(R.id.fgqx_v_qxs)
    EditText qxs;
    @BindView(R.id.fgqx_v_qxsbs)
    EditText qxsbs;
    @BindView(R.id.fgqx_v_qxzsp)
    TextView qxzsp;

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

        View view = inflater.inflate(fgqx, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "fgqx", activity);
        submitResultReceiver = this;

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ph.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.batch_scan_first, false);
                } else {
                    if(qxzps.getText().toString().contains("-")||
                       qxs.getText().toString().contains("-")||
                       qxsbs.getText().toString().contains("-")){
                        MyApplication.toast(R.string.data_error, false);
                    }else{
                        FgqxProduct fgqx = createFgqx();
                        fgqx.setCode("fgqx");
                        new ProcessUtil(activity).submit(submitResultReceiver, fgqx, userInfo.getUser());
                    }
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        EditTextUtil.setNoKeyboard(qxs);
        EditTextUtil.setNoKeyboard(qxsbs);


        UserInfo userInfo = ((MainActivity) getActivity()).getUserInfo();
        sysConfig = userInfo.getSysConfig();

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        ph.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getFanGongInfo(res,"fgqx"))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData() {
        cpzps.setText(data.getVal("fgcp_sjcps"));
        //qxzps.setText(data.getVal("fgqx_qxzps"));
        EditTextUtil.setTextEnd(qxs, data.getVal("fgqx_qxs"));
        EditTextUtil.setTextEnd(qxsbs, data.getVal("fgqx_qxsbs"));
        //qxzsp.setText(data.getVal("fgqx_qxzsp"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void submitOk() {
        doAfterSumbit(ph.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(ph.getText().toString(), false);
    }

    public FgqxProduct createFgqx() {
        FgqxProduct fgqx = new FgqxProduct();
        fgqx.setPh(ph.getText().toString());
        fgqx.setCpzps(cpzps.getText().toString());
        fgqx.setQxzps(qxzps.getText().toString());
        fgqx.setQxs(qxs.getText().toString());
        fgqx.setQxsbs(qxsbs.getText().toString());
        fgqx.setQxzsp(qxzsp.getText().toString());
        return fgqx;
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        ph.setText(R.string.wait_scan);
        cpzps.setText("");
        qxzps.setText("");
        qxs.setText("");
        qxsbs.setText("");
        qxzsp.setText("");
    }

    @Override
    public void serverError(Throwable e) {

    }

    /***
     * 计算清洗总片数
     */
    @OnTextChanged(value = {R.id.fgqx_v_cpzps, R.id.fgqx_v_qxzsp}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calQxzps() {
        //插片总片数
        int cpzpsV = DataUtil.getIntValue(cpzps.getText().toString());
        //清洗总碎片
        int qxzspV = DataUtil.getIntValue(qxzsp.getText().toString());
        //清洗总片数
        int qxzpsV = new BigDecimal(cpzpsV).subtract(new BigDecimal(qxzspV)).intValue();
        qxzps.setText((qxzpsV + ""));
    }

    /***
     * 计算清洗总碎片
     */
    @OnTextChanged(value = {R.id.fgqx_v_qxs, R.id.fgqx_v_qxsbs}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calSjps() {
        //清洗碎
        int qxsV = DataUtil.getIntValue(qxs.getText().toString());
        //清洗设备碎
        int qxsbsV = DataUtil.getIntValue(qxsbs.getText().toString());
        //清洗总碎片
        int qxzspV = new BigDecimal(qxsV).add(new BigDecimal(qxsbsV)).intValue();
        qxzsp.setText((qxzspV + ""));
        //calQxzps();
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
