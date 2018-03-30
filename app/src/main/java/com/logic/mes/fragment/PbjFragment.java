package com.logic.mes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.logic.mes.DataUtil;
import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.entity.base.PbjYuanYin;
import com.logic.mes.entity.process.PbjProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.logic.mes.R.layout.pbj;

public class PbjFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public PbjFragment() {
        this.tagNameId = R.string.pbj_tab_name;
    }

    @BindView(R.id.pbj_b_quali)
    Button bQuali;
    @BindView(R.id.pbj_b_unquali)
    Button bUnquali;
    @BindView(R.id.pbj_brick_id)
    TextView brickId;
    @BindView(R.id.pbj_v_kjcd)
    EditText kjcd;
    @BindView(R.id.pbj_v_gghyxcd)
    TextView gghyxcd;
    @BindView(R.id.pbj_v_ggyy)
    Spinner ggyy;
    @BindView(R.id.pbj_v_yxbc)
    TextView yxbcValue;
    @BindView(R.id.pbj_v_size)
    EditText sizeValue;
    @BindView(R.id.pbj_dj_group)
    RadioGroup djValue;

    IScanReceiver receiver;
    ServerObserver serverObserver;
    Context context;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    View view;

    String yy = MyApplication.getResString(R.string.pbj_yy_need);

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(pbj, container, false);

        ButterKnife.bind(this, view);

        receiver = this;
        submitResultReceiver = this;

        serverObserver = new ServerObserver(this, "pbj", activity);

        bQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(MyApplication.getResString(R.string.yes));
            }
        });

        bUnquali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(MyApplication.getResString(R.string.no));
            }
        });

        EditTextUtil.setNoKeyboard(sizeValue);

        final List<String> reasons = new ArrayList<>();

        List<PbjYuanYin> infos = userInfo.getPbjYuanYin();

        reasons.add(MyApplication.getResString(R.string.pbj_yy_need));

        for (PbjYuanYin info : infos) {
            reasons.add(info.getItemKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ggyy.setAdapter(adapter);

        ggyy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                yy = reasons.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    public void submit(String quali) {
        try {

            PbjProduct bean = createBean(quali);

            if (bean != null && bean.getBrickId() != null && !bean.getBrickId().equals("") && !bean.getBrickId().equals(MyApplication.getResString(R.string.wait_scan))) {
                if (bean.getKjcd()!=null && !bean.getKjcd().equals("") && yy.equals(MyApplication.getResString(R.string.pbj_yy_need))) {
                    MyApplication.toast(R.string.pbj_yy_need, false);
                } else {
                    new ProcessUtil(context).submit(submitResultReceiver, bean, userInfo.getUser());
                }
            } else {
                MyApplication.toast(R.string.form_required, false);
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
        EditTextUtil.setTextEnd(kjcd, "");
        EditTextUtil.setTextEnd(sizeValue, data.getVal("pbj_cc"));
        yxbcValue.setText(data.getRelVal("ej", "pbj", "yxbc"));
        gghyxcd.setText(data.getRelVal("ej", "pbj", "yxbc"));
        setRatioGroup(data.getVal("pbj_dj"));
    }

    @Override
    public void setData(ServerResult res) {
        this.data = res;
    }

    @Override
    public void clear() {
        brickId.setText(R.string.wait_scan);
        kjcd.setText("");
        gghyxcd.setText("");
        ggyy.setSelection(0);
        yy = MyApplication.getResString(R.string.pbj_yy_need);
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
        pbj.setSizeValue(sizeValue.getText().toString());
        pbj.setYxbcValue(gghyxcd.getText().toString());
        pbj.setKjcd(kjcd.getText().toString());

        if(pbj.getKjcd().equals("")){
            pbj.setYy("");
        }else{
            pbj.setYy(yy);
        }

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

    /***
     * 计算更改后有效长度
     */
    @OnTextChanged(value = {R.id.pbj_v_kjcd}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void calSjps() {
        //扣减长度
        double kjcdV = DataUtil.getDoubleValue(kjcd.getText().toString());
        //原有效长度
        double yxbcV = DataUtil.getDoubleValue(yxbcValue.getText().toString());
        //清洗总片数
        double gghyxcdV = new BigDecimal(yxbcV).subtract(new BigDecimal(kjcdV)).doubleValue();
        gghyxcd.setText((gghyxcdV + ""));
    }
}
