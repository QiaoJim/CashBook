package com.cq9191.cashbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cq9191.cashbook.MyManageUi.AboutUs;
import com.cq9191.cashbook.MyManageUi.AddNewPersonCards;
import com.cq9191.cashbook.MyManageUi.AddNewThingsCards;
import com.cq9191.cashbook.MyManageUi.ChangePassword;
import com.cq9191.cashbook.MyManageUi.CreateCash;
import com.cq9191.cashbook.MyManageUi.Feedback;
import com.cq9191.cashbook.MyManageUi.DeleteCash;
import com.cq9191.cashbook.MyManageUi.TotalDetail;

public class MyManageActivity extends Activity implements View.OnClickListener {
    //存储登录用户名
    private String my_account;

    private TextView textView;

    //功能按钮
    private Button bt_change_password;
    private Button bt_total_detail;
    private Button bt_add_new_person;
    private Button bt_add_new_things;
    private Button bt_delete_cash;
    private Button bt_create_cash;
    private Button bt_feedback;
    private Button bt_about_us;
    private Button bt_logout;

    private ImageButton ib_back;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_manage_layout);

        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        //获取登录的用户名
        my_account = mPreferences.getString("user_nickname", "");

        textView = (TextView) findViewById(R.id.tv_my_account);
        textView.setText(my_account);

        bt_change_password = (Button) findViewById(R.id.bt_change_password);
        bt_total_detail = (Button) findViewById(R.id.bt_total_detail);
        bt_add_new_person = (Button) findViewById(R.id.bt_add_new_person);
        bt_add_new_things = (Button) findViewById(R.id.bt_add_new_things);
        bt_delete_cash = (Button) findViewById(R.id.bt_delete_cash);
        bt_create_cash = (Button) findViewById(R.id.bt_create_cash);
        bt_feedback = (Button) findViewById(R.id.bt_feedback);
        bt_about_us = (Button) findViewById(R.id.bt_about_us);
        bt_logout = (Button) findViewById(R.id.bt_logout);
        ib_back = (ImageButton) findViewById(R.id.ib_back);

        bt_change_password.setOnClickListener(this);
        bt_total_detail.setOnClickListener(this);
        bt_add_new_person.setOnClickListener(this);
        bt_add_new_things.setOnClickListener(this);
        bt_about_us.setOnClickListener(this);
        bt_create_cash.setOnClickListener(this);
        bt_feedback.setOnClickListener(this);
        bt_delete_cash.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        ib_back.setOnClickListener(this);

        bt_change_password.setTag(1);
        bt_add_new_person.setTag(2);
        bt_add_new_things.setTag(3);
        bt_delete_cash.setTag(4);
        bt_create_cash.setTag(5);
        bt_feedback.setTag(6);
        bt_about_us.setTag(7);
        bt_logout.setTag(8);
        ib_back.setTag(9);
        bt_total_detail.setTag(10);

    }

    //按钮点击监听器
    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        switch (tag){
            case 1:      //密码修改
                Intent intent_1= new Intent(MyManageActivity.this, ChangePassword.class);
                startActivity(intent_1);
                break;
            case 2:     //添加人物分类卡
                Intent intent_2 = new Intent(MyManageActivity.this, AddNewPersonCards.class);
                startActivity(intent_2);
                break;
            case 3:     //添加事件人类卡
                Intent intent_3 = new Intent(MyManageActivity.this, AddNewThingsCards.class);
                startActivity(intent_3);
                break;
            case 4:     //删除缓存
                Intent intent_4 = new Intent(MyManageActivity.this, DeleteCash.class);
                startActivity(intent_4);
                break;
            case 5:     //生成自定义账单
                Intent intent_5 = new Intent(MyManageActivity.this, CreateCash.class);
                startActivity(intent_5);
                break;
            case 6:     //意见反馈
                Intent intent_6 = new Intent(MyManageActivity.this, Feedback.class);
                startActivity(intent_6);
                break;
            case 7:     //关于我们
                Intent intent_7 = new Intent(MyManageActivity.this, AboutUs.class);
                startActivity(intent_7);
                break;
            case 8:     //退出登录

                mEditor.putInt("user_state", 0);
                mEditor.commit();

                Intent intent_8 = new Intent(MyManageActivity.this, HomeActivity.class);
                startActivity(intent_8);
                finish();
                break;
            case 9:     //返回
                Intent intent_9 = new Intent(MyManageActivity.this, MainActivity.class);
                startActivity(intent_9);
                break;
            case 10:
                Intent intent_10 = new Intent(MyManageActivity.this, TotalDetail.class);
                startActivity(intent_10);
                break;
        }
    }

}
