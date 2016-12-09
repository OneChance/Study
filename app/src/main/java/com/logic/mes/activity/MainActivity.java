package com.logic.mes.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.presenter.main.IMain;
import com.logic.mes.presenter.main.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements IMain {

    private static FragmentManager fragmentManager;
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
    MainPresenter mainPresenter;
    private Context context;
    ViewPagerAdapter adapter;

    private BroadcastReceiver netStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainpage);
        ButterKnife.inject(this);
        context = this;
        activity = this;
        Bundle bundle = this.getIntent().getExtras();
        userInfo = (UserInfo) bundle.getSerializable("userInfo");
        loginUser.setText(userInfo.getUser().getEmpName());
        empNo.setText("[" + userInfo.getUser().getEmpCode() + "]");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager);
        mainPresenter = new MainPresenter(this);
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
        netStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();

                if (activeInfo == null) {
                    //网络断开
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNoNet));
                    MyApplication.netAble = false;
                    netState.setText(R.string.net_disable);
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNoNet));
                    mainPresenter.stopAutoSubmit(context);
                } else {
                    MyApplication.netAble = true;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    netState.setText("");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mainPresenter.autoSubmitData(context);
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netStateReceiver, mFilter);

        MyApplication.addActivity(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
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
        unregisterReceiver(netStateReceiver);
        super.onDestroy();
    }
}
