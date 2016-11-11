package com.logic.mes.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.net.NetConfig;
import com.logic.mes.net.NetUtil;
import com.logic.mes.net.ServerConfig;
import com.logic.mes.observer.LoginObserver;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements IScanReceiver, LoginObserver.IUpdate {

    private Context context;
    @InjectView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
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


    private Gson gson;
    private GsonBuilder builder;

    @Override
    protected void onResume() {
        loginAble();
        loading.setVisibility(View.INVISIBLE);
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

        if (NetConfig.IP.equals("") || NetConfig.PORT.equals("")) {
            List<ServerConfig> dataList = DBHelper.getInstance(activity).query(ServerConfig.class);
            if (dataList.size() > 0) {
                NetConfig.IP = dataList.get(0).getIp();
                NetConfig.PORT = dataList.get(0).getPort();
            }
        }

        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //扫描登陆
                loginDisable();
                MyApplication.getScanUtil().send(receiver, 0);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入登陆
                if (empNo.getText() == null || empNo.getText().toString().equals("")) {
                    MyApplication.toast(R.string.need_emp_no);
                } else {
                    loginDisable();
                    receive(empNo.getText().toString(), 0);
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

                ip.setText(NetConfig.IP);
                port.setText(NetConfig.PORT);

                dialog.setPositiveButton(R.string.config_save, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServerConfig serverConfig = new ServerConfig(ip.getText().toString(), port.getText().toString());
                        NetConfig.IP = ip.getText().toString();
                        NetConfig.PORT = port.getText().toString();
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

        builder = new GsonBuilder();
        gson = builder.create();

        EditTextUtil.setNoKeyboard(empNo);

        MyApplication.addActivity(this);
    }

    public void loginAble() {
        scanBarCodeButton.setClickable(true);
        bLogin.setClickable(true);
    }

    public void loginDisable() {
        scanBarCodeButton.setClickable(false);
        bLogin.setClickable(false);
    }


    @Override
    public void receive(String scanResult, int scanCode) {
        loading.setVisibility(View.VISIBLE);
        LoginObserver.currentInputCode = scanResult;
        NetUtil.SetObserverCommonAction(NetUtil.getServices(true).Login(scanResult))
                .subscribe(loginObserver);
    }

    @Override
    public void error() {
        loginButtonRecover();
    }

    @Override
    public void updateStart() {
        loginAble();
        loadingText.setText("正在下载更新程序......");
    }

    @Override
    public void loginButtonRecover() {
        loginAble();
        loading.setVisibility(View.INVISIBLE);
    }
}
