package com.cq9191.cashbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;


public class EditRecords extends Activity implements View.OnClickListener {


    private EditText et_time;
    private EditText et_hour;
    private EditText et_week;

    private EditText et_person;
    private EditText et_things;
    private EditText et_money;
    private EditText et_desc;
    private RadioButton tag_in;
    private RadioButton tag_out;
    private RadioGroup rg_tag;

    private CashData cash_data;

    private int fragment_from;
    private String certain_title;

    private CashDatabaseHelper helper;
    private SQLiteDatabase db;
    private ImageButton finish;
    private Button ok;
    private Button cancel;

    private int tag_in_out = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_records_layout);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        fragment_from = getIntent().getIntExtra("fragment_from", 0);
        certain_title = getIntent().getStringExtra("certain_title");

        //显示需要修改的账单数据
        cash_data = (CashData) getIntent().getSerializableExtra("cash_data");

        et_time = (EditText) findViewById(R.id.et_time);
        et_hour = (EditText) findViewById(R.id.et_hour);
        et_week = (EditText) findViewById(R.id.et_week);
        et_person = (EditText) findViewById(R.id.et_person);
        et_things = (EditText) findViewById(R.id.et_things);
        et_money = (EditText) findViewById(R.id.et_money);
        et_desc = (EditText) findViewById(R.id.et_desc);

        rg_tag = (RadioGroup) findViewById(R.id.bt_tag);
        rg_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tag_in){
                    tag_in_out = 0;
                    Log.d("EditRecords", "in click : tag_int_out"+tag_in_out);
                }
                else if (checkedId == R.id.tag_out){
                    tag_in_out = 1;
                    Log.d("EditRecords", "out click : tag_int_out"+tag_in_out);
                }
            }
        });

        tag_in = (RadioButton) findViewById(R.id.tag_in);
        tag_out = (RadioButton) findViewById(R.id.tag_out);

        et_time.setText(cash_data.getTime());
        et_hour.setText(cash_data.getHour());
        et_week.setText(cash_data.getWeek());

        et_person.setText(cash_data.getCash_person());
        et_things.setText(cash_data.getCash_things());
        et_money.setText(cash_data.getCash_money());
        et_desc.setText(cash_data.getCash_desc().equals("") ? "暂无描述" : cash_data.getCash_desc());
        if(cash_data.getTag() == 0){
            tag_in.setChecked(true);
            tag_out.setChecked(false);
        }
        else if(cash_data.getTag() == 1){
            tag_in.setChecked(false);
            tag_out.setChecked(true);
        }

        myBtClick();

    }

    private void myBtClick() {
        finish = (ImageButton) findViewById(R.id.ib_finish);
        ok = (Button) findViewById(R.id.ib_ok);
        cancel = (Button) findViewById(R.id.ib_cancel);

        finish.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        et_desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && et_desc.getText().toString().equals("暂无描述")){
                    et_desc.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.ib_ok || id == R.id.ib_finish) {

            String time = et_time.getText().toString();
            String hour = et_hour.getText().toString();
            String week = et_week.getText().toString();

            //判断输入合法性
            boolean legal = doInputCheck(time, hour, week);

            if (!legal) {
                Toast.makeText(EditRecords.this, "请输入正确的格式", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Log.d("EditRecords", "finish edit : tag_int_out"+tag_in_out);

                //存入修改后的账单数据
                ContentValues values = new ContentValues();

                values.put("time", time);
                values.put("hour", hour);
                values.put("week", week);

                values.put("person", et_person.getText().toString());
                values.put("things", et_things.getText().toString());
                values.put("money", et_money.getText().toString());
                values.put("desc", et_desc.getText().toString());
                values.put("tag", tag_in_out);

                db.update("cash", values, "_id = ?", new String[]{String.valueOf(cash_data.getCash_id())});

                Intent intent = new Intent(EditRecords.this, DetailRecords.class);
                Bundle bundle = new Bundle();
                //更新数据

                cash_data.setTime(et_time.getText().toString());
                cash_data.setHour(et_hour.getText().toString());
                cash_data.setWeek(et_week.getText().toString());

                cash_data.setCash_person(et_person.getText().toString());
                cash_data.setCash_things(et_things.getText().toString());
                cash_data.setCash_money(et_money.getText().toString());
                cash_data.setCash_desc(et_desc.getText().toString());
                cash_data.setTag(tag_in_out);

                bundle.putSerializable("cash_data", cash_data);
                bundle.putInt("fragment_from", fragment_from);
                bundle.putString("certain_title", certain_title);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

                Toast.makeText(EditRecords.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.ib_cancel) {

            //取消编辑
            Intent intent = new Intent(EditRecords.this, DetailRecords.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("cash_data", cash_data);
            bundle.putInt("fragment_from", fragment_from);
            bundle.putString("certain_title", certain_title);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        }
    }

    private boolean doInputCheck(String time, String hour, String week) {

        boolean flag_year = false;
        boolean flag_month = false;
        boolean flag_day = false;
        boolean flag_break = false;
        boolean flag_hour = false;
        boolean flag_min = false;
        boolean flag_break2 = false;

        //获取 年-月-日
        SimpleDateFormat formatter_1 = new SimpleDateFormat("yyyy-MM-dd");
        //获取 时：分
        SimpleDateFormat formatter_2 = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String now_time = formatter_1.format(date);
        String now_hour = formatter_2.format(date);

        if (time.length() != 10 || hour.length()!= 5
                || !time.substring(4, 5).equals("-") || !time.substring(7, 8).equals("-")
                || !hour.substring(2,3).equals(":")){
            Log.v("--- wrong input---",time.length()+"///"+hour.length());
            return false;
        }
        else {

            int int_year,int_day,int_hour,int_min;

            //判断 年-月-日 格式合法性
            String input_year = time.substring(0, 4);
            String input_break1 = time.substring(4, 5);
            String input_month = time.substring(5, 7);
            String input_break2 = time.substring(7, 8);
            String input_day = time.substring(8, 10);

            String input_hour = hour.substring(0,2);
            String input_break3 = hour.substring(2,3);
            String input_min = hour.substring(3,5);

            Log.v("--- substring ---", time + "\n" + input_year + "  " + input_break1 + "  " + input_month + "  " + input_break2 + "  " + input_day);

            try{
                int_year = Integer.valueOf(input_year);
                int_day = Integer.valueOf(input_day);
                int_hour = Integer.valueOf(input_hour);
                int_min = Integer.valueOf(input_min);
            }catch (Exception e){
                return false;
            }
            if (Integer.valueOf(input_year) <= 9999 || Integer.valueOf(input_year) >= 1000) {
                flag_year = true;
            }
            switch (input_month) {
                case "01":
                case "03":
                case "05":
                case "07":
                case "08":
                case "10":
                case "12":
                    if (Integer.valueOf(input_day) > 0 && Integer.valueOf(input_day) <= 31) {
                        flag_month = true;
                        flag_day = true;
                    }
                    break;
                case "04":
                case "06":
                case "09":
                case "11":
                    if (Integer.valueOf(input_day) > 0 && Integer.valueOf(input_day) <= 30) {
                        flag_month = true;
                        flag_day = true;
                    }
                    break;
                case "02":
                    if (Integer.valueOf(input_day) > 0 && Integer.valueOf(input_day) <= 29) {
                        flag_month = true;
                        flag_day = true;
                    }
                    break;
                default:
                    flag_month = false;
                    flag_day = false;
                    break;
            }
            if (input_break1.equals("-") && input_break2.equals("-")) {
                flag_break = true;
            }
            if (!(flag_break && flag_year && flag_month && flag_day)) {
                Toast.makeText(EditRecords.this, "请输入正确的时间格式", Toast.LENGTH_SHORT).show();
                return false;
            }

            //判断时间 HH：mm 合法性

            if (input_break3.equals(":")){
                flag_break2 = true;
            }
            if (Integer.valueOf(input_hour)>=0 && Integer.valueOf(input_hour)<=23){
                flag_hour = true;
            }
            if (Integer.valueOf(input_min)>=0 && Integer.valueOf(input_min)<=59){
                flag_min = true;
            }
            if (!(flag_hour && flag_min && flag_break2)){
                return false;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //取消编辑
        Intent intent = new Intent(EditRecords.this, DetailRecords.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cash_data", cash_data);
        bundle.putInt("fragment_from", fragment_from);
        bundle.putString("certain_title", certain_title);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
