package com.logic.mes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.entity.process.PbjProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.pbj;

public class PbjFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public PbjFragment() {
        this.tagNameId = R.string.pbj_tab_name;
    }

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
    @InjectView(R.id.pbj_dj_group)
    RadioGroup djValue;

    IScanReceiver receiver;
    ServerObserver serverObserver;
    Context context;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    View view;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(pbj, container, false);

        ButterKnife.inject(this, view);

        views.add(brickId);
        views.add(codeValue);

        receiver = this;
        submitResultReceiver = this;

        serverObserver = new ServerObserver(this, "pbj", activity);

        bQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit("是");
            }
        });

        bUnquali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit("否");
            }
        });

        EditTextUtil.setNoKeyboard(bbValue);
        EditTextUtil.setNoKeyboard(zcbcValue);
        EditTextUtil.setNoKeyboard(zdbcValue);
        EditTextUtil.setNoKeyboard(yxbcValue);
        EditTextUtil.setNoKeyboard(sizeValue);

        return view;
    }

    public void submit(String quali) {
        try {

            PbjProduct bean = createBean(quali);

            if (bean != null && bean.getCodeValue() != null && !bean.getCodeValue().equals("")) {
                new ProcessUtil(context).submit(submitResultReceiver, bean, userInfo.getUser());
            } else {
                MyApplication.toast(R.string.data_need, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRatioGroup(String dj) {
        if (dj.equals(MyApplication.getResString(R.string.pbj_dj_yes))) {
            djValue.check(R.id.dj_yes);
        } else {
            djValue.check(R.id.dj_no);
        }
    }


    @Override
    public void scanReceive(String res, int scanCode) {
        brickId.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "pbj"))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void serverData() {
        codeValue.setText(data.getVal("ej_BrickID"));
        EditTextUtil.setTextEnd(bbValue, data.getRelVal("ej", "pbj", "bb"));
        EditTextUtil.setTextEnd(zcbcValue, data.getVal("ej_zccd"));
        EditTextUtil.setTextEnd(zdbcValue, data.getVal("ej_zdcd"));
        EditTextUtil.setTextEnd(yxbcValue, data.getRelVal("ej", "pbj", "yxbc"));
        EditTextUtil.setTextEnd(sizeValue, data.getVal("ej_jzcc"));
        setRatioGroup(data.getVal("pbj_dj"));
    }

    @Override
    public void setData(ServerResult res) {
        this.data = res;
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
        djValue.clearCheck();
    }

    @Override
    public void serverError(Throwable e) {

    }

    @Override
    public void submitOk() {
        doAfterSumbit(brickId.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(brickId.getText().toString(), false);
    }

    public PbjProduct createBean(String sfhg) {
        PbjProduct pbj = new PbjProduct();
        pbj.setCode("pbj");
        pbj.setBrickId(brickId.getText().toString());
        pbj.setCodeValue(brickId.getText().toString());
        pbj.setBbValue(bbValue.getText().toString());
        pbj.setSizeValue(sizeValue.getText().toString());
        pbj.setYxbcValue(yxbcValue.getText().toString());
        pbj.setZcbcValue(zcbcValue.getText().toString());
        pbj.setZdbcValue(zdbcValue.getText().toString());

        RadioButton rb = (RadioButton) (view.findViewById(djValue.getCheckedRadioButtonId()));
        String dj = "";

        if (rb != null) {
            dj = rb.getText().toString();
        } else {
            return null;
        }

        pbj.setDjValue(dj);
        pbj.setSfhg(sfhg);
        return pbj;
    }

    @Override
    public void preventSubmit() {
        bQuali.setVisibility(View.INVISIBLE);
        bUnquali.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        bQuali.setVisibility(View.VISIBLE);
        bUnquali.setVisibility(View.VISIBLE);
    }
}
