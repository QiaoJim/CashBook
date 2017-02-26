package com.cq9191.cashbook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashData;
import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyManageUi.DeleteCash;
import com.cq9191.cashbook.ThreeMainFragments.MyListAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResultListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private String person;
    private String things;
    private String start_time;
    private String end_time;
    private float min_money;
    private float max_money;
    private boolean[] tag;

    CashDatabaseHelper helper;
    SQLiteDatabase db;

    private List<Integer> _id_record;
    private List<String> money_record;
    private List<String> person_record;
    private List<String> things_record;

    private List<String> time_record;
    private List<String> hour_record;
    private List<String> week_record;

    private List<String> desc_record;
    private List<Integer> tag_record;

    private ListView listView;
    private MyListAdapter adapter;

    private int is_from_delete_cash;
    private TextView top_title;
    private TextView tv_things_tip;
    private TextView tv_things_total_detail;
    private TextView tv_month_in;
    private TextView tv_month_out;
    private TextView tv_month_left;

    private ImageButton ib_back;
    private Button bt_sure_delete;

    private LinearLayout ll_things_tip;
    private LinearLayout ll_month_tip;

    final int QUERY = 1;
    final int DELETE = 2;

    private String month_in_money;
    private String month_out_money;
    private String month_left_money;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_result_layout);

        //创建数据库
        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        is_from_delete_cash = getIntent().getIntExtra("from_delete_cash", 0);

        top_title = (TextView) findViewById(R.id.top_title);
        tv_things_tip = (TextView) findViewById(R.id.tv_things_tip);
        tv_things_total_detail = (TextView) findViewById(R.id.tv_things_total_detail);

        tv_month_in = (TextView) findViewById(R.id.tv_month_in);
        tv_month_out = (TextView) findViewById(R.id.tv_month_out);
        tv_month_left = (TextView) findViewById(R.id.tv_month_left);

        ll_things_tip = (LinearLayout) findViewById(R.id.ll_things_tip);
        ll_month_tip = (LinearLayout) findViewById(R.id.ll_month_tip);

        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);
        bt_sure_delete= (Button) findViewById(R.id.bt_sure_delete);
        bt_sure_delete.setOnClickListener(this);


        _id_record = new ArrayList<>();
        money_record = new ArrayList<>();
        person_record = new ArrayList<>();
        things_record = new ArrayList<>();
        time_record = new ArrayList<>();
        hour_record = new ArrayList<>();
        week_record = new ArrayList<>();
        desc_record = new ArrayList<>();
        tag_record = new ArrayList<>();

        //得到搜索条件
        getRequirement();

        //搜索
        doQuery();

        showResult();

        if (is_from_delete_cash == 1) {
            top_title.setText("将要删除的账单");
            ll_things_tip.setVisibility(View.GONE);
            ll_month_tip.setVisibility(View.GONE);
        }
        else {
            bt_sure_delete.setVisibility(View.GONE);
            //统计事件的总收入/支出金额
            if(tag[1] && !things.equals("所有") && !things.equals("")){
                ll_things_tip.setVisibility(View.VISIBLE);
                if(tag_record.get(0) == 0){
                    tv_things_tip.setText("“"+things+"”" + "总收入金额： ");
                    tv_things_total_detail.setTextColor(getColor(R.color.green));
                    tv_things_total_detail.setText( SumThingsTotalMoney() + "元");
                }
                else if(tag_record.get(0) == 1){
                    tv_things_tip.setText("“"+things+"”" + "总支出金额： ");
                    tv_things_total_detail.setTextColor(getColor(R.color.red));
                    tv_things_total_detail.setText( SumThingsTotalMoney() + "元");
                }
            }
            //统计该时间段的总收入，支出金额
            else if (tag[2] && tag[3] && !start_time.equals("") && !end_time.equals("")){
                ll_month_tip.setVisibility(View.VISIBLE);
                SumMonthTotalMoney();
                if(month_in_money.equals(".0")){
                    tv_month_in.setText("0元");
                }
                else {
                    tv_month_in.setText(month_in_money+"元");
                }
                if (month_out_money.equals(".0")){
                    tv_month_out.setText("0元");
                }
                else {
                    tv_month_out.setText(month_out_money+"元");
                }
                if (month_left_money.equals(".0")){
                    tv_month_left.setText("0元");
                }
                else {
                    tv_month_left.setText(month_left_money+"元");
                }

            }

        }
    }

    private double SumThingsTotalMoney() {
        DecimalFormat df = new DecimalFormat("#.0");

        double sum = 0;
        for (int i = 0;i<money_record.size();i++){
            sum += Double.valueOf(money_record.get(i));
        }

        String info = df.format(sum);
        return Double.valueOf(info);
    }

    private void SumMonthTotalMoney(){
        DecimalFormat df = new DecimalFormat("#.0");

        double in = 0;
        double out = 0;
        double left = 0;
        for (int i = 0; i<money_record.size(); i++){
            if (tag_record.get(i) == 0){
                in += Double.valueOf(money_record.get(i));
            }
            else if (tag_record.get(i) == 1){
                out += Double.valueOf(money_record.get(i));
            }
        }
        left = in - out;
        month_in_money = df.format(in);
        month_out_money = df.format(out);
        month_left_money = df.format(left);
    }

    private void showResult() {

        if (person_record.size() > 0) {
            listView = (ListView) findViewById(R.id.query_list);

            adapter = new MyListAdapter(this,time_record, hour_record, week_record,
                    person_record, things_record, money_record, tag_record);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

        } else {
            final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setTitle("提示");
            mDialog.setMessage("亲，暂无相关账单数据哦!");
            mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            mDialog.create();
            mDialog.show();

        }
    }

    private List<? extends Map<String, ?>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < person_record.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("time", time_record.get(i) + "    " +
                    hour_record.get(i) + "    " + week_record.get(i));
            map.put("person", person_record.get(i));
            map.put("things", things_record.get(i));
            map.put("money", money_record.get(i) + " 元");
            list.add(map);
        }
        return list;
    }

    public void getRequirement() {
        QueryRequirement requirement = (QueryRequirement) getIntent().getSerializableExtra("requirement");
        person = requirement.getPerson();
        things = requirement.getThings();
        start_time = requirement.getStart_time();
        end_time = requirement.getEnd_time();

        min_money = requirement.getMin_money().equals("") ? 0 : Float.valueOf(requirement.getMin_money());
        max_money = requirement.getMax_money().equals("") ? 0 : Float.valueOf(requirement.getMax_money());

        tag = requirement.getTag();
    }

    private String createSQL(int query_or_delete){

        StringBuilder sql;
        if (query_or_delete == DELETE){
            sql = new StringBuilder("delete from cash");
        }else {
            sql = new StringBuilder("select * from cash");
        }
        boolean hasWhere = false;

        if (tag[0]) {
            String str = "";
            if (!person.equals("") && !person.equals("所有")) {
                str = "person = '" + person + "'";
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }

        }
        if (tag[1]) {
            String str = "";
            if (!things.equals("") && !things.equals("所有")) {
                str = "things = '" + things + "'";
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }

        }
        if (tag[2]) {
            String str = "";
            if (!start_time.equals("")) {
                str = "time >= '" + start_time + "'";
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }

        }
        if (tag[3]) {
            String str = "";
            if (!end_time.equals("")) {
                str = "time <= '" + end_time + "'";
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }
        }
        if (tag[4]) {
            if (!(min_money == 0)) {
                String str = "money >= " + min_money;
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }
            if (!(max_money == 0)) {
                String str = "money <= " + max_money;
                hasWhere = addWhereIfNeed(sql, hasWhere);
                sql.append(str);
            }
        }
        if (query_or_delete != DELETE) {
            sql.append(" order by time DESC, hour DESC;");
        }

        Log.v("--- create SQL ---", sql.toString());

        return String.valueOf(sql);
    }

    private void doQuery() {

        Cursor cursor = db.rawQuery(createSQL(QUERY), null);
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

    private boolean addWhereIfNeed(StringBuilder sql, boolean hasWhere) {
        if (hasWhere == false) {
            sql.append(" where ");
            return true;
        }
        sql.append(" and ");
        return true;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                if (is_from_delete_cash == 1) {
                    Intent intent = new Intent(QueryResultListActivity.this, DeleteCash.class);
                    startActivity(intent);
                    finish();
                } else {
                    onBackPressed();
                }
                break;

            case R.id.bt_sure_delete:
                if (is_from_delete_cash == 1) {
                    doDeleteCash();
                    Toast.makeText(QueryResultListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
        }
    }

    public void doDeleteCash(){
        db.execSQL(createSQL(DELETE));
    }

    @Override
    public void onItemClick(AdapterView<?> a, View v, int position, long l) {

        //传入该改单数据，在详情页显示
        CashData cash_data = new CashData();
        cash_data.setCash_id(_id_record.get(position));

        cash_data.setTime(time_record.get(position));
        cash_data.setHour(hour_record.get(position));
        cash_data.setWeek(week_record.get(position));

        cash_data.setCash_person(person_record.get(position));
        cash_data.setCash_things(things_record.get(position));
        cash_data.setCash_money(money_record.get(position));
        cash_data.setCash_desc(desc_record.get(position));

        Intent intent = new Intent(QueryResultListActivity.this, DetailRecords.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cash_data", cash_data);
        bundle.putInt("fragment_from", 4);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
