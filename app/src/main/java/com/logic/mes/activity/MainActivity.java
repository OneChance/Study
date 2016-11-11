package com.logic.mes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.presenter.main.IMain;
import com.logic.mes.presenter.main.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements IMain{

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
    @InjectView(R.id.btn_login_out)
    TextView bLoginOut;
    MainPresenter mainPresenter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        ButterKnife.inject(this);
        context = this;
        activity = this;
        Bundle bundle=this.getIntent().getExtras();
        userInfo = (UserInfo)bundle.getSerializable("userInfo");
        loginUser.setText(userInfo.getUser().getEmpName());
        empNo.setText("["+userInfo.getUser().getEmpCode()+"]");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        mainPresenter = new MainPresenter(this);
        mainPresenter.getAuthTags(userInfo);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        bLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void setTags(List<BaseTagFragment> tags) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        for (BaseTagFragment tag : tags) {
            if(tag.tagNameId>0){
                adapter.addFrag(tag, getResources().getText(tag.tagNameId).toString());
            }
        }
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
