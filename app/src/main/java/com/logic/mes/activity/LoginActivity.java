package com.logic.mes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.LocalConfig;
import com.logic.mes.net.NetUtil;
import com.logic.mes.ServerConfig;
import com.logic.mes.observer.LoginObserver;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements IScanReceiver, LoginObserver.IUpdate {

    private Context context;
    @InjectView(R.id.btn_login)
    Button bLogin;
    @InjectView(R.id.emp_no)
    EditText empNo;
    IScanReceiver receiver;
    LoginObserver loginObserver;
    @InjectView(R.id.loading)
    LinearLayout loading;
    @InjectView(R.id.loading_text)
    TextView loadingText;
    @InjectView(R.id.btn_config_server)
    Button config;
    LoginActivity activity;

    @Override
    protected void onResume() {
        loginAble();
        loading.setVisibility(View.INVISIBLE);
        MyApplication.getScanUtil().setReceiver(receiver, 0);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        receiver = this;
        activity = this;
        loginObserver = new LoginObserver(context, this);

        ButterKnife.inject(this);

        if (LocalConfig.IP.equals("") || LocalConfig.PORT.equals("")) {
            List<ServerConfig> dataList = DBHelper.getInstance(activity).query(ServerConfig.class);
            if (dataList.size() > 0) {
                LocalConfig.IP = dataList.get(0).getIp();
                LocalConfig.PORT = dataList.get(0).getPort();
                LocalConfig.MACHINE_CODE = dataList.get(0).getMachineCode();
            }
        }

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入登陆
                if (empNo.getText() == null || empNo.getText().toString().equals("")) {
                    MyApplication.toast(R.string.need_emp_no, false);
                } else {
                    loginDisable();
                    scanReceive(empNo.getText().toString(), 0);
                }
            }
        });

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog(context);
                View view = View.inflate(context, R.layout.config_server, null);
                dialog.setTitle(R.string.server_config);
                dialog.setContentView(view);
                final EditText ip = (EditText) view.findViewById(R.id.server_ip);
                final EditText port = (EditText) view.findViewById(R.id.server_port);
                final EditText machineCode = (EditText) view.findViewById(R.id.machine_code);

                ip.setText(LocalConfig.IP);
                port.setText(LocalConfig.PORT);
                machineCode.setText(LocalConfig.MACHINE_CODE);

                dialog.setPositiveButton(R.string.config_save, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServerConfig serverConfig = new ServerConfig(ip.getText().toString(), port.getText().toString(), machineCode.getText().toString());
                        LocalConfig.IP = ip.getText().toString();
                        LocalConfig.PORT = port.getText().toString();
                        LocalConfig.MACHINE_CODE = machineCode.getText().toString();
                        DBHelper.getInstance(activity).delete(ServerConfig.class);
                        DBHelper.getInstance(activity).save(serverConfig);
                        dialog.dismiss(view);
                    }
                });

                dialog.setNegativeButton(R.string.config_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(view);
                    }
                });

                dialog.show();
            }
        });

        MyApplication.getScanUtil().setReceiver(receiver, 0);
        EditTextUtil.setNoKeyboard(empNo);

        //禁用HOME键
        Intent intent = new Intent("com.android.action.HOMEKEY_SWITCH_STATE");
        intent.putExtra("enable", false);
        context.sendBroadcast(intent);

        MyApplication.addActivity(this);
    }

    public void loginAble() {
        bLogin.setClickable(true);
    }

    public void loginDisable() {
        bLogin.setClickable(false);
    }

    @Override
    public void scanReceive(String scanResult, int scanCode) {
        loading.setVisibility(View.VISIBLE);
        LoginObserver.currentInputCode = scanResult;
        NetUtil.SetObserverCommonAction(NetUtil.getServices(true).Login(scanResult))
                .subscribe(loginObserver);
        loadingText.setText(R.string.logining);
    }

    @Override
    public void scanError() {
        loginButtonRecover();
    }

    @Override
    public void updateStart() {
        loginAble();
        loadingText.setText(R.string.app_downloading);
    }

    @Override
    public void loginButtonRecover() {
        loginAble();
        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        //不允许退出程序
        //MyApplication.exit();
    }
}
