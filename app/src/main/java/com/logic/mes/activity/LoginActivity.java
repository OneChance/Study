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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.base.UserInfoResult;
import com.logic.mes.observer.LoginObserver;

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
        loginObserver = new LoginObserver(context, this);

        ButterKnife.inject(this);

        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启扫描
                loginDisable();
                //MyApplication.scanUtil.send(receiver, 0);
                receive("555", 0);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启扫描
                //MyApplication.scanUtil.send(receiver, 0);
                if (empNo.getText() == null || empNo.getText().toString().equals("")) {
                    MyApplication.toast(R.string.need_emp_no);
                } else {
                    loginDisable();
                    receive("555", 0);
                }
            }
        });

        builder = new GsonBuilder();
        gson = builder.create();
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
        //NetUtil.SetObserverCommonAction(NetUtil.getServices().Login(scanResult))
        //        .subscribe(loginObserver);
        try {

            UserInfo userInfo = gson.fromJson("{'code':0,'info':null,'datas':{'user':{'id':33287,'empCode':'00473','empName':'王成香','userName':'00473','sex':'2','password':'13952986413','mobile':'13952986413','weixin':null,'email':null,'tel':'','orgid_hr':'10116001','orgid_mes':null,'isDel':0,'isChanged':1},'classTime':{'id':9,'orgid':2,'class1':1,'start1':'20:10','end1':'20:05','class2':0,'start2':'20:00','end2':'19:55','class3':0,'start3':'19:50','end3':'19:45'},'brokenType':[{'id':1,'produceCode':'ej','dataName':'打碎','dataCode':'ds'},{'id':2,'produceCode':'ej','dataName':'摔碎','dataCode':'ss'}],'produceDef':[{'id':1,'producename':'二检','producecode':'ej','idx':1},{'id':2,'producename':'配棒检','producecode':'pbj','idx':2},{'id':3,'producename':'配棒','producecode':'pb','idx':3},{'id':4,'producename':'粘棒','producecode':'zb','idx':4},{'id':5,'producename':'切片','producecode':'qp','idx':5},{'id':6,'producename':'验棒','producecode':'yb','idx':6},{'id':7,'producename':'脱胶','producecode':'tj','idx':7},{'id':8,'producename':'插片','producecode':'cp','idx':8},{'id':9,'producename':'清洗','producecode':'qx','idx':9},{'id':10,'producename':'切割碎复核','producecode':'qgs','idx':10},{'id':11,'producename':'分选','producecode':'fx','idx':11},{'id':12,'producename':'中转','producecode':'zz','idx':12},{'id':13,'producename':'装盒','producecode':'zh','idx':13},{'id':14,'producename':'装箱','producecode':'zx','idx':14},{'id':15,'producename':'装托','producecode':'zt','idx':15},{'id':16,'producename':'入库','producecode':'rk','idx':16},{'id':17,'producename':'出库','producecode':'ck','idx':17}],'tableSet':[{'id':1,'typeCode':'722','dataSet':'1M,1中,2中,3中,4中,5中,6中,1F'},{'id':2,'typeCode':'722','dataSet':'1M,1中,2中,3中,4中,5中,1F'},{'id':3,'typeCode':'721','dataSet':'2M,2F,1M,1中,1F'}]}}", UserInfoResult.class).getDatas();

            DBHelper.getInstance(context).deleteAll(UserInfo.class);
            DBHelper.getInstance(context).save(userInfo);

            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userInfo", userInfo);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStart() {
        loginAble();
        loadingText.setText("正在下载更新程序......");
    }

    @Override
    public void loginButtonRecover() {
        loginAble();
    }
}
