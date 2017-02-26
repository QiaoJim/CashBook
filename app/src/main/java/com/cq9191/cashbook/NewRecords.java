package com.cq9191.cashbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.MyManageUi.GridViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewRecords extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText et_person;
    private EditText et_time;
    private EditText et_things;
    private EditText et_describ;
    private EditText et_money;

    private ImageButton ib_add;
    private ImageButton ib_back;

    private RadioGroup tag;

    private GridView person_grid;
    private GridView things_grid;

    List<String> person_cards;
    List<String> things_cards;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    private int person_cards_num;
    private int things_cards_num;

    private String time;
    private String hour;
    private String week;
    private int tag_in_out = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_records_layout);

        //获取用户基本数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        //获取 人物卡、事件卡长度
        person_cards_num = mPreferences.getInt("person_cards_num", 0);
        things_cards_num = mPreferences.getInt("things_cards_num", 0);


        et_time = (EditText) findViewById(R.id.et_time);
        et_money = (EditText) findViewById(R.id.et_money);
        et_person = (EditText) findViewById(R.id.et_person);
        et_things = (EditText) findViewById(R.id.et_things);
        et_describ = (EditText) findViewById(R.id.et_describ);


        ib_add = (ImageButton) findViewById(R.id.ib_add);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        //添加账单、返回按钮监听
        ib_add.setOnClickListener(this);
        ib_back.setOnClickListener(this);

        //单选按钮
        tag = (RadioGroup) findViewById(R.id.tag_bt);
        tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.tag_in){
                    tag_in_out = 0;
                }
                else if(checkedId == R.id.tag_out){
                    tag_in_out = 1;
                }
            }
        });

        person_grid = (GridView) findViewById(R.id.person_grid);
        things_grid = (GridView) findViewById(R.id.things_grid);

        //聚焦时改变“描述”输入框的边框颜色
        changeBorder();

        //自动获取当前时间，转化成2016-7-30  15:30  周日   的形式，
        //显示在时间显示框中
        et_time.setText(getNowTime());

        //加载人物卡、事件卡数据
        showCards();
    }

    private void showCards() {
        //获取人物卡数据
        person_cards = new ArrayList<>();
        if (person_cards_num == 0) {
            person_cards.add("我");
            person_cards.add("好友 1");
            person_cards.add("好友 2");
            person_cards.add("员工 1");
            person_cards.add("员工 2");
            person_cards.add("同学 1");
            person_cards.add("同学 2");
        } else {
            for (int k = 0; k < person_cards_num; k++) {
                person_cards.add(mPreferences.getString("person" + k, null));
            }
        }


        //获取事件卡数据
        things_cards = new ArrayList<>();
        if (things_cards_num == 0) {
            things_cards.add("淘宝");
            things_cards.add("早餐");
            things_cards.add("午餐");
            things_cards.add("晚餐");
            things_cards.add("还钱");
            things_cards.add("零食、小吃");
            things_cards.add("购买图书");
            things_cards.add("看电影");
            things_cards.add("新衣服");
            things_cards.add("日用品");
        } else {
            for (int k = 0; k < things_cards_num; k++) {
                things_cards.add(mPreferences.getString("things" + k, null));
            }
        }

        GridViewAdapter pga = new GridViewAdapter(NewRecords.this, person_cards, 1);
        GridViewAdapter tga = new GridViewAdapter(NewRecords.this, things_cards, 2);

        person_grid.setAdapter(pga);
        things_grid.setAdapter(tga);
        //GridView卡片点击监听
        person_grid.setOnItemClickListener(this);
        things_grid.setOnItemClickListener(this);

    }

    //改变描述框的样式
    private void changeBorder() {

        //监听账单描述输入框的焦点变化，聚焦是边框变为 蓝色；失去焦点边框变为 灰色
        et_describ.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (et_describ.hasFocus()) {
                    et_describ.setBackgroundResource(R.drawable.blue_border);
                } else if (!et_describ.hasFocus()) {
                    et_describ.setBackgroundResource(R.drawable.gray_border);
                }
            }
        });
    }

    //按钮点击事件监听
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_add:

                Log.d("NewRecords","收入支出："+tag_in_out);

                String money = et_money.getText().toString();
                String person = et_person.getText().toString();
                String things = et_things.getText().toString();
                String describe = et_describ.getText().toString();

                if (!(person.equals("") || things.equals("") || money.equals("")
                    ||tag_in_out == -1)) {
                    //将新账单数据存入数据库
                    CashDatabaseHelper helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("money", money);
                    values.put("person", person);
                    values.put("things", things);
                    values.put("time", time);
                    values.put("hour", hour);
                    values.put("week", week);
                    values.put("desc", describe);
                    values.put("tag", tag_in_out);

                    long insert = db.insert("cash", null, values);
                    values.clear();

                    if (insert > -1) {
                        Toast.makeText(this, "添加账单成功", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(NewRecords.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_back:

                Intent intent = new Intent(NewRecords.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewRecords.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public String getNowTime() {

        //获取 年-月-日
        SimpleDateFormat formatter_1 = new SimpleDateFormat("yyyy-MM-dd");
        //获取 时：分
        SimpleDateFormat formatter_2 = new SimpleDateFormat("HH:mm");
        //获取 周几
        SimpleDateFormat formatter_3 = new SimpleDateFormat("E");
        Date date = new Date();
        time = formatter_1.format(date);
        hour = formatter_2.format(date);
        week = formatter_3.format(date);
        return time + "  " + hour + "  " + week;
    }

    //GridView卡片项点击监听
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.person_grid) {
            et_person.setText(person_cards.get(i));
        } else if (adapterView.getId() == R.id.things_grid) {
            et_things.setText(things_cards.get(i));
        }
    }

}
