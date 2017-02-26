package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.R;

public class ChangePassword extends Activity implements View.OnClickListener{

    private Button bt_sure_to_change;
    private Button bt_back;

    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_new_password_repeat;

    private String old_pass;
    private String old_pass_input;
    private String new_pass;
    private String new_pass_repeat;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        //获得SharedPreferences，读取数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        bt_sure_to_change = (Button) findViewById(R.id.bt_change_password);
        bt_back = (Button) findViewById(R.id.bt_back);

        bt_sure_to_change.setTag(1);
        bt_back.setTag(2);

        bt_sure_to_change.setOnClickListener(this);
        bt_back.setOnClickListener(this);

        et_old_password = (EditText) findViewById(R.id.old_password_input);
        et_new_password = (EditText) findViewById(R.id.new_password_input);
        et_new_password_repeat = (EditText) findViewById(R.id.new_password_repeat);

        // 新建一个可以添加属性的文本对象
        SpannableString hint1 = new SpannableString(" 请输入原始密码");
        SpannableString hint2 = new SpannableString(" 请输入新密码");
        SpannableString hint3 = new SpannableString(" 请再次输入新密码");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan text_size = new AbsoluteSizeSpan(14,true);
        // 附加属性到文本
        hint1.setSpan(text_size, 1, hint1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hint2.setSpan(text_size, 1, hint2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hint3.setSpan(text_size, 1, hint3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        et_old_password.setHint(new SpannedString(hint1));
        et_new_password.setHint(new SpannedString(hint2));
        et_new_password_repeat.setHint(new SpannedString(hint3));

    }

    @Override
    public void onClick(View view) {

        int tag = (int) view.getTag();

        switch (tag){

            //确认修改密码
            case 1:
                //获得原始正确密码
                old_pass = mPreferences.getString("user_passwd", null);

                //读取输入值
                old_pass_input = et_old_password.getText().toString();
                new_pass = et_new_password.getText().toString();
                new_pass_repeat = et_new_password_repeat.getText().toString();

                if(old_pass_input.equals(old_pass)
                        && new_pass.equals(new_pass_repeat)
                        && !new_pass.equals("")){

                    //修改密码
                    mEditor.putString("user_passwd", new_pass);
                    mEditor.commit();

                    Intent intent = new Intent(ChangePassword.this, MyManageActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(ChangePassword.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                }
                else if(!old_pass_input.equals(old_pass)){
                    Toast.makeText(ChangePassword.this, "原始密码错误", Toast.LENGTH_SHORT).show();
                }
                else if (!new_pass.equals(new_pass_repeat)){
                    Toast.makeText(ChangePassword.this, "2次密码输入不一致", Toast.LENGTH_SHORT).show();
                }
                else if (new_pass.equals("")){
                    Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;

            //返回
            case 2:
                Intent intent = new Intent(ChangePassword.this, MyManageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
