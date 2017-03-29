package com.logic.mes.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.TimeUtil;
import com.logic.mes.entity.process.Wx;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.View.GONE;
import static com.logic.mes.R.layout.wx;

public class WxFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver {

    public final int SCAN_CODE_OPER = 0;
    public final int SCAN_CODE_EQUIP = 1;
    public final int NOTE_SUBMIT = 2;
    public final int CONFIRM_SUBMIT = 3;
    public final int GET_BAOYANG = 4;

    int currentReceiverCode = SCAN_CODE_EQUIP;

    public WxFragment() {
        this.tagNameId = R.string.wx_tab_name;
    }

    @InjectView(R.id.oper_code_value)
    TextView operCode;
    @InjectView(R.id.equip_code_value)
    TextView equipCode;
    @InjectView(R.id.oper_name_value)
    TextView operName;
    @InjectView(R.id.equip_name_value)
    TextView equipName;
    @InjectView(R.id.scan_oper)
    Button scanOper;
    @InjectView(R.id.scan_equip)
    TextView scanEquip;

    /*通知维修*/
    @InjectView(R.id.note_maintain)
    LinearLayout noteMaintain;
    @InjectView(R.id.equip_stop_date_value)
    Button equipStopDate;
    @InjectView(R.id.equip_stop_time_value)
    Button equipStopTime;
    @InjectView(R.id.btn_note_maintain)
    Button noteMaintainBtn;

    /*确认维修*/
    @InjectView(R.id.confirm_maintain)
    LinearLayout confirmMaintain;
    @InjectView(R.id.equip_stop_datetime_value)
    TextView stopDatetime;
    @InjectView(R.id.note_datetime_value)
    TextView noteDatetime;
    @InjectView(R.id.accept_datetime_value)
    TextView acceptDatetime;
    @InjectView(R.id.complete_datetime_value)
    TextView completeDatetime;
    @InjectView(R.id.maintain_oper_value)
    TextView maintainOper;
    @InjectView(R.id.confirm_date_value)
    Button confirmDate;
    @InjectView(R.id.confirm_time_value)
    Button confirmTime;
    @InjectView(R.id.confirm_agree_value)
    RadioGroup confirmAgree;
    @InjectView(R.id.btn_confirm_maintain)
    Button confirmMaintainBtn;

    IScanReceiver receiver;
    ServerObserver serverObserver;
    View view;
    Wx wxEntity;
    Calendar c;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_EQUIP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(wx, container, false);

        ButterKnife.inject(this, view);

        if (wxEntity == null) {
            wxEntity = new Wx();
        }

        if (c == null) {
            c = Calendar.getInstance();
        }

        serverObserver = new ServerObserver(this, "by", activity);

        operCode.setText(userInfo.getUser().getEmpCode());
        operName.setText(userInfo.getUser().getEmpName());

        scanOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operCode.setText(R.string.wait_scan);
                operName.setText("");
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_OPER);
            }
        });

        scanEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equipCode.setText(R.string.wait_scan);
                equipName.setText("");
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_EQUIP);
            }
        });

        equipStopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        equipStopDate.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        equipStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        equipStopTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", min));
                    }
                }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true).show();
            }
        });

        noteMaintainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {

                if (operName.getText().equals("")) {
                    MyApplication.toast(R.string.need_scan_oper, false);
                } else if (equipName.getText().equals("")) {
                    MyApplication.toast(R.string.need_scan_equip, false);
                } else if (equipStopDate.getText().equals("")) {
                    MyApplication.toast(R.string.need_choose_stop_date, false);
                } else if (equipStopTime.getText().equals("")) {
                    MyApplication.toast(R.string.need_choose_stop_time, false);
                } else {
                    wxEntity.setEquipmentCode(equipCode.getText().toString());
                    wxEntity.setEquipmentName(equipName.getText().toString());
                    wxEntity.setStartCode(operCode.getText().toString());
                    wxEntity.setStartName(operName.getText().toString());
                    wxEntity.setProcessStep("开始");
                    wxEntity.setStartTime("");
                    wxEntity.setStopTime(equipStopDate.getText() + " " + equipStopTime.getText() + ":00");
                    wxEntity.setCode("wx");
                    currentReceiverCode = NOTE_SUBMIT;
                    NetUtil.SetObserverCommonAction(NetUtil.getServices(false).weiXiuStart(wxEntity))
                            .subscribe(serverObserver);
                }
            }
        });


        /*确认维修*/
        confirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        confirmDate.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        confirmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        confirmTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", min));
                    }
                }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true).show();
            }
        });

        confirmMaintainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {

                if (operName.getText().equals("")) {
                    MyApplication.toast(R.string.need_scan_oper, false);
                } else if (equipName.getText().equals("")) {
                    MyApplication.toast(R.string.need_scan_equip, false);
                } else if (confirmDate.getText().equals("")) {
                    MyApplication.toast(R.string.need_choose_confirm_date, false);
                } else if (confirmTime.getText().equals("")) {
                    MyApplication.toast(R.string.need_choose_confirm_time, false);
                } else {
                    RadioButton rb = (RadioButton) (view.findViewById(confirmAgree.getCheckedRadioButtonId()));
                    String agree = "";
                    if (rb != null) {
                        agree = rb.getText().toString();
                    }
                    if (agree.equals("")) {
                        MyApplication.toast(R.string.need_choose_agree, false);
                    } else {
                        wxEntity.setCode("wx");
                        wxEntity.setProcessStep("确认");
                        wxEntity.setConfirmCode(operCode.getText().toString());
                        wxEntity.setConfirmName(operName.getText().toString());
                        wxEntity.setConfirmResult(agree);
                        wxEntity.setConfirmFinishTime(confirmDate.getText() + " " + confirmTime.getText() + ":00");
                        currentReceiverCode = CONFIRM_SUBMIT;
                        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).weiXiuStart(wxEntity))
                                .subscribe(serverObserver);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        ProcessItem item = new ProcessItem();

        if (scanCode == SCAN_CODE_OPER) {
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getUser(res))
                    .subscribe(serverObserver);
            currentReceiverCode = SCAN_CODE_OPER;
        } else if (scanCode == SCAN_CODE_EQUIP) {
            equipCode.setText(res);
            item.setItemKey("Equipment");
            item.setItemValue(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                    .subscribe(serverObserver);
            currentReceiverCode = SCAN_CODE_EQUIP;
        }
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void serverData() {
        Log.d("mes", new Gson().toJson(data));
        if (currentReceiverCode == SCAN_CODE_OPER) {
            if (data != null && data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                operCode.setText(mapList.get(0).get("empcode"));
                operName.setText(mapList.get(0).get("empname"));
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_EQUIP);
            }
        } else if (currentReceiverCode == SCAN_CODE_EQUIP) {
            if (data.getCode().equals("0")) {
                if (data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                    List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                    equipName.setText(mapList.get(0).get("ename"));
                    wxEntity.setEquipmentId(Long.parseLong(mapList.get(0).get("id")));
                    ProcessItem item = new ProcessItem();
                    item.setItemKey("WeiXiu");
                    item.setItemValue(equipCode.getText().toString());
                    currentReceiverCode = GET_BAOYANG;
                    NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                            .subscribe(serverObserver);
                }
            }
        } else if (currentReceiverCode == GET_BAOYANG) {
            String now = TimeUtil.getNow();
            if (data.getCode().equals("2")) {
                noteMaintain.setVisibility(View.VISIBLE);
                confirmMaintain.setVisibility(GONE);
                if (data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                    List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                    equipStopDate.setText(now.split(" ")[0]);
                    equipStopTime.setText(now.split(" ")[1]);
                }
            } else if (data.getCode().equals("0")) {
                noteMaintain.setVisibility(GONE);
                confirmMaintain.setVisibility(View.VISIBLE);
                if (data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                    List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                    wxEntity.setId(Long.parseLong(mapList.get(0).get("id")));
                    stopDatetime.setText(mapList.get(0).get("stopTime"));
                    noteDatetime.setText(mapList.get(0).get("startTime"));
                    acceptDatetime.setText(mapList.get(0).get("acceptTime"));
                    completeDatetime.setText(mapList.get(0).get("finishTime"));
                    maintainOper.setText(mapList.get(0).get("acceptName"));
                    confirmDate.setText(now.split(" ")[0]);
                    confirmTime.setText(now.split(" ")[1]);
                }
            } else {
                noteMaintain.setVisibility(GONE);
                confirmMaintain.setVisibility(GONE);
            }
        } else if (currentReceiverCode == NOTE_SUBMIT || currentReceiverCode == CONFIRM_SUBMIT) {
            if (data.getCode().equals("0")) {
                doAfterSumbit(equipCode.getText().toString(), true);
            }
        }
    }

    @Override
    public void setData(ServerResult res) {
        this.data = res;
    }

    @Override
    public void clear() {
        equipCode.setText(R.string.wait_scan);
        equipName.setText("");
        noteMaintain.setVisibility(GONE);
        confirmMaintain.setVisibility(GONE);
    }

    @Override
    public void serverError(Throwable e) {
        doAfterSumbit(equipCode.getText().toString(), false);
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }
}
