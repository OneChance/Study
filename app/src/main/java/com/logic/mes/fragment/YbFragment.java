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
import com.logic.mes.entity.process.YbProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.yb;

public class YbFragment extends BaseTagFragment implements IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public YbFragment() {
        this.tagNameId = R.string.yb_tab_name;
    }

    @InjectView(R.id.yb_save)
    Button save;
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

    FragmentActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(yb, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "yb", activity);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    //先清空表
                    DBHelper.getInstance(activity).delete(YbProduct.class);
                    YbProduct yb = createYb();
                    DBHelper.getInstance(activity).save(yb);

                    MyApplication.toast(R.string.product_save_success);
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    YbProduct yb = createYb();
                    new ProcessUtil(activity).submit(submitResultReceiver, yb);
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        List<YbProduct> plist = DBHelper.getInstance(activity).query(YbProduct.class);
        if (plist.size() > 0) {
            YbProduct yb = plist.get(0);
            setPbjValue(yb);
        }

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
    public void serverData(ServerResult res) {
        EditTextUtil.setTextEnd(yzd, res.getVal("yb_yzd"));
        EditTextUtil.setTextEnd(hbp, res.getVal("yb_hbp"));
        EditTextUtil.setTextEnd(zb, res.getVal("yb_zb"));
        EditTextUtil.setTextEnd(dp, res.getVal("yb_dp"));
        EditTextUtil.setTextEnd(kxs, res.getVal("yb_kxs"));
        EditTextUtil.setTextEnd(lds, res.getVal("yb_lds"));
        EditTextUtil.setTextEnd(zqbb, res.getVal("yb_zqbb"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
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
        clear();
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
        yb.setCode("yb");
        return yb;
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
        DBHelper.getInstance(activity).delete(YbProduct.class);
    }

    @Override
    public void serverError() {

    }
}
