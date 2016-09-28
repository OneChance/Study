package com.logic.mes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.fragment.FxFragment;
import com.logic.mes.fragment.PbFragment;
import com.logic.mes.fragment.TjFragment;
import com.logic.mes.fragment.ZbFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private static FragmentManager fragmentManager;
    private static ZbFragment zbFragment;
    private static PbFragment pbFragment;
    private static TjFragment tjFragment;
    private static FxFragment fxFragment;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        fragmentManager = getSupportFragmentManager();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        Intent loginDataIntent = getIntent();
        String identity = loginDataIntent.getStringExtra("identity");
        TextView loginUser = (TextView)findViewById(R.id.loginUser);
        loginUser.setText(identity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<BaseTagFragment> getAuthTab() {
        zbFragment = new ZbFragment();
        pbFragment = new PbFragment();
        tjFragment = new TjFragment();
        fxFragment = new FxFragment();
        List<BaseTagFragment> tabs = new ArrayList<>();
        tabs.add(pbFragment);
        tabs.add(zbFragment);
        tabs.add(tjFragment);
        tabs.add(fxFragment);
        return tabs;
    }

    private void setupViewPager(ViewPager viewPager) {

        //根据权限设置Tab
        List<BaseTagFragment> tabs = getAuthTab();
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);

        for (BaseTagFragment tab : tabs) {
            adapter.addFrag(tab, getResources().getText(tab.tabNameId).toString());
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
