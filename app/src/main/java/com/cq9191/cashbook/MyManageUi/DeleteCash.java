package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cq9191.cashbook.CertainCashListActivity;
import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.QueryResultListActivity;
import com.cq9191.cashbook.R;
import com.cq9191.cashbook.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class DeleteCash extends FragmentActivity implements View.OnClickListener,
        SetStartTimeDialog.OnTimeListener, DropDownListAdapter.MyListItemClickListener {

    private ImageButton ib_back;
    private ImageButton ib_ok;
    private ImageButton ib_drop;
    private ImageButton ib_drop2;
    private ImageButton ib_time;
    private Button bt_delete;
    private Button bt_back;
    private EditText et_person;
    private EditText et_things;
    private EditText et_time;
    private CheckBox cb_person;
    private CheckBox cb_things;
    private CheckBox cb_time;

    private List<String> all_person;
    private List<String> all_things;

    private CashDatabaseHelper helper;
    private SQLiteDatabase db;

    private int num_click = 0;
    private DropDownList mDropDownPersonList;
    private int num_click_2;
    private DropDownList mDropDownThingsList;

    private String tip;
    private String person;
    private String things;
    private String time;
    private boolean[] tag = {false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_cash);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        findView();

        initListData();
    }

    private void findView() {
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_ok = (ImageButton) findViewById(R.id.ib_ok);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_back = (Button) findViewById(R.id.bt_back);
        ib_drop = (ImageButton) findViewById(R.id.bt_dropdown);
        ib_drop2 = (ImageButton) findViewById(R.id.bt_dropdown_2);
        ib_time = (ImageButton) findViewById(R.id.ib_time);
        ib_back.setOnClickListener(this);
        ib_ok.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        ib_drop.setOnClickListener(this);
        ib_drop2.setOnClickListener(this);
        ib_time.setOnClickListener(this);

        et_person = (EditText) findViewById(R.id.et_person);
        et_things = (EditText) findViewById(R.id.et_things);
        et_time = (EditText) findViewById(R.id.et_time);

        cb_person = (CheckBox) findViewById(R.id.cb_person);
        cb_things = (CheckBox) findViewById(R.id.cb_things);
        cb_time = (CheckBox) findViewById(R.id.cb_time);

        cb_person.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_person.setText("");
                }
            }
        });
        cb_things.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_things.setText("");
                }
            }
        });
        cb_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_time.setText("");
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_dropdown:
                ++num_click;
                if (num_click % 2 == 1) {
                    mDropDownPersonList = new DropDownList(1, DeleteCash.this, all_person, DeleteCash.this);
                    mDropDownPersonList.setWidth(et_person.getWidth());
                    mDropDownPersonList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownPersonList.showAsDropDown(et_person);
                } else {
                    mDropDownPersonList.dismiss();
                }
                break;
            case R.id.bt_dropdown_2:
                ++num_click_2;
                if (num_click_2 % 2 == 1) {
                    mDropDownThingsList = new DropDownList(2, DeleteCash.this, all_things, DeleteCash.this);
                    mDropDownThingsList.setWidth(et_things.getWidth());
                    mDropDownThingsList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownThingsList.showAsDropDown(et_things);
                } else {
                    mDropDownThingsList.dismiss();
                }
                break;

            case R.id.ib_time:
                SetStartTimeDialog start_time_picker = new SetStartTimeDialog();
                start_time_picker.show(getSupportFragmentManager(), "time_picker");
                break;

            case R.id.ib_back:
            case R.id.bt_back:
                Intent intent = new Intent(DeleteCash.this, MyManageActivity.class);
                startActivity(intent);
                finish();
                break;

            //清除部分账单
            case R.id.ib_ok:
            case R.id.bt_delete:

                getTip();
                showMyDialog();
                break;
        }
    }

    private void getTip() {
        person = et_person.getText().toString();
        things = et_things.getText().toString();
        time = et_time.getText().toString();

        tip = "您确定删除";
        if (!time.equals("")) {
            tip += "“" + time + "”之前的";
        }
        if (!person.equals("")) {
            tip += "“" + person + "”的";
        }
        if (!things.equals("")) {
            tip += "“" + things + "”的";
        }
        tip += "所有账单吗?";
    }

    private void showMyDialog() {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("提示");
        mDialog.setMessage(tip + "\n" + "\n" + "是否预览将要删除的账单?"+"\n");
        mDialog.setPositiveButton("预览", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                setTag();

                Intent intent = new Intent(DeleteCash.this, QueryResultListActivity.class);

                Bundle bundle = new Bundle();

                QueryRequirement requirement = new QueryRequirement();
                requirement.setPerson(person);
                requirement.setThings(things);
                requirement.setStart_time("");
                requirement.setEnd_time(time);
                requirement.setMin_money("");
                requirement.setMax_money("");
                requirement.setTag(tag);

                bundle.putSerializable("requirement",requirement);
                intent.putExtras(bundle);
                intent.putExtra("from_delete_cash", 1);
                startActivity(intent);
            }
        });
        mDialog.setNeutralButton("直接删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doDeleteCash();
                Toast.makeText(DeleteCash.this, "删除成功", Toast.LENGTH_SHORT).show();
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

    private void doDeleteCash() {

        StringBuilder sql = new StringBuilder("delete from cash where 1=1");
        if (!person.equals("")){
            String str = " and person = '"+person+"'";
            sql.append(str);
        }
        if (!things.equals("")){
            String str = " and things = '"+things+"'";
            sql.append(str);
        }
        if (!time.equals("")){
            String str = " and time <= '"+time+"'";
            sql.append(str);
        }
        Log.v("--- delete directly ---",sql+"");
        db.execSQL(String.valueOf(sql));
    }

    private void setTag() {
        if (cb_person.isChecked()){
            tag[0]=true;
        }
        if (cb_things.isChecked()){
            tag[1]=true;
        }
        if (cb_time.isChecked()){
            tag[3]=true;
        }
    }

    //读取存储的所有 人物、事件
    private void initListData() {
        all_person = new ArrayList<>();
        all_things = new ArrayList<>();

        Cursor cursor_1 = db.query(true, "cash", new String[]{"person"}, null, null, null, null, "_id ASC", null);
        while (cursor_1.moveToNext()) {
            all_person.add(cursor_1.getString(cursor_1.getColumnIndex("person")));
        }
        Cursor cursor_2 = db.query(true, "cash", new String[]{"things"}, null, null, null, null, "_id ASC", null);
        while (cursor_2.moveToNext()) {
            all_things.add(cursor_2.getString(cursor_2.getColumnIndex("things")));
        }
    }

    //下拉列表项点击事件 接口
    @Override
    public void onItemClick(int list_type, int position) {
        if (list_type == 1) {
            person = all_person.get(position);
            et_person.setText(person);
            cb_person.setChecked(true);
        } else if (list_type == 2) {
            things = all_things.get(position);
            et_things.setText(things);
            cb_things.setChecked(true);
        }
    }

    //节点时间的值 接口
    @Override
    public void getStartTime(int year, int month, int day) {
        if (month / 10 == 0 && day / 10 == 0) {
            time = year + "-0" + month + "-0" + day;
        } else if (month / 10 == 0 && day / 10 != 0) {
            time = year + "-0" + month + "-" + day;
        } else if (month / 10 != 0 && day / 10 == 0) {
            time = year + "-" + month + "-0" + day;
        } else {
            time = year + "-" + month + "-" + day;
        }
        et_time.setText(time);
        cb_time.setChecked(true);
    }
}
