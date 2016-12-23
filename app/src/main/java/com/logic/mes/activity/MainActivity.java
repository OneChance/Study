package com.logic.mes.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.observer.ServerObserver;
import com.logic.mes.presenter.main.IMain;
import com.logic.mes.presenter.main.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements IMain {

    Activity activity;
    UserInfo userInfo;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_viewpager)
    ViewPager viewPager;
    @InjectView(R.id.main_tabs)
    TabLayout tabLayout;
    @InjectView(R.id.login_user)
    TextView loginUser;
    @InjectView(R.id.emp_no)
    TextView empNo;
    @InjectView(R.id.net_state)
    TextView netState;
    @InjectView(R.id.btn_login_out)
    TextView bLoginOut;
    @InjectView(R.id.submit_status)
    TextView submitStatus;
    MainPresenter mainPresenter;
    private Context context;
    ViewPagerAdapter adapter;

    private BroadcastReceiver serverAccessReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainpage);
        ButterKnife.inject(this);
        context = this;
        activity = this;
        Bundle bundle = this.getIntent().getExtras();
        userInfo = (UserInfo) bundle.getSerializable("userInfo");
        assert userInfo != null;
        loginUser.setText(userInfo.getUser().getEmpName());
        empNo.setText("[" + userInfo.getUser().getEmpCode() + "]");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager);
        mainPresenter = new MainPresenter(this, context);
        mainPresenter.getAuthTags(userInfo);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.getItem(position).setReceiver();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (adapter.getCount() > 0) {
            adapter.getItem(0).setReceiver();
        }

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        bLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        //注册网络监听
        serverAccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if (MyApplication.netAble && intent.getAction().equals(ServerObserver.SERVER_ERROR)) {
                    //网络访问失败
                    MyApplication.netAble = false;
                    setNetableStyle(false);
                } else {
                    if (!MyApplication.netAble && intent.getAction().equals(ServerObserver.SERVER_OK)) {
                        MyApplication.netAble = true;
                        setNetableStyle(true);
                    }
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ServerObserver.SERVER_ERROR);
        mFilter.addAction(ServerObserver.SERVER_OK);
        registerReceiver(serverAccessReceiver, mFilter);

        setNetableStyle(MyApplication.netAble);

        MyApplication.addActivity(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setNetableStyle(boolean netable) {
        if (netable) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mainPresenter.autoSubmitData();
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNoNet));
            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNoNet));
        }
    }

    @Override
    public void setTags(List<BaseTagFragment> tags) {
        for (BaseTagFragment tag : tags) {
            if (tag.tagNameId > 0) {
                adapter.addFrag(tag, getResources().getText(tag.tagNameId).toString());
            }
        }

        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<BaseTagFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public BaseTagFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(BaseTagFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(serverAccessReceiver);
        mainPresenter.stopSubmitData();
        super.onDestroy();
    }

    public void setStatus(String text, boolean success) {
        if (success) {
            submitStatus.setTextColor(getResources().getColor(R.color.success));
        } else {
            submitStatus.setTextColor(getResources().getColor(R.color.error));
        }
        submitStatus.setText(text);
    }
}
