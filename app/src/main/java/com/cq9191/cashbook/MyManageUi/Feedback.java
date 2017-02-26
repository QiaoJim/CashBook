package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.R;

public class Feedback extends Activity implements View.OnClickListener{

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        editText = (EditText) findViewById(R.id.et_feedback);

        Button back = (Button) findViewById(R.id.bt_back);
        Button put = (Button) findViewById(R.id.bt_put);

        back.setOnClickListener(this);
        put.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_back:
                Intent intent = new Intent(Feedback.this, MyManageActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_put:
                if(!editText.getText().toString().equals("")) {
                    Toast.makeText(this, "真心谢谢您提交的宝贵意见", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "还没有输入您的宝贵意见哦", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
