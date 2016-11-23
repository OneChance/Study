package com.logic.mes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
    @InjectView(R.id.pbj_dj_group)
    RadioGroup djValue;

    FragmentActivity activity;
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
                submit("是");
            }
        });

        bUnquali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit("否");
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

            setRatioGroup(pbj.getDjValue());
        }

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
                MyApplication.toast(R.string.data_need);
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
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
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
}
