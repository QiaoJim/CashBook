package com.cq9191.cashbook;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashData;
import com.cq9191.cashbook.Data.CashDatabaseHelper;


/**
 * Created by 91910 on 2016/7/19.
 */
public class DetailRecords extends Activity implements View.OnClickListener {

    private ImageButton ib_edit;
    private ImageButton ib_add;
    private Button bt_edit;
    private Button bt_delete;
    private RadioButton bt_tag_in;
    private RadioButton bt_tag_out;

    private EditText time;
    private EditText person;
    private EditText things;
    private EditText money;
    private EditText desc;

    private int fragment_from;
    private String certain_title;

    private CashData cash_data;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    CashDatabaseHelper helper;
    SQLiteDatabase db;

    private int user_state = 0;
    private ImageButton ib_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_records_layout);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        //获取基本数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        fragment_from = getIntent().getIntExtra("fragment_from", 0);
        certain_title = getIntent().getStringExtra("certain_title");

        showDetailCash();

        myBtClick();
    }

    private void showDetailCash() {
        time = (EditText) findViewById(R.id.show_time);
        person = (EditText) findViewById(R.id.show_person);
        things = (EditText) findViewById(R.id.show_things);
        money = (EditText) findViewById(R.id.show_money);
        desc = (EditText) findViewById(R.id.show_desc);
        bt_tag_in = (RadioButton) findViewById(R.id.tag_in);
        bt_tag_out = (RadioButton) findViewById(R.id.tag_out);

        cash_data = (CashData) getIntent().getSerializableExtra("cash_data");

        time.setText(cash_data.getTime()+ "    " +
                cash_data.getHour() + "    " + cash_data.getWeek());
        person.setText(cash_data.getCash_person());
        things.setText(cash_data.getCash_things());
        money.setText(cash_data.getCash_money() + " 元");
        desc.setText(cash_data.getCash_desc().equals("") ? "暂无描述" : cash_data.getCash_desc());
        if(cash_data.getTag() == 0){
            bt_tag_in.setChecked(true);
            bt_tag_out.setChecked(false);
        }
        else if(cash_data.getTag() == 1){
            bt_tag_in.setChecked(false);
            bt_tag_out.setChecked(true);
        }

        //Log.v("--- 3 ----",cash_data.getCash_time());

        desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (desc.hasFocus()) {
                    desc.setBackgroundResource(R.drawable.blue_border);
                } else if (!desc.hasFocus()) {
                    desc.setBackgroundResource(R.drawable.gray_border);
                }
            }
        });
    }

    private void myBtClick() {
        ib_edit = (ImageButton) findViewById(R.id.ib_edit);
        ib_add = (ImageButton) findViewById(R.id.ib_add);
        bt_edit = (Button) findViewById(R.id.bt_edit);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        ib_back =(ImageButton) findViewById(R.id.ib_back);

        ib_edit.setOnClickListener(this);
        ib_add.setOnClickListener(this);
        bt_edit.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        ib_back.setOnClickListener(this);
    }

    //返回、编辑记录、添加记录 按钮监听
    @Override
    public void onClick(View view) {

        //修改账单
        if (view.getId() == R.id.ib_edit || view.getId() == R.id.bt_edit || view.getId() == R.id.bt_delete) {

            user_state = mPreferences.getInt("user_state", 0);

            if (user_state == 0) {//超级用户未登录，不可修改
                final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
                mDialog.setTitle("提示");
                mDialog.setMessage("亲，您还未登录，登录后可编辑账单!\n是否现在登录?");
                mDialog.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(DetailRecords.this, HomeActivity.class);
                        intent.putExtra("From_DetailRecords", true);
                        startActivity(intent);
                    }
                });
                mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                mDialog.create();
                mDialog.show();
            } else if (user_state == 1) {//超级用户已登录。

                //进入修改页面
                if (view.getId() == R.id.ib_edit || view.getId() == R.id.bt_edit) {
                    Intent intent_2 = new Intent(DetailRecords.this, EditRecords.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cash_data", cash_data);
                    bundle.putInt("fragment_from", fragment_from);
                    bundle.putString("certain_title", certain_title);
                    intent_2.putExtras(bundle);
                    startActivity(intent_2);
                    finish();
                }
                //删除该账单
                else if (view.getId() == R.id.bt_delete) {
                    final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
                    mDialog.setTitle("提示");
                    mDialog.setMessage("亲，您确定要删除该账单吗?\n数据不可恢复呀！");
                    mDialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            db.delete("cash", "_id = ?",
                                    new String[]{String.valueOf(cash_data.getCash_id())});

                            if (fragment_from == 1) {
                                Intent intent = new Intent(DetailRecords.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (fragment_from == 2 || fragment_from == 3) {
                                Intent intent = new Intent(DetailRecords.this, CertainCashListActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("fragment_from", fragment_from);
                                bundle.putString("certain_title", certain_title);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }else if (fragment_from == 4){
                                onBackPressed();
                                finish();
                            }

                            Toast.makeText(DetailRecords.this, "删除成功", Toast.LENGTH_SHORT).show();
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
            }
        }

        //添加新账单
        else if (view.getId() == R.id.ib_add) {
            Intent intent_3 = new Intent(DetailRecords.this, NewRecords.class);
            startActivity(intent_3);
            finish();
        }
        else if (view.getId() == R.id.ib_back){
            if (fragment_from == 1) {
                Intent intent = new Intent(DetailRecords.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (fragment_from == 2 || fragment_from == 3) {
                Intent intent = new Intent(DetailRecords.this, CertainCashListActivity.class);
                intent.putExtra("fragment_from", fragment_from);
                intent.putExtra("certain_title", certain_title);
                startActivity(intent);
                finish();
            } else if (fragment_from == 4){
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment_from == 1) {
            Intent intent = new Intent(DetailRecords.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (fragment_from == 2 || fragment_from == 3) {
            Intent intent = new Intent(DetailRecords.this, CertainCashListActivity.class);
            intent.putExtra("fragment_from", fragment_from);
            intent.putExtra("certain_title", certain_title);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}
