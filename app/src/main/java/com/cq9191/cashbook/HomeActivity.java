package com.cq9191.cashbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

    private String instruction = "             你还没有登录哦！"+
            "\n       登陆以后开启更多精彩功能！"+"\n"+
            "\n1、自定义设置账单保存的最长时限；"+
            "\n2、自定义添加人物和事件分类，更方\n    便使用；"+
            "\n3、生成一段时间内的账单，方便调\n    整购物计划" +
            "\n";

    private TextView textView;
    private EditText et_account;
    private EditText et_passwd;
    private Button login;
    private Button register;
    private ImageButton back;
    private CheckBox keep_email;
    private boolean From_DetailRecords = false;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    private String user_accouont;
    private String user_passwd;
    private int user_keep_account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login_layout);

        //获得SharedPreferences，读取数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        //获得超级用户 账号、密码
        user_accouont = mPreferences.getString("user_account","");
        user_passwd = mPreferences.getString("user_passwd","");
        user_keep_account = mPreferences.getInt("user_keep_account", 0);

        From_DetailRecords = getIntent().getBooleanExtra("From_DetailRecords", false);

        et_account = (EditText) findViewById(R.id.et_account);
        et_passwd = (EditText) findViewById(R.id.et_password);
        textView = (TextView) findViewById(R.id.tv_instruction);
        keep_email = (CheckBox) findViewById(R.id.bt_keep_email);

        textView.setText(instruction);

        //记住的用户账号
        if (user_keep_account == 1){
            keep_email.setChecked(true);
            et_account.setText(user_accouont);
        }

        clickButton();

        keep_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mEditor.putInt("user_keep_account", 1);
                }
                else {
                    mEditor.putInt("user_keep_account", 0);
                }
                mEditor.commit();
            }
        });
    }

    private void clickButton() {
        login = (Button) findViewById(R.id.bt_login);
        register = (Button) findViewById(R.id.bt_register);
        back = (ImageButton) findViewById(R.id.ib_back);

        //登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String account = et_account.getText().toString();
                String passwd = et_passwd.getText().toString();

                if (account.equals(user_accouont) && passwd.equals(user_passwd)
                        && !account.equals("") && !passwd.equals("")) {

                    //状态：用户已登录
                    mEditor.putInt("user_state", 1);
                    mEditor.commit();

                    if (From_DetailRecords){
                        Intent intent = new Intent(HomeActivity.this, DetailRecords.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(HomeActivity.this, MyManageActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    Toast.makeText(HomeActivity.this, "登录成功，欢迎你！", Toast.LENGTH_SHORT).show();
                }
                else if(!account.equals(user_accouont)){
                    Toast.makeText(HomeActivity.this, "账号错误", Toast.LENGTH_SHORT).show();
                }
                else if (!passwd.equals(user_passwd)){
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
