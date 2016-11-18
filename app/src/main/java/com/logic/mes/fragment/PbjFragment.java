package com.logic.mes.fragment;

import android.content.Context;
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
import com.logic.mes.entity.process.PbjProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.pbj;

public class PbjFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public PbjFragment() {
        this.tagNameId = R.string.pbj_tab_name;
    }

    @InjectView(R.id.pbj_save)
    Button save;
    @InjectView(R.id.pbj_b_quali)
    Button bQuali;
    @InjectView(R.id.pbj_b_unquali)
    Button bUnquali;
    @InjectView(R.id.pbj_brick_id)
    TextView brickId;
    @InjectView(R.id.pbj_v_code)
    TextView codeValue;
    @InjectView(R.id.pbj_v_bb)
    EditText bbValue;
    @InjectView(R.id.pbj_v_zcbc)
    EditText zcbcValue;
    @InjectView(R.id.pbj_v_zdbc)
    EditText zdbcValue;
    @InjectView(R.id.pbj_v_yxbc)
    EditText yxbcValue;
    @InjectView(R.id.pbj_v_size)
    EditText sizeValue;
    @InjectView(R.id.pbj_v_dj)
    EditText djValue;

    FragmentActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    Context context;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(pbj, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;
        context = getActivity();

        serverObserver = new ServerObserver(this, "pbj", activity);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeValue != null && !codeValue.equals("")) {
                    //先清空表
                    DBHelper.getInstance(activity).delete(PbjProduct.class);
                    PbjProduct pbj = createBean("");
                    DBHelper.getInstance(activity).save(pbj);
                    MyApplication.toast("保存成功");
                }
            }
        });

        bQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProcessUtil(context).submit(submitResultReceiver, createBean("是"));
            }
        });

        bUnquali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new ProcessUtil(context).submit(submitResultReceiver, createBean("否"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        List<PbjProduct> plist = DBHelper.getInstance(activity).query(PbjProduct.class);
        if (plist.size() > 0) {
            PbjProduct pbj = plist.get(0);
            brickId.setText(pbj.getBrickId());
            codeValue.setText(pbj.getBrickId());
            EditTextUtil.setTextEnd(bbValue, pbj.getBbValue());
            EditTextUtil.setTextEnd(zcbcValue, pbj.getZcbcValue());
            EditTextUtil.setTextEnd(zdbcValue, pbj.getZdbcValue());
            EditTextUtil.setTextEnd(yxbcValue, pbj.getYxbcValue());
            EditTextUtil.setTextEnd(sizeValue, pbj.getSizeValue());
            EditTextUtil.setTextEnd(djValue, pbj.getDjValue());
        }

        EditTextUtil.setNoKeyboard(bbValue);
        EditTextUtil.setNoKeyboard(zcbcValue);
        EditTextUtil.setNoKeyboard(zdbcValue);
        EditTextUtil.setNoKeyboard(yxbcValue);
        EditTextUtil.setNoKeyboard(sizeValue);
        EditTextUtil.setNoKeyboard(djValue);

        return view;
    }


    @Override
    public void scanReceive(String res, int scanCode) {
        brickId.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    @Override
    public void serverData(ServerResult res) {
        codeValue.setText(res.getVal("ej_BrickID"));
        EditTextUtil.setTextEnd(bbValue, res.getVal("pbj_bb"));
        EditTextUtil.setTextEnd(zcbcValue, res.getVal("ej_zccd"));
        EditTextUtil.setTextEnd(zdbcValue, res.getVal("ej_zdcd"));
        EditTextUtil.setTextEnd(yxbcValue, res.getVal("pbj_yxbc"));
        EditTextUtil.setTextEnd(sizeValue, res.getVal("ej_jzcc"));
        EditTextUtil.setTextEnd(djValue, res.getVal("pbj_dj"));
    }

    @Override
    public void clear() {
        brickId.setText(R.string.wait_scan);
        codeValue.setText("");
        bbValue.setText("");
        zcbcValue.setText("");
        zdbcValue.setText("");
        yxbcValue.setText("");
        sizeValue.setText("");
        djValue.setText("");
    }

    @Override
    public void serverError() {

    }

    @Override
    public void submitOk() {
        clear();
    }

    public PbjProduct createBean(String sfhg) {
        PbjProduct pbj = new PbjProduct();
        pbj.setCode("pbj");
        pbj.setBrickId(brickId.getText().toString());
        pbj.setCodeValue(codeValue.getText().toString());
        pbj.setBbValue(bbValue.getText().toString());
        pbj.setSizeValue(sizeValue.getText().toString());
        pbj.setYxbcValue(yxbcValue.getText().toString());
        pbj.setZcbcValue(zcbcValue.getText().toString());
        pbj.setZdbcValue(zdbcValue.getText().toString());
        pbj.setDjValue(djValue.getText().toString());
        pbj.setSfhg(sfhg);
        return pbj;
    }
}