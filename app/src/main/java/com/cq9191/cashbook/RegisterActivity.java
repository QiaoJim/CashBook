package com.cq9191.cashbook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity {

    EditText phone;
    EditText passw;
    EditText passw_repeat;
    EditText nick_name;

    Button regiter;
    ImageButton back;
    ImageButton finish;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //读取超级用户数据
        mPreferences = getSharedPreferences("user_data",MODE_PRIVATE);
        mEditor = mPreferences.edit();

        //点击事件监听
        clickButton();

        //获取新用户信息
        getNewCount();
    }

    private void clickButton() {
        regiter = (Button) findViewById(R.id.regiter_button);
        back = (ImageButton) findViewById(R.id.ib_back);
        finish = (ImageButton) findViewById(R.id.ib_yes);

        regiter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String account = phone.getText().toString();
                String passwd = passw.getText().toString();
                String passwd_repeat = passw_repeat.getText().toString();
                String nickname = nick_name.getText().toString();

                int user_num = mPreferences.getInt("user_num", 0);

                if (user_num == 0) {

                    if (passwd.equals(passwd_repeat) && !passwd.equals("")
                            && !account.equals("") && !nickname.equals("")) {
                        mEditor.putInt("user_num", 1);
                        mEditor.putString("user_account", account);
                        mEditor.putString("user_passwd", passwd);
                        mEditor.putString("user_nickname", nickname);
                        mEditor.putInt("user_state", 1);
                        mEditor.commit();

                        Intent intent = new Intent(RegisterActivity.this, MyManageActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (!passwd.equals(passwd_repeat)) {
                        Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    } else if (passwd.equals("")) {
                        Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else if (account.equals("")) {
                        Toast.makeText(RegisterActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else if (nickname.equals("")) {
                        Toast.makeText(RegisterActivity.this, "亲，取个昵称吧", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (user_num == 1){
                    Toast.makeText(RegisterActivity.this, "已存在超级用户，不可注册", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String account = phone.getText().toString();
                String passwd = passw.getText().toString();
                String passwd_repeat = passw_repeat.getText().toString();
                String nickname = nick_name.getText().toString();

                int user_num = mPreferences.getInt("user_num", 0);

                if (user_num == 0) {

                    if (passwd.equals(passwd_repeat) && !passwd.equals("")
                            && !account.equals("") && !nickname.equals("")) {
                        mEditor.putInt("user_num", 1);
                        mEditor.putString("user_account", account);
                        mEditor.putString("user_passwd", passwd);
                        mEditor.putString("user_nickname", nickname);
                        mEditor.putInt("user_state", 1);
                        mEditor.commit();

                        Intent intent = new Intent(RegisterActivity.this, MyManageActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (!passwd.equals(passwd_repeat)) {
                        Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    } else if (passwd.equals("")) {
                        Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else if (account.equals("")) {
                        Toast.makeText(RegisterActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else if (nickname.equals("")) {
                        Toast.makeText(RegisterActivity.this, "亲，取个昵称吧", Toast.LENGTH_SHORT).show();
                    }

                }
                else if (user_num == 1){
                    Toast.makeText(RegisterActivity.this, "已存在超级用户，不可注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getNewCount() {
        phone = (EditText) findViewById(R.id.phone);
        passw = (EditText) findViewById(R.id.password);
        passw_repeat = (EditText) findViewById(R.id.password_repeat);
        nick_name = (EditText) findViewById(R.id.nick_name);

        //设置hint
        setMyHint();
    }

    private void setMyHint() {
        // 新建一个可以添加属性的文本对象
        SpannableString hint1 = new SpannableString("请输入手机号");
        SpannableString hint2 = new SpannableString("请输入新密码");
        SpannableString hint3 = new SpannableString("请再次输入新密码");
        SpannableString hint4 = new SpannableString("请输入昵称");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan text_size = new AbsoluteSizeSpan(14,true);
        // 附加属性到文本
        hint1.setSpan(text_size, 0, hint1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hint2.setSpan(text_size, 0, hint2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hint3.setSpan(text_size, 0, hint3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hint4.setSpan(text_size, 0, hint4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        phone.setHint(new SpannedString(hint1));
        passw.setHint(new SpannedString(hint2));
        passw_repeat.setHint(new SpannedString(hint3));
        nick_name.setHint(new SpannedString(hint4));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

