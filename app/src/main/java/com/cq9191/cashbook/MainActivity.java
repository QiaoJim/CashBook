package com.cq9191.cashbook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.ThreeMainFragments.PersonFragment;
import com.cq9191.cashbook.ThreeMainFragments.ThingsFragment;
import com.cq9191.cashbook.ThreeMainFragments.TimeFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {

    //首页的 搜索、个人中心、添加 按钮
    private ImageButton bt_search;
    private ImageButton bt_home;
    private ImageButton bt_help;
    private ImageButton fab_add;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    CashDatabaseHelper helper;
    SQLiteDatabase db;

    private final int OUT = 0;
    private final int ON = 1;

    private List<Fragment> mFragments;
    private List<String> mTabTitles;

    private TimeFragment timeFragment;
    private PersonFragment personFragment;
    private ThingsFragment thingsFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private MyPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //创建数据库
        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        //获得SharedPreferences，读取数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mTabLayout = (TabLayout) findViewById(R.id.my_tab);
        mViewPager = (ViewPager) findViewById(R.id.my_viewpager);

        //加载 tab和对应的 fragment
        initList();

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabTitles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabTitles.get(2)));

        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTabTitles);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);

        //按钮监听
        buttonClick();


    }

    private void initList() {
        mFragments = new ArrayList<>();
        mTabTitles = new ArrayList<>();

        timeFragment = new TimeFragment();
        personFragment = new PersonFragment();
        thingsFragment = new ThingsFragment();

        mFragments.add(timeFragment);
        mFragments.add(personFragment);
        mFragments.add(thingsFragment);

        mTabTitles.add("按时间");
        mTabTitles.add("按人物");
        mTabTitles.add("按事件");
    }

    //处理搜索、个人中心按钮点击
    private void buttonClick() {
        bt_search = (ImageButton) findViewById(R.id.bt_search);
        bt_home = (ImageButton) findViewById(R.id.bt_home);
        bt_help = (ImageButton) findViewById(R.id.bt_help);
        fab_add = (ImageButton) findViewById(R.id.fab_add);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int user_state = mPreferences.getInt("user_state", 0);

                if (user_state == OUT) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else if (user_state == ON){
                    Intent intent = new Intent(MainActivity.this, MyManageActivity.class);
                    startActivity(intent);
                }
            }
        });

        bt_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用帮助页面
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, NewRecords.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("提示");
        mDialog.setMessage("亲，您确定退出\"爱记账\"吗?");
        mDialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mDialog.create();
        mDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Fragment> newFragmentList = new ArrayList<>();
        newFragmentList.add(new TimeFragment());
        newFragmentList.add(new PersonFragment());
        newFragmentList.add(new ThingsFragment());
        mAdapter.refreshFragments(newFragmentList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
