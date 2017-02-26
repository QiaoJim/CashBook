package com.cq9191.cashbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.cq9191.cashbook.Data.CashData;
import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertainCashListActivity extends Activity {

    private int fragment_from = 0;     //1-时间；2-人物；3-事件
    private String certain_title;

    private List<Integer> _id_record;

    private List<String> time_record;
    private List<String> hour_record;
    private List<String> week_record;

    private List<String> person_record;
    private List<String> things_record;
    private List<String> money_record;
    private List<String> desc_record;
    private List<Integer> tag_record;

    private ListView listView;
    private MyCertainListAdapter adapter;

    private TextView certain_cash_title;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    private static final int PERSON = 2;
    private static final int THINGS= 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certain_cash_list);

        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        certain_cash_title = (TextView) findViewById(R.id.certain_cash_title);

        //获得Fragment传入的
        fragment_from = getIntent().getIntExtra("fragment_from", 0);
        certain_title = getIntent().getStringExtra("certain_title");



        //加载 按人物、按事件 数组数据
        initList();


        listView = (ListView) findViewById(R.id.certain_cash_list);
        /*
        simpleAdapter = new SimpleAdapter(this, getList(fragment_from),
                R.layout.certain_cash_item,
                new String[]{"time", "person_or_things", "money",},
                new int[]{R.id.tv_time, R.id.tv_person_or_things, R.id.tv_money});
                */

        //设置列表页面标题
        if (fragment_from == 2) {
            certain_cash_title.setText(certain_title + "的账单");
            adapter = new MyCertainListAdapter(PERSON, this,
                    time_record, hour_record, week_record, person_record, things_record, money_record, tag_record);

        } else if (fragment_from == 3) {
            certain_cash_title.setText(certain_title + "的账单");
            adapter = new MyCertainListAdapter(THINGS, this,
                    time_record, hour_record, week_record, person_record, things_record, money_record, tag_record);
        }
        listView.setAdapter(adapter);

        //按钮点击
        buttonClick();

        //列表项点击
        itemClick();
    }

    private void buttonClick() {
        ImageButton search = (ImageButton) findViewById(R.id.ib_search);
        ImageButton home = (ImageButton) findViewById(R.id.ib_home);
        ImageButton add = (ImageButton) findViewById(R.id.fab_add);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CertainCashListActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int user_state = mPreferences.getInt("user_state", 0);
                if (user_state == 0){
                    Intent intent = new Intent(CertainCashListActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (user_state == 1){
                    Intent intent = new Intent(CertainCashListActivity.this, MyManageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CertainCashListActivity.this, NewRecords.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initList() {

        _id_record = new ArrayList<>();

        time_record = new ArrayList<>();
        hour_record = new ArrayList<>();
        week_record = new ArrayList<>();

        person_record = new ArrayList<>();
        things_record = new ArrayList<>();
        money_record = new ArrayList<>();
        desc_record = new ArrayList<>();
        tag_record = new ArrayList<>();

        CashDatabaseHelper helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        //按人物 排序列表
        if (fragment_from == 2) {

            Cursor cursor = db.query("cash", null, "person = ?", new String[]{certain_title}, null,
                    null, "time DESC, hour DESC");
            while (cursor.moveToNext()) {
                _id_record.add(cursor.getInt(cursor.getColumnIndex("_id")));

                time_record.add(cursor.getString(cursor.getColumnIndex("time")));
                hour_record.add(cursor.getString(cursor.getColumnIndex("hour")));
                week_record.add(cursor.getString(cursor.getColumnIndex("week")));

                money_record.add(cursor.getString(cursor.getColumnIndex("money")));
                person_record.add(cursor.getString(cursor.getColumnIndex("person")));
                things_record.add(cursor.getString(cursor.getColumnIndex("things")));
                desc_record.add(cursor.getString(cursor.getColumnIndex("desc")));
                tag_record.add(cursor.getInt(cursor.getColumnIndex("tag")));
            }
        }
        //按事件 排序列表
        else if (fragment_from == 3) {
            Cursor cursor = db.query("cash", null, "things = ?", new String[]{certain_title}, null,
                    null, "time DESC, hour DESC");
            while (cursor.moveToNext()) {
                _id_record.add(cursor.getInt(cursor.getColumnIndex("_id")));

                time_record.add(cursor.getString(cursor.getColumnIndex("time")));
                hour_record.add(cursor.getString(cursor.getColumnIndex("hour")));
                week_record.add(cursor.getString(cursor.getColumnIndex("week")));

                money_record.add(cursor.getString(cursor.getColumnIndex("money")));
                person_record.add(cursor.getString(cursor.getColumnIndex("person")));
                things_record.add(cursor.getString(cursor.getColumnIndex("things")));
                desc_record.add(cursor.getString(cursor.getColumnIndex("desc")));
                tag_record.add(cursor.getInt(cursor.getColumnIndex("tag")));
            }
        }

    }

    private void itemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //传入该改单数据，在详情页显示
                CashData cash_data = new CashData();

                cash_data.setCash_id(_id_record.get(i));

                cash_data.setTime(time_record.get(i));
                cash_data.setHour(hour_record.get(i));
                cash_data.setWeek(week_record.get(i));

                cash_data.setCash_person(person_record.get(i));
                cash_data.setCash_things(things_record.get(i));
                cash_data.setCash_money(money_record.get(i));
                cash_data.setCash_desc(desc_record.get(i));
                cash_data.setTag(tag_record.get(i));

                Intent intent = new Intent(CertainCashListActivity.this, DetailRecords.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cash_data", cash_data);
                bundle.putInt("fragment_from", fragment_from);
                bundle.putString("certain_title", certain_title);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }


    private List<? extends Map<String, ?>> getList(int person_or_things) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < time_record.size(); i++) {
            Map<String, Object> map = new HashMap<>();

            map.put("time", time_record.get(i) + "    " +
                    hour_record.get(i) + "    " + week_record.get(i));

            //填充不同的信息
            if (fragment_from == 2) {    //人物
                map.put("person_or_things", things_record.get(i));
            } else if (person_or_things == 3) {    //事件
                map.put("person_or_things", person_record.get(i));
            }
            map.put("money", money_record.get(i) + " 元");
            list.add(map);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CertainCashListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
